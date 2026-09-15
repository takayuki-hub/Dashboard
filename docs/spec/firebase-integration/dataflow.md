# Firebaseの導入 データフロー設計

**作成日**: 2026-09-12
**関連要件定義**: [requirements.md](requirements.md)
**関連アーキテクチャ**: [architecture.md](architecture.md)

## 概要

Firebase Analytics導入における各種イベントのデータフロー図を定義します。画面遷移、ユーザー操作、エラー・例外、アプリライフサイクルの4つのカテゴリに分けて詳細なフローを示します。

## 1. 画面遷移イベントのデータフロー（REQ-201）

### フロー図

```
┌──────────────────────────────────────────────────────────────┐
│ UI Layer (Composable)                                        │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ HomeScreen.kt                                          │   │
│ │ WeatherScreen.kt                                       │   │
│ │ NewsScreen.kt                                          │   │
│ │ TaskScreen.kt                                          │   │
│ └────────────────────────────────────────────────────────┘   │
│                          │                                    │
│                          │ LaunchedEffect { ... }             │
│                          ↓                                    │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ ViewModel.onScreenVisible()                            │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ viewModelScope.launch
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ ViewModel Layer                                              │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ HomeViewModel / WeatherViewModel / etc.                │   │
│ │                                                        │   │
│ │ fun onScreenVisible() {                                │   │
│ │     viewModelScope.launch {                            │   │
│ │         analyticsRepository.logScreenView(             │   │
│ │             screenName = AnalyticsEvents.SCREEN_HOME   │   │
│ │         )                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ suspend fun呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Repository Layer (:core:analytics)                           │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ AnalyticsRepositoryImpl                                │   │
│ │                                                        │   │
│ │ override suspend fun logScreenView(                    │   │
│ │     screenName: String                                 │   │
│ │ ) {                                                    │   │
│ │     withContext(Dispatchers.IO) {                      │   │
│ │         val params = mapOf(                            │   │
│ │             AnalyticsParams.SCREEN_NAME to screenName, │   │
│ │             AnalyticsParams.SCREEN_CLASS to screenName │   │
│ │         )                                              │   │
│ │         firebaseAnalytics.logEvent(                    │   │
│ │             FirebaseAnalytics.Event.SCREEN_VIEW,       │   │
│ │             bundleOf(*params.toList().toTypedArray())  │   │
│ │         )                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ SDK呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Firebase Analytics SDK                                       │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ FirebaseAnalytics.logEvent(...)                        │   │
│ │ - イベント名: "screen_view"                            │   │
│ │ - パラメータ: { screen_name, screen_class }            │   │
│ │ - バックグラウンドでFirebase Consoleに送信              │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### データ例

**画面**: ホーム画面
**イベント名**: `screen_view`
**パラメータ**:
```json
{
  "screen_name": "home_screen",
  "screen_class": "home_screen"
}
```

**関連要件**: REQ-201, TC-201-01~TC-201-04

---

## 2. ユーザー操作イベントのデータフロー（REQ-202, REQ-203）

### フロー図

```
┌──────────────────────────────────────────────────────────────┐
│ UI Layer (Composable)                                        │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ Button(onClick = { viewModel.onTaskCreateClicked() })  │   │
│ │ NewsItem(onClick = { viewModel.onArticleClicked(id) }) │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ onClick callback
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ ViewModel Layer                                              │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ TaskViewModel                                          │   │
│ │                                                        │   │
│ │ fun onTaskCreateClicked() {                            │   │
│ │     viewModelScope.launch {                            │   │
│ │         analyticsRepository.logEvent(                  │   │
│ │             eventName = AnalyticsEvents.               │   │
│ │                         TASK_CREATE_CLICKED,           │   │
│ │             params = emptyMap()                        │   │
│ │         )                                              │   │
│ │         // ビジネスロジック実行...                      │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ │                                                        │   │
│ │ fun onArticleClicked(articleId: String) {              │   │
│ │     viewModelScope.launch {                            │   │
│ │         analyticsRepository.logEvent(                  │   │
│ │             eventName = AnalyticsEvents.               │   │
│ │                         NEWS_ARTICLE_CLICKED,          │   │
│ │             params = mapOf(                            │   │
│ │                 AnalyticsParams.ARTICLE_ID to articleId│   │
│ │             )                                          │   │
│ │         )                                              │   │
│ │         // ビジネスロジック実行...                      │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ suspend fun呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Repository Layer (:core:analytics)                           │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ AnalyticsRepositoryImpl                                │   │
│ │                                                        │   │
│ │ override suspend fun logEvent(                         │   │
│ │     eventName: String,                                 │   │
│ │     params: Map<String, Any>                           │   │
│ │ ) {                                                    │   │
│ │     withContext(Dispatchers.IO) {                      │   │
│ │         firebaseAnalytics.logEvent(                    │   │
│ │             eventName,                                 │   │
│ │             bundleOf(*params.toList().toTypedArray())  │   │
│ │         )                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ SDK呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Firebase Analytics SDK                                       │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ FirebaseAnalytics.logEvent(...)                        │   │
│ │ - イベント名: "task_create_clicked"                    │   │
│ │ - パラメータ: {}                                       │   │
│ │ - バックグラウンドでFirebase Consoleに送信              │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### データ例

#### タスク作成ボタンクリック
**イベント名**: `task_create_clicked`
**パラメータ**: `{}`

#### ニュース記事クリック
**イベント名**: `news_article_clicked`
**パラメータ**:
```json
{
  "article_id": "article_12345"
}
```

**関連要件**: REQ-202, REQ-203, TC-202-01~TC-202-02

---

## 3. エラー・例外イベントのデータフロー（REQ-204, REQ-205）

### フロー図

```
┌──────────────────────────────────────────────────────────────┐
│ ViewModel Layer                                              │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ WeatherViewModel                                       │   │
│ │                                                        │   │
│ │ fun fetchWeather() {                                   │   │
│ │     viewModelScope.launch {                            │   │
│ │         weatherRepository.getWeather()                 │   │
│ │             .onSuccess { data -> /* ... */ }           │   │
│ │             .onFailure { exception ->                  │   │
│ │                 analyticsRepository.logEvent(          │   │
│ │                     eventName = AnalyticsEvents.       │   │
│ │                                 WEATHER_FETCH_FAILED,  │   │
│ │                     params = mapOf(                    │   │
│ │                         AnalyticsParams.ERROR_TYPE to  │   │
│ │                             exception::class.simpleName,│  │
│ │                         AnalyticsParams.ERROR_MESSAGE  │   │
│ │                             to exception.message?.      │   │
│ │                                take(100) // 100文字制限 │   │
│ │                     )                                  │   │
│ │                 )                                      │   │
│ │             }                                          │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ suspend fun呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Repository Layer (:core:analytics)                           │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ AnalyticsRepositoryImpl                                │   │
│ │                                                        │   │
│ │ override suspend fun logEvent(                         │   │
│ │     eventName: String,                                 │   │
│ │     params: Map<String, Any>                           │   │
│ │ ) {                                                    │   │
│ │     withContext(Dispatchers.IO) {                      │   │
│ │         // 個人情報（PII）を除外する検証ロジック        │   │
│ │         val sanitizedParams = params.filterNot { (k, v) ->│  │
│ │             // メールアドレス、電話番号等を除外        │   │
│ │             k.contains("email", ignoreCase = true) ||  │   │
│ │             k.contains("phone", ignoreCase = true)     │   │
│ │         }                                              │   │
│ │         firebaseAnalytics.logEvent(                    │   │
│ │             eventName,                                 │   │
│ │             bundleOf(                                  │   │
│ │                 *sanitizedParams.toList().toTypedArray()│  │
│ │             )                                          │   │
│ │         )                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ SDK呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Firebase Analytics SDK                                       │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ FirebaseAnalytics.logEvent(...)                        │   │
│ │ - イベント名: "weather_fetch_failed"                   │   │
│ │ - パラメータ: { error_type, error_message }            │   │
│ │ - バックグラウンドでFirebase Consoleに送信              │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### データ例

**イベント名**: `weather_fetch_failed`
**パラメータ**:
```json
{
  "error_type": "IOException",
  "error_message": "Unable to resolve host \"api.openweathermap.org\": No address associated with hostname"
}
```

**注意**: エラーメッセージは100文字に制限し、個人情報を含まないことを確認します。

**関連要件**: REQ-204, REQ-205, NFR-101

---

## 4. アプリライフサイクルイベントのデータフロー（REQ-206, REQ-207）

### フロー図

```
┌──────────────────────────────────────────────────────────────┐
│ Application Layer                                            │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ MainApplication.onCreate()                             │   │
│ │                                                        │   │
│ │ override fun onCreate() {                              │   │
│ │     super.onCreate()                                   │   │
│ │                                                        │   │
│ │     // Firebase Analytics設定                          │   │
│ │     if (BuildConfig.DEBUG) {                           │   │
│ │         firebaseAnalytics.                             │   │
│ │             setAnalyticsCollectionEnabled(false)       │   │
│ │     } else {                                           │   │
│ │         firebaseAnalytics.                             │   │
│ │             setAnalyticsCollectionEnabled(true)        │   │
│ │     }                                                  │   │
│ │                                                        │   │
│ │     // アプリ起動イベント記録                           │   │
│ │     lifecycleScope.launch {                            │   │
│ │         analyticsRepository.logEvent(                  │   │
│ │             eventName = AnalyticsEvents.APP_LAUNCHED,  │   │
│ │             params = mapOf(                            │   │
│ │                 AnalyticsParams.APP_VERSION to         │   │
│ │                     BuildConfig.VERSION_NAME           │   │
│ │             )                                          │   │
│ │         )                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ suspend fun呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Activity Layer                                               │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ MainActivity.onCreate()                                │   │
│ │                                                        │   │
│ │ override fun onCreate(savedInstanceState: Bundle?) {   │   │
│ │     super.onCreate(savedInstanceState)                 │   │
│ │                                                        │   │
│ │     lifecycleScope.launch {                            │   │
│ │         analyticsRepository.logEvent(                  │   │
│ │             eventName = AnalyticsEvents.               │   │
│ │                         SESSION_STARTED,               │   │
│ │             params = emptyMap()                        │   │
│ │         )                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ │                                                        │   │
│ │ override fun onStop() {                                │   │
│ │     super.onStop()                                     │   │
│ │                                                        │   │
│ │     lifecycleScope.launch {                            │   │
│ │         analyticsRepository.logEvent(                  │   │
│ │             eventName = AnalyticsEvents.               │   │
│ │                         SESSION_ENDED,                 │   │
│ │             params = emptyMap()                        │   │
│ │         )                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ suspend fun呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Repository Layer (:core:analytics)                           │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ AnalyticsRepositoryImpl.logEvent(...)                  │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ SDK呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Firebase Analytics SDK                                       │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ FirebaseAnalytics.logEvent(...)                        │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### データ例

#### アプリ起動イベント
**イベント名**: `app_launched`
**パラメータ**:
```json
{
  "app_version": "1.0.0"
}
```

#### セッション開始イベント
**イベント名**: `session_started`
**パラメータ**: `{}`

#### セッション終了イベント
**イベント名**: `session_ended`
**パラメータ**: `{}`

**関連要件**: REQ-206, REQ-207

---

## 5. ユーザープロパティ設定のデータフロー（REQ-211, REQ-212）

### フロー図

```
┌──────────────────────────────────────────────────────────────┐
│ Application Layer                                            │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ MainApplication.onCreate()                             │   │
│ │                                                        │   │
│ │ override fun onCreate() {                              │   │
│ │     super.onCreate()                                   │   │
│ │                                                        │   │
│ │     lifecycleScope.launch {                            │   │
│ │         // アプリバージョンをユーザープロパティに設定    │   │
│ │         analyticsRepository.setUserProperty(           │   │
│ │             name = "app_version",                      │   │
│ │             value = BuildConfig.VERSION_NAME           │   │
│ │         )                                              │   │
│ │                                                        │   │
│ │         // 初回起動判定（SharedPreferencesから取得）     │   │
│ │         val isFirstLaunch = prefs.getBoolean(          │   │
│ │             "is_first_launch", true                    │   │
│ │         )                                              │   │
│ │         if (isFirstLaunch) {                           │   │
│ │             analyticsRepository.setUserProperty(       │   │
│ │                 name = "first_open_time",              │   │
│ │                 value = System.currentTimeMillis()     │   │
│ │                         .toString()                    │   │
│ │             )                                          │   │
│ │             prefs.edit()                               │   │
│ │                 .putBoolean("is_first_launch", false)  │   │
│ │                 .apply()                               │   │
│ │         }                                              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ suspend fun呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Repository Layer (:core:analytics)                           │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ AnalyticsRepositoryImpl                                │   │
│ │                                                        │   │
│ │ override suspend fun setUserProperty(                  │   │
│ │     name: String,                                      │   │
│ │     value: String                                      │   │
│ │ ) {                                                    │   │
│ │     withContext(Dispatchers.IO) {                      │   │
│ │         firebaseAnalytics.setUserProperty(name, value) │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ SDK呼び出し
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Firebase Analytics SDK                                       │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ FirebaseAnalytics.setUserProperty(...)                 │   │
│ │ - プロパティ名: "app_version"                          │   │
│ │ - プロパティ値: "1.0.0"                                │   │
│ │ - Firebase Consoleのユーザープロパティに反映            │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### データ例

**ユーザープロパティ**:
```json
{
  "app_version": "1.0.0",
  "first_open_time": "1726099200000"
}
```

**関連要件**: REQ-211, REQ-212, REQ-213

---

## 6. デバッグビルドでのAnalytics無効化フロー（REQ-301）

### フロー図

```
┌──────────────────────────────────────────────────────────────┐
│ MainApplication.onCreate()                                   │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ override fun onCreate() {                              │   │
│ │     super.onCreate()                                   │   │
│ │                                                        │   │
│ │     if (BuildConfig.DEBUG) { ←─────────────────┐      │   │
│ │         firebaseAnalytics.                     │      │   │
│ │             setAnalyticsCollectionEnabled(false)│      │   │
│ │         Log.d("MainApplication",               │      │   │
│ │             "Firebase Analytics disabled")     │      │   │
│ │     } else { ←─────────────────────────────────┘      │   │
│ │         firebaseAnalytics.                             │   │
│ │             setAnalyticsCollectionEnabled(true)        │   │
│ │         Log.d("MainApplication",                       │   │
│ │             "Firebase Analytics enabled")              │   │
│ │     }                                                  │   │
│ │ }                                                      │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                           │
                           │ デバッグビルド判定
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ Firebase Analytics SDK                                       │
│ ┌────────────────────────────────────────────────────────┐   │
│ │ setAnalyticsCollectionEnabled(false)                   │   │
│ │ → 全てのイベント記録が無効化                            │   │
│ │ → Firebase Consoleにイベントが送信されない              │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### Logcat出力例

**デバッグビルド**:
```
D/MainApplication: Firebase Analytics disabled in debug build
```

**リリースビルド**:
```
D/MainApplication: Firebase Analytics enabled in release build
```

**関連要件**: REQ-301, REQ-302, TC-301-01, TC-301-E01

---

## 7. エッジケース対応

### パラメータ数制限（EDGE-101）

**制限**: 1イベントあたり最大25個のパラメータ

**対応**:
- `AnalyticsRepository.logEvent()`でパラメータ数を検証
- 25個を超える場合は警告ログを出力し、最初の25個のみ記録

```kotlin
override suspend fun logEvent(
    eventName: String,
    params: Map<String, Any>
) {
    withContext(Dispatchers.IO) {
        if (params.size > 25) {
            Log.w("AnalyticsRepository",
                "Event $eventName has ${params.size} params (max 25)")
        }
        val limitedParams = params.toList().take(25).toMap()
        firebaseAnalytics.logEvent(
            eventName,
            bundleOf(*limitedParams.toList().toTypedArray())
        )
    }
}
```

### イベント名長制限（EDGE-102）

**制限**: イベント名は最大40文字

**対応**:
- `AnalyticsEvents`オブジェクトの全定数を40文字以内に制限
- コンパイル時にチェック可能（定数なので）

### パラメータ名長制限（EDGE-103）

**制限**: パラメータ名は最大40文字

**対応**:
- `AnalyticsParams`オブジェクトの全定数を40文字以内に制限
- コンパイル時にチェック可能（定数なので）

---

## 8. データフローサマリー

| フローカテゴリ | 起点 | 経由 | 終点 | 関連要件 |
|---------------|------|------|------|----------|
| 画面遷移 | Composable | ViewModel → Repository | Firebase SDK | REQ-201 |
| ユーザー操作 | UI onClick | ViewModel → Repository | Firebase SDK | REQ-202, REQ-203 |
| エラー・例外 | Repository.onFailure | ViewModel → Analytics Repository | Firebase SDK | REQ-204, REQ-205 |
| ライフサイクル | Application/Activity | lifecycleScope → Repository | Firebase SDK | REQ-206, REQ-207 |
| ユーザープロパティ | Application.onCreate | lifecycleScope → Repository | Firebase SDK | REQ-211, REQ-212 |
| デバッグ設定 | Application.onCreate | BuildConfig.DEBUG判定 | FirebaseAnalytics | REQ-301 |

---

## 関連文書

- **要件定義書**: [requirements.md](requirements.md)
- **アーキテクチャ設計**: [architecture.md](architecture.md)
- **インターフェース定義**: [interfaces.kt](interfaces.kt)
- **受け入れ基準**: [acceptance-criteria.md](acceptance-criteria.md)

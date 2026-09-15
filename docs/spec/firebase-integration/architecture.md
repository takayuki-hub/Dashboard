# Firebaseの導入 アーキテクチャ設計

**作成日**: 2026-09-12
**関連要件定義**: [requirements.md](requirements.md)
**関連ヒアリング**: [design-interview.md](design-interview.md)
**関連コンテキスト**: [note.md](note.md)

## 概要

Firebase Analytics導入のためのアーキテクチャ設計を定義します。既存のマルチモジュール構成に`:core:analytics`モジュールを追加し、全featureモジュールから統一的にAnalytics機能を利用できるようにします。

## モジュール構成

### 新規作成モジュール

#### :core:analytics

**責務**: Firebase Analyticsのイベント記録機能を提供する共通モジュール

**パッケージ構成**:
```
com.github.takayuki_hub.dashboard.core.analytics
├── constants
│   ├── AnalyticsEvents.kt        # イベント名定数
│   └── AnalyticsParams.kt        # パラメータ名定数
├── di
│   └── AnalyticsModule.kt        # Hilt DIモジュール
└── repository
    ├── AnalyticsRepository.kt     # Repositoryインターフェース
    └── AnalyticsRepositoryImpl.kt # Repository実装
```

**依存関係**:
- Firebase BOM (34.19.0)
- Firebase Analytics
- Hilt (2.60.1)
- Kotlin Coroutines
- `implementation(platform(libs.firebase.bom))`
- `implementation(libs.firebase.analytics)`
- `implementation(libs.hilt.android)`
- `ksp(libs.hilt.compiler)`

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.github.takayuki_hub.dashboard.core.analytics"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
```

### 既存モジュールの変更

#### :app

**変更内容**:
- `MainApplication.onCreate()`にFirebase Analytics初期化設定を追加
- デバッグビルド時のAnalytics無効化ロジックを追加

**追加依存関係**:
- `:core:analytics`モジュールへの依存

#### :feature:home, :feature:weather, :feature:news, :feature:task

**変更内容**:
- 各featureモジュールから`:core:analytics`への依存を追加
- ViewModelで`AnalyticsRepository`をインジェクト
- 画面遷移時・ユーザー操作時にイベント記録

**追加依存関係**:
```kotlin
dependencies {
    implementation(project(":core:analytics"))
}
```

## レイヤー構成

### データフロー

```
┌─────────────────────────────────────────────────┐
│ :app モジュール                                   │
│ ┌─────────────────────────────────────────────┐ │
│ │ MainApplication                             │ │
│ │ - Firebase Analytics初期化設定               │ │
│ │ - デバッグビルド時の無効化                    │ │
│ └─────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
                      │
                      │ 依存
                      ↓
┌─────────────────────────────────────────────────┐
│ :feature:* モジュール                            │
│ ┌─────────────────────────────────────────────┐ │
│ │ ViewModel                                   │ │
│ │ - AnalyticsRepositoryをインジェクト          │ │
│ │ - viewModelScope.launch { ... }             │ │
│ │ - イベント記録ロジック                       │ │
│ └─────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
                      │
                      │ インジェクト
                      ↓
┌─────────────────────────────────────────────────┐
│ :core:analytics モジュール                       │
│ ┌─────────────────────────────────────────────┐ │
│ │ AnalyticsRepository (interface)             │ │
│ │ - suspend fun logEvent(...)                 │ │
│ │ - suspend fun setUserProperty(...)          │ │
│ │ - suspend fun logScreenView(...)            │ │
│ └─────────────────────────────────────────────┘ │
│                     ↑                           │
│                     │ 実装                       │
│ ┌─────────────────────────────────────────────┐ │
│ │ AnalyticsRepositoryImpl                     │ │
│ │ - FirebaseAnalyticsインスタンスをDI          │ │
│ │ - withContext(Dispatchers.IO) { ... }       │ │
│ └─────────────────────────────────────────────┘ │
│                     │                           │
│                     │ Hilt DI                    │
│ ┌─────────────────────────────────────────────┐ │
│ │ AnalyticsModule (@Module, @InstallIn)       │ │
│ │ - provideFirebaseAnalytics()                │ │
│ │ - bindAnalyticsRepository()                 │ │
│ └─────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
                      │
                      │ SDK呼び出し
                      ↓
┌─────────────────────────────────────────────────┐
│ Firebase Analytics SDK                          │
│ - FirebaseAnalytics.logEvent()                  │
│ - FirebaseAnalytics.setUserProperty()           │
│ - FirebaseAnalytics.setAnalyticsCollectionEnabled() │
└─────────────────────────────────────────────────┘
```

## 依存性注入（Hilt）

### AnalyticsModuleの設計

**ファイル**: `core/analytics/src/main/java/com/github/takayuki_hub/dashboard/core/analytics/di/AnalyticsModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        impl: AnalyticsRepositoryImpl
    ): AnalyticsRepository
}
```

### インジェクションパターン

#### ViewModelへのインジェクション例

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    // 他の依存関係...
) : ViewModel() {

    fun onScreenVisible() {
        viewModelScope.launch {
            analyticsRepository.logScreenView(
                screenName = AnalyticsEvents.SCREEN_HOME
            )
        }
    }

    fun onTaskCreateClicked() {
        viewModelScope.launch {
            analyticsRepository.logEvent(
                eventName = AnalyticsEvents.TASK_CREATE_CLICKED,
                params = emptyMap()
            )
        }
    }
}
```

## デバッグ設定の実装

### MainApplicationでの実装

**ファイル**: `app/src/main/java/com/github/takayuki_hub/dashboard/MainApplication.kt`

```kotlin
@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        // デバッグビルド時はFirebase Analyticsを無効化
        if (BuildConfig.DEBUG) {
            firebaseAnalytics.setAnalyticsCollectionEnabled(false)
            android.util.Log.d(
                "MainApplication",
                "Firebase Analytics disabled in debug build"
            )
        } else {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            android.util.Log.d(
                "MainApplication",
                "Firebase Analytics enabled in release build"
            )
        }
    }
}
```

**注意**: `FirebaseAnalytics`インスタンスはHiltでインジェクトするため、`@Inject`アノテーションを使用します。

## イベント定数の管理

### AnalyticsEventsオブジェクト

**ファイル**: `core/analytics/src/main/java/com/github/takayuki_hub/dashboard/core/analytics/constants/AnalyticsEvents.kt`

```kotlin
object AnalyticsEvents {
    // 画面遷移イベント
    const val SCREEN_HOME = "home_screen"
    const val SCREEN_WEATHER = "weather_screen"
    const val SCREEN_NEWS = "news_screen"
    const val SCREEN_TASK = "task_screen"

    // ユーザー操作イベント
    const val TASK_CREATE_CLICKED = "task_create_clicked"
    const val TASK_EDITED = "task_edited"
    const val TASK_DELETED = "task_deleted"
    const val NEWS_ARTICLE_CLICKED = "news_article_clicked"
    const val WEATHER_REFRESHED = "weather_refreshed"

    // エラーイベント
    const val NETWORK_ERROR = "network_error"
    const val WEATHER_FETCH_FAILED = "weather_fetch_failed"
    const val NEWS_FETCH_FAILED = "news_fetch_failed"
    const val EXCEPTION_OCCURRED = "exception_occurred"

    // アプリライフサイクルイベント
    const val APP_LAUNCHED = "app_launched"
    const val SESSION_STARTED = "session_started"
    const val SESSION_ENDED = "session_ended"
}
```

### AnalyticsParamsオブジェクト

**ファイル**: `core/analytics/src/main/java/com/github/takayuki_hub/dashboard/core/analytics/constants/AnalyticsParams.kt`

```kotlin
object AnalyticsParams {
    // Firebase標準パラメータ
    const val SCREEN_NAME = "screen_name"
    const val SCREEN_CLASS = "screen_class"

    // カスタムパラメータ
    const val TASK_ID = "task_id"
    const val ARTICLE_ID = "article_id"
    const val ERROR_TYPE = "error_type"
    const val ERROR_MESSAGE = "error_message"
    const val APP_VERSION = "app_version"
    const val NAVIGATION_DESTINATION = "navigation_destination"
}
```

## セキュリティ考慮事項

### 個人情報の除外（NFR-101）

- イベントパラメータに個人識別情報（PII）を含めない
- メールアドレス、電話番号、氏名などは記録しない
- タスクの内容やニュース記事のタイトルは記録しない（IDのみ）

### google-services.jsonの保護（REQ-401）

- `.gitignore`に`app/google-services.json`を追加
- `git rm --cached app/google-services.json`で既存追跡を削除
- `google-services.json.sample`をプレースホルダーとして作成

## パフォーマンス考慮事項

### UIスレッドブロックの回避（NFR-001）

- 全てのAnalyticsメソッドを`suspend fun`として定義
- `withContext(Dispatchers.IO)`でバックグラウンドスレッド実行
- ViewModelから`viewModelScope.launch`で非同期呼び出し

### Firebase Analytics SDKの最適化

- Firebase Analytics SDKは内部で非ブロッキング処理を行う
- ネットワーク未接続時は自動的にローカルキューに保存（EDGE-001）
- 送信失敗時は自動リトライ（EDGE-002）

## テスト戦略

### ユニットテスト

- `AnalyticsRepositoryImpl`のモックを使用したViewModelテスト
- `MockK`によるFirebaseAnalyticsインスタンスのモック化
- Coroutinesテスト（`runTest`使用）

### インストルメンテーションテスト

- Firebase Analytics DebugViewでのイベント記録確認
- `adb shell setprop debug.firebase.analytics.app com.github.takayuki_hub.dashboard`

## 非機能要件の対応

| 要件ID | 対応方法 |
|--------|----------|
| NFR-001 | suspend関数 + withContext(Dispatchers.IO) |
| NFR-002 | Firebase SDK内部で500ms以内に完了 |
| NFR-101 | イベントパラメータにPII含めない設計 |
| NFR-201 | AnalyticsEvents/AnalyticsParams objectで定数管理 |
| NFR-202 | MockKによるAnalyticsRepositoryモック提供 |
| NFR-301 | Min SDK 24対応 |
| NFR-302 | Firebase BOM 34.19.0使用 |

## Edgeケースの対応

| 要件ID | 対応方法 |
|--------|----------|
| EDGE-001 | Firebase SDK内部で自動キューイング |
| EDGE-002 | Firebase SDK内部で自動リトライ |
| EDGE-101 | パラメータ数25個制限をドキュメント化 |
| EDGE-102 | イベント名40文字制限を定数で遵守 |
| EDGE-103 | パラメータ名40文字制限を定数で遵守 |

## 関連文書

- **要件定義書**: [requirements.md](requirements.md)
- **ユーザストーリー**: [user-stories.md](user-stories.md)
- **受け入れ基準**: [acceptance-criteria.md](acceptance-criteria.md)
- **データフロー**: [dataflow.md](dataflow.md)
- **インターフェース定義**: [interfaces.kt](interfaces.kt)
- **モジュール依存関係**: [module-dependencies.md](module-dependencies.md)

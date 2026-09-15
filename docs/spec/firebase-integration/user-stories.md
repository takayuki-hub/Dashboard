# Firebaseの導入 ユーザストーリー

**作成日**: 2026-09-12 00:58:26
**関連要件定義**: [requirements.md](requirements.md)
**ヒアリング記録**: [interview-record.md](interview-record.md)

**【信頼性レベル凡例】**:
- 🔵 **青信号**: PRD・EARS要件定義書・設計文書・ユーザヒアリングを参考にした確実なストーリー
- 🟡 **黄信号**: PRD・EARS要件定義書・設計文書・ユーザヒアリングから妥当な推測によるストーリー
- 🔴 **赤信号**: PRD・EARS要件定義書・設計文書・ユーザヒアリングにない推測によるストーリー

---

## エピック1: コアモジュール構築

### ストーリー 1.1: :core:analyticsモジュールの作成 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q3、既存のモジュール構成より*

**私は** 開発者 **として**
**`:core:analytics`モジュールを作成したい**
**そうすることで** 全featureモジュールから統一的にAnalytics機能を利用できる

**関連要件**: REQ-101, REQ-105

**詳細シナリオ**:
1. `settings.gradle.kts`に`:core:analytics`を追加
2. `core/analytics/build.gradle.kts`を作成し、必要な依存関係を定義
3. Firebase BOMとFirebase Analyticsの依存を追加
4. Hilt、Kotlin Serializationなどの依存を追加
5. パッケージ構造`com.github.takayuki_hub.dashboard.core.analytics`を作成

**前提条件**:
- `gradle/libs.versions.toml`にFirebase関連の定義が存在する
- 既存の`:core`モジュール構成を理解している

**制約事項**:
- アーキテクチャルールに従い、`feature:*`モジュールへの依存は禁止
- Hilt DIパターンに従う

**優先度**: Must Have

---

### ストーリー 1.2: AnalyticsRepositoryインターフェースの定義 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q6、既存のRepositoryパターンより*

**私は** 開発者 **として**
**`AnalyticsRepository`インターフェースを定義したい**
**そうすることで** featureモジュールから統一的なAPIでAnalyticsを利用できる

**関連要件**: REQ-102, REQ-201~REQ-213

**詳細シナリオ**:
1. `core/analytics/src/main/java/com/github/takayuki_hub/dashboard/core/analytics/repository/AnalyticsRepository.kt`を作成
2. `logEvent(eventName: String, params: Map<String, Any>)`メソッドを定義
3. `setUserProperty(name: String, value: String)`メソッドを定義
4. `logScreenView(screenName: String)`メソッドを定義
5. Kotlin Coroutinesを使用した非同期APIとして設計

**前提条件**:
- `:core:analytics`モジュールが作成済み
- 既存の`:core:data/repository`パターンを参考にできる

**優先度**: Must Have

---

### ストーリー 1.3: AnalyticsRepositoryImplの実装 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q6、Firebase Analytics APIより*

**私は** 開発者 **として**
**`AnalyticsRepositoryImpl`を実装したい**
**そうすることで** 実際にFirebase Analyticsにイベントを送信できる

**関連要件**: REQ-103, REQ-104

**詳細シナリオ**:
1. `FirebaseAnalytics`インスタンスをHiltでインジェクト
2. `logEvent`メソッドでFirebase Analyticsの`logEvent`を呼び出し
3. `setUserProperty`メソッドでFirebase Analyticsの`setUserProperty`を呼び出し
4. `logScreenView`メソッドで画面遷移イベントを記録
5. Hilt `@Singleton`スコープで実装

**前提条件**:
- `AnalyticsRepository`インターフェースが定義済み
- Firebase Analyticsが自動初期化されている

**制約事項**:
- UIスレッドをブロックしない実装
- FirebaseAnalyticsインスタンスはApplicationContextから取得

**優先度**: Must Have

---

## エピック2: イベントトラッキング実装

### ストーリー 2.1: 画面遷移トラッキングの実装 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q4、既存のナビゲーション実装より*

**私は** アプリ開発者 **として**
**各画面の表示時に自動的にAnalyticsイベントを記録したい**
**そうすることで** ユーザーの画面遷移パターンを分析できる

**関連要件**: REQ-201

**詳細シナリオ**:
1. 各feature の `Route.kt` で `AnalyticsRepository` をインジェクト
2. `Composable` の `LaunchedEffect` で画面表示時にイベント記録
3. 画面名（例: "home_screen", "weather_screen"）をパラメータとして記録
4. ナビゲーション先情報も追加パラメータとして記録

**前提条件**:
- `:core:analytics`モジュールが各featureモジュールから利用可能
- 既存のナビゲーション構造を理解している

**優先度**: Must Have

---

### ストーリー 2.2: ユーザー操作トラッキングの実装 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q4より*

**私は** アプリ開発者 **として**
**ボタンクリックやタスク操作時にAnalyticsイベントを記録したい**
**そうすることで** ユーザーの操作パターンを分析できる

**関連要件**: REQ-202, REQ-203

**詳細シナリオ**:
1. タスク作成ボタンクリック時に`task_create_clicked`イベントを記録
2. タスク編集時に`task_edited`イベントを記録（タスクIDをパラメータに含む）
3. タスク削除時に`task_deleted`イベントを記録
4. ニュース記事クリック時に`news_article_clicked`イベントを記録
5. 天気データ更新時に`weather_refreshed`イベントを記録

**前提条件**:
- `:feature:task`、`:feature:news`、`:feature:weather`が`:core:analytics`に依存
- ViewModelから`AnalyticsRepository`をインジェクト可能

**優先度**: Must Have

---

### ストーリー 2.3: エラー・例外トラッキングの実装 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q4より*

**私は** アプリ開発者 **として**
**エラーや例外発生時にAnalyticsイベントを記録したい**
**そうすることで** アプリの品質問題を早期に発見できる

**関連要件**: REQ-204, REQ-205

**詳細シナリオ**:
1. ネットワークエラー時に`network_error`イベントを記録
2. 天気データ取得失敗時に`weather_fetch_failed`イベントを記録
3. ニュース取得失敗時に`news_fetch_failed`イベントを記録
4. 例外発生時に`exception_occurred`イベントを記録（例外タイプをパラメータに含む）
5. エラーメッセージの一部（個人情報を除く）をパラメータに含める

**前提条件**:
- Repository層でエラーハンドリングが実装されている
- ViewModelでエラー状態を管理している

**制約事項**:
- 個人情報（PII）を含めない
- スタックトレースは記録しない（プライバシー保護）

**優先度**: Should Have

---

### ストーリー 2.4: アプリライフサイクルトラッキングの実装 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q4より*

**私は** アプリ開発者 **として**
**アプリ起動・終了時にAnalyticsイベントを記録したい**
**そうすることで** アプリの利用頻度や利用時間を分析できる

**関連要件**: REQ-206, REQ-207

**詳細シナリオ**:
1. `MainApplication.onCreate`でアプリ起動イベントを記録
2. `MainActivity.onCreate`でセッション開始イベントを記録
3. `MainActivity.onStop`でセッション終了イベントを記録
4. アプリバージョン情報をパラメータに含める

**前提条件**:
- `MainApplication`と`MainActivity`が`@HiltAndroidApp`、`@AndroidEntryPoint`でアノテート済み
- `AnalyticsRepository`がApplicationスコープでインジェクト可能

**優先度**: Should Have

---

## エピック3: ユーザープロパティ実装

### ストーリー 3.1: ユーザープロパティ設定機能の実装 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q8より*

**私は** アプリ開発者 **として**
**ユーザープロパティを設定できるようにしたい**
**そうすることで** ユーザーセグメント別の分析ができる

**関連要件**: REQ-211, REQ-212, REQ-213

**詳細シナリオ**:
1. `AnalyticsRepository.setUserProperty`メソッドを実装
2. アプリ初回起動時に`first_open_time`プロパティを設定
3. アプリバージョンを`app_version`プロパティとして設定
4. カスタムプロパティ（例: `preferred_theme`）を設定可能にする

**前提条件**:
- `AnalyticsRepository`が実装済み
- SharedPreferencesまたはDataStoreでローカル保存可能

**優先度**: Should Have

---

## エピック4: デバッグ設定

### ストーリー 4.1: デバッグビルドでのAnalytics無効化 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q7より*

**私は** 開発者 **として**
**デバッグビルドではAnalyticsを無効化したい**
**そうすることで** 開発中のテストデータが本番Analyticsを汚染しない

**関連要件**: REQ-301, REQ-302

**詳細シナリオ**:
1. `BuildConfig.DEBUG`をチェックしてAnalyticsの有効/無効を判定
2. デバッグビルド時は`FirebaseAnalytics.setAnalyticsCollectionEnabled(false)`を呼び出し
3. デバッグメニューからAnalyticsを手動で有効化できるUIを提供（オプション）
4. Logcatにデバッグログを出力してAnalyticsの状態を確認可能にする

**前提条件**:
- `BuildConfig`が利用可能
- `MainApplication`でAnalyticsの初期設定が可能

**優先度**: Should Have

---

## エピック5: セキュリティ対策

### ストーリー 5.1: google-services.jsonの保護 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q2より*

**私は** 開発者 **として**
**`google-services.json`をGit追跡から除外したい**
**そうすることで** Firebase APIキーが公開リポジトリに露出しない

**関連要件**: REQ-401, REQ-402

**詳細シナリオ**:
1. `.gitignore`に`app/google-services.json`を追加
2. `google-services.json.sample`というサンプルファイルを作成
3. README.mdに設定手順を記載
4. 既にGit追跡されている場合は`git rm --cached`で削除

**前提条件**:
- `.gitignore`ファイルが存在する
- Firebase Consoleからダウンロードした`google-services.json`が存在する

**優先度**: Must Have

---

## ストーリーマップ

```
エピック1: コアモジュール構築
├── ストーリー 1.1 (🔵 Must Have) :core:analyticsモジュールの作成
├── ストーリー 1.2 (🔵 Must Have) AnalyticsRepositoryインターフェースの定義
└── ストーリー 1.3 (🔵 Must Have) AnalyticsRepositoryImplの実装

エピック2: イベントトラッキング実装
├── ストーリー 2.1 (🔵 Must Have) 画面遷移トラッキングの実装
├── ストーリー 2.2 (🔵 Must Have) ユーザー操作トラッキングの実装
├── ストーリー 2.3 (🔵 Should Have) エラー・例外トラッキングの実装
└── ストーリー 2.4 (🔵 Should Have) アプリライフサイクルトラッキングの実装

エピック3: ユーザープロパティ実装
└── ストーリー 3.1 (🔵 Should Have) ユーザープロパティ設定機能の実装

エピック4: デバッグ設定
└── ストーリー 4.1 (🔵 Should Have) デバッグビルドでのAnalytics無効化

エピック5: セキュリティ対策
└── ストーリー 5.1 (🔵 Must Have) google-services.jsonの保護
```

## 信頼性レベルサマリー

- 🔵 青信号: 11件 (100%)
- 🟡 黄信号: 0件 (0%)
- 🔴 赤信号: 0件 (0%)

**品質評価**: 高品質（全ストーリーがユーザヒアリングまたは既存実装に基づいている）

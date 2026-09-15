# Firebaseの導入 受け入れ基準

**作成日**: 2026-09-12 00:58:26
**関連要件定義**: [requirements.md](requirements.md)
**関連ユーザストーリー**: [user-stories.md](user-stories.md)
**ヒアリング記録**: [interview-record.md](interview-record.md)

**【信頼性レベル凡例】**:
- 🔵 **青信号**: PRD・EARS要件定義書・設計文書・ユーザヒアリングを参考にした確実な基準
- 🟡 **黄信号**: PRD・EARS要件定義書・設計文書・ユーザヒアリングから妥当な推測による基準
- 🔴 **赤信号**: PRD・EARS要件定義書・設計文書・ユーザヒアリングにない推測による基準

---

## REQ-001: Firebase Analyticsの自動初期化 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q5、既存実装より*

### Given（前提条件）
- `app/google-services.json`が配置されている
- `build.gradle.kts`にGoogle Services Pluginが適用されている
- Firebase BOMとFirebase Analyticsの依存が定義されている

### When（実行条件）
- アプリケーションを起動する

### Then（期待結果）
- Firebase Analyticsが自動的に初期化される
- `FirebaseAnalytics.getInstance(context)`で取得可能になる

### テストケース

#### 正常系

- [ ] **TC-001-01**: アプリ起動時の自動初期化 🔵
  - **入力**: アプリを起動
  - **期待結果**: FirebaseAnalyticsインスタンスがnullでない
  - **信頼性**: 🔵 *既存実装（google-services.json）より*

---

## REQ-101: :core:analyticsモジュールの作成 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q3より*

### Given（前提条件）
- プロジェクトがマルチモジュール構成である
- `settings.gradle.kts`が存在する

### When（実行条件）
- `:core:analytics`モジュールを作成する

### Then（期待結果）
- `core/analytics/build.gradle.kts`が存在する
- `settings.gradle.kts`に`include(":core:analytics")`が追加されている
- Firebase Analytics依存関係が定義されている

### テストケース

#### 正常系

- [ ] **TC-101-01**: モジュール作成の確認 🔵
  - **入力**: `./gradlew :core:analytics:build`を実行
  - **期待結果**: ビルドが成功する
  - **信頼性**: 🔵 *ユーザヒアリングより*

- [ ] **TC-101-02**: 依存関係の確認 🔵
  - **入力**: `./gradlew :core:analytics:dependencies`を実行
  - **期待結果**: Firebase Analyticsが依存関係に含まれる
  - **信頼性**: 🔵 *ユーザヒアリングより*

---

## REQ-102: AnalyticsRepositoryインターフェースの定義 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q6より*

### Given（前提条件）
- `:core:analytics`モジュールが作成済み
- パッケージ`com.github.takayuki_hub.dashboard.core.analytics.repository`が存在する

### When（実行条件）
- `AnalyticsRepository`インターフェースを定義する

### Then（期待結果）
- `logEvent(eventName: String, params: Map<String, Any>)`メソッドが定義されている
- `setUserProperty(name: String, value: String)`メソッドが定義されている
- `logScreenView(screenName: String)`メソッドが定義されている

### テストケース

#### 正常系

- [ ] **TC-102-01**: インターフェース定義の確認 🔵
  - **入力**: `AnalyticsRepository`インターフェースをインポート
  - **期待結果**: 3つのメソッドが定義されている
  - **信頼性**: 🔵 *ユーザヒアリングより*

---

## REQ-201: 画面遷移イベントの記録 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q4より*

### Given（前提条件）
- `AnalyticsRepository`が実装済み
- 各featureモジュールが`:core:analytics`に依存している
- ユーザーがアプリを使用している

### When（実行条件）
- ユーザーが画面を遷移する

### Then（期待結果）
- `screen_view`イベントが記録される
- イベントパラメータに`screen_name`が含まれる
- イベントパラメータに`screen_class`が含まれる

### テストケース

#### 正常系

- [ ] **TC-201-01**: ホーム画面表示時のイベント記録 🔵
  - **入力**: ホーム画面を表示
  - **期待結果**: `screen_view`イベントが記録され、`screen_name="home_screen"`が含まれる
  - **信頼性**: 🔵 *ユーザヒアリングより*

- [ ] **TC-201-02**: 天気画面表示時のイベント記録 🔵
  - **入力**: 天気画面を表示
  - **期待結果**: `screen_view`イベントが記録され、`screen_name="weather_screen"`が含まれる
  - **信頼性**: 🔵 *ユーザヒアリングより*

- [ ] **TC-201-03**: ニュース画面表示時のイベント記録 🔵
  - **入力**: ニュース画面を表示
  - **期待結果**: `screen_view`イベントが記録され、`screen_name="news_screen"`が含まれる
  - **信頼性**: 🔵 *ユーザヒアリングより*

- [ ] **TC-201-04**: タスク画面表示時のイベント記録 🔵
  - **入力**: タスク画面を表示
  - **期待結果**: `screen_view`イベントが記録され、`screen_name="task_screen"`が含まれる
  - **信頼性**: 🔵 *ユーザヒアリングより*

---

## REQ-202: ユーザー操作イベントの記録 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q4より*

### Given（前提条件）
- `AnalyticsRepository`が実装済み
- ViewModelから`AnalyticsRepository`がインジェクト可能
- ユーザーがアプリを使用している

### When（実行条件）
- ユーザーがボタンをクリックする

### Then（期待結果）
- カスタムイベントが記録される
- イベントパラメータに操作内容が含まれる

### テストケース

#### 正常系

- [ ] **TC-202-01**: タスク作成ボタンクリック 🔵
  - **入力**: タスク作成ボタンをクリック
  - **期待結果**: `task_create_clicked`イベントが記録される
  - **信頼性**: 🔵 *ユーザヒアリング、既存機能より*

- [ ] **TC-202-02**: ニュース記事クリック 🔵
  - **入力**: ニュース記事をクリック
  - **期待結果**: `news_article_clicked`イベントが記録され、パラメータに記事情報が含まれる
  - **信頼性**: 🔵 *ユーザヒアリング、既存機能より*

---

## REQ-301: デバッグビルドでのAnalytics無効化 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q7より*

### Given（前提条件）
- デバッグビルドが構成されている
- `BuildConfig.DEBUG`が利用可能

### When（実行条件）
- デバッグビルドでアプリを起動する

### Then（期待結果）
- `FirebaseAnalytics.setAnalyticsCollectionEnabled(false)`が呼び出される
- Analyticsイベントが送信されない

### テストケース

#### 正常系

- [ ] **TC-301-01**: デバッグビルドでの無効化 🔵
  - **入力**: デバッグビルドでアプリを起動
  - **期待結果**: Logcatに「Firebase Analytics disabled in debug build」が出力される
  - **信頼性**: 🔵 *ユーザヒアリングより*

#### 異常系

- [ ] **TC-301-E01**: リリースビルドでは有効 🔵
  - **入力**: リリースビルドでアプリを起動
  - **期待結果**: Firebase Analyticsが有効になり、イベントが記録される
  - **信頼性**: 🔵 *ユーザヒアリングより*

---

## REQ-401: google-services.jsonの保護 🔵

**信頼性**: 🔵 *ユーザヒアリング2026-09-12 Q2より*

### Given（前提条件）
- `.gitignore`ファイルが存在する
- `app/google-services.json`が存在する

### When（実行条件）
- `.gitignore`に`app/google-services.json`を追加する

### Then（期待結果）
- `git status`で`app/google-services.json`が表示されない
- ファイルがGit追跡から除外される

### テストケース

#### 正常系

- [ ] **TC-401-01**: .gitignoreへの追加 🔵
  - **入力**: `echo "app/google-services.json" >> .gitignore`を実行
  - **期待結果**: `git status`で`google-services.json`が未追跡ファイルとして表示されない
  - **信頼性**: 🔵 *ユーザヒアリングより*

- [ ] **TC-401-02**: 既存追跡の削除 🔵
  - **入力**: `git rm --cached app/google-services.json`を実行
  - **期待結果**: Git追跡から削除される
  - **信頼性**: 🔵 *ユーザヒアリング、セキュリティベストプラクティスより*

---

## 非機能要件テスト

### NFR-001: Analyticsイベント記録のパフォーマンス 🟡

**信頼性**: 🟡 *Androidパフォーマンスベストプラクティスから妥当な推測*

- [ ] **TC-NFR-001-01**: UIスレッドブロック時間の測定
  - **測定項目**: UIスレッドのブロック時間
  - **目標値**: 0ms（非ブロッキング）
  - **測定条件**: 100イベントを連続で記録
  - **信頼性**: 🟡 *パフォーマンスベストプラクティスから推測*

### NFR-101: 個人情報の除外 🔵

**信頼性**: 🔵 *Firebase Analyticsセキュリティガイドラインより*

- [ ] **TC-NFR-101-01**: PIIの除外確認
  - **検証内容**: イベントパラメータに個人識別情報が含まれていないことを確認
  - **期待結果**: メールアドレス、電話番号、氏名などが記録されない
  - **信頼性**: 🔵 *Firebase Analyticsガイドラインより*

---

## Edgeケーステスト

### EDGE-101: イベントパラメータ数の制限 🔵

**信頼性**: 🔵 *Firebase Analytics制限仕様より*

- [ ] **TC-EDGE-101-01**: パラメータ数制限のテスト
  - **条件**: 26個のパラメータを持つイベントを記録
  - **期待結果**: 最初の25個のみ記録され、26個目は無視される（またはエラーログ）
  - **信頼性**: 🔵 *Firebase Analytics仕様より*

### EDGE-102: イベント名長制限 🔵

**信頼性**: 🔵 *Firebase Analytics制限仕様より*

- [ ] **TC-EDGE-102-01**: イベント名長制限のテスト
  - **条件**: 41文字のイベント名でイベントを記録
  - **期待結果**: 最初の40文字に切り詰められる（またはエラーログ）
  - **信頼性**: 🔵 *Firebase Analytics仕様より*

---

## テストケースサマリー

### カテゴリ別件数

| カテゴリ | 正常系 | 異常系 | 境界値 | 合計 |
|---------|--------|--------|--------|------|
| 機能要件 | 11 | 1 | 0 | 12 |
| 非機能要件 | 2 | 0 | 0 | 2 |
| Edgeケース | 0 | 0 | 2 | 2 |
| **合計** | 13 | 1 | 2 | 16 |

### 信頼性レベル分布

- 🔵 青信号: 14件 (87.5%)
- 🟡 黄信号: 2件 (12.5%)
- 🔴 赤信号: 0件 (0%)

**品質評価**: 高品質（大部分がユーザヒアリングまたは既存実装・仕様に基づいている）

### 優先度別テストケース

- **Must Have**: 9件
- **Should Have**: 5件
- **Could Have**: 2件

---

## テスト実施計画

### Phase 1: コアモジュール機能テスト
- REQ-001, REQ-101, REQ-102, REQ-103
- 優先度: Must Have
- 実施予定: 実装完了後即座

### Phase 2: イベントトラッキングテスト
- REQ-201, REQ-202, REQ-204~REQ-207
- 優先度: Must Have + Should Have
- 実施予定: Phase 1完了後

### Phase 3: セキュリティ・非機能テスト
- REQ-301, REQ-401, NFR-001, NFR-101, EDGE-101, EDGE-102
- 優先度: Must Have + Should Have
- 実施予定: Phase 2完了後

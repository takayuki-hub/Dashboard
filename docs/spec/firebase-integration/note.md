# Firebaseの導入 開発コンテキストノート

## 作成日時
2026-09-12

## プロジェクト概要

### プロジェクト名
Dashboard - Android ダッシュボードアプリケーション

### プロジェクトの目的
Jetpack Composeを使用して構築されたAndroidダッシュボードアプリケーション。天気情報、ニュース記事、タスク管理機能を統一されたダッシュボードに表示します。

**参照元**: [.claude/CLAUDE.md](.claude/CLAUDE.md)

## 技術スタック

### 使用技術・フレームワーク
- **言語**: Kotlin 2.4.20
- **UI**: Jetpack Compose（Compose BOM 2026.09.00）
- **DI**: Hilt 2.60.1
- **ネットワーク**: Retrofit 3.0.0
- **データベース**: Room 2.8.5
- **画像読み込み**: Coil 3.6.2
- **ナビゲーション**: Kotlin Serialization（型安全）
- **アナリティクス**: Firebase Analytics（BOM 34.19.0）

### ビルド環境
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 37 preview (37.1)
- **Java Version**: 11
- **Gradle**: Kotlin DSL
- **パッケージマネージャー**: Gradle + Version Catalog

### アーキテクチャパターン
- **アーキテクチャスタイル**: レイヤード・モジュラーアーキテクチャ
- **設計パターン**: Repository パターン、MVVM（ViewModel + UiState）
- **ディレクトリ構造**: マルチモジュール構成

```
:app                    # メインアプリケーションモジュール
├── :core:model        # ドメインモデル
├── :core:network      # ネットワーク層（Retrofit API）
├── :core:data         # データ層（Repository、Room、位置情報）
├── :core:designsystem # 共有UIコンポーネント
├── :feature:home      # ダッシュボード画面
├── :feature:weather   # 天気詳細画面
├── :feature:news      # ニュース画面
└── :feature:task      # タスク管理画面
```

**参照元**:
- [.claude/CLAUDE.md](.claude/CLAUDE.md)
- [build.gradle.kts](build.gradle.kts)
- [gradle/libs.versions.toml](gradle/libs.versions.toml)
- [settings.gradle.kts](settings.gradle.kts)

## 開発ルール

### アーキテクチャルール
1. **モジュール間依存の制約**: `feature:*` モジュールは他の `feature:*` モジュールに直接依存してはならない
2. **共通コードの配置**: 全ての共通コード・コンポーネントは `core:*` モジュールに配置する
3. **依存性注入の必須化**: ViewModel や Repository の作成時は必ず Hilt (`@HiltViewModel`, `@Inject`) を使用する
4. **ライブラリ追加の手順**: 新規ライブラリの追加は必ず `gradle/libs.versions.toml` に定義してから使用する

### コーディング規約
- **命名規則**: Kotlinの標準命名規則に従う
- **パッケージ構成**: `com.github.takayuki_hub.dashboard.{module}.{layer}`
- **ナビゲーション**: `@Serializable` による型安全なナビゲーション

### テスト要件
- **ユニットテスト**: JUnit 4.13.2
- **インストルメンテーションテスト**: AndroidX Test（Espresso、Compose UI Test）
- **実行コマンド**:
  - 全テスト: `./gradlew test`
  - モジュール別: `./gradlew :core:data:test`
  - インストルメンテーション: `./gradlew connectedAndroidTest`

**参照元**:
- [.claude/CLAUDE.md](.claude/CLAUDE.md)

## Firebase 現在の実装状況

### 導入済みの構成要素

#### Gradle設定
- **Google Services Plugin**: 4.5.0（build.gradle.kts で定義）
- **Firebase BOM**: 34.19.0（gradle/libs.versions.toml で定義）
- **Firebase Analytics**: BOM経由で管理（app/build.gradle.kts で依存）

**参照元**:
- [build.gradle.kts:10](build.gradle.kts) - Google Services Plugin
- [gradle/libs.versions.toml:4](gradle/libs.versions.toml) - Firebase BOM バージョン
- [gradle/libs.versions.toml:27-28](gradle/libs.versions.toml) - Firebase ライブラリ定義
- [app/build.gradle.kts:6](app/build.gradle.kts) - Google Services Plugin 適用
- [app/build.gradle.kts:72-73](app/build.gradle.kts) - Firebase 依存関係

#### google-services.json
- **配置場所**: `app/google-services.json`
- **Firebase Project ID**: YOUR_PROJECT_ID
- **Project Number**: YOUR_PROJECT_NUMBER
- **Package Name**: com.github.takayuki_hub.dashboard
- **Storage Bucket**: YOUR_PROJECT_ID.firebasestorage.app
- **API Key**: YOUR_FIREBASE_API_KEY
- **状態**: Git追跡対象外（`.gitignore` で除外）

**セキュリティ注意**: このファイルは現在Gitの追跡対象になっています。APIキーなどの認証情報を含むため、`.gitignore`への追加を検討してください。

**参照元**: [app/google-services.json](app/google-services.json)

### 未実装の部分

#### アプリケーション初期化
- **MainApplication.kt**: Firebase の明示的な初期化コードなし（自動初期化に依存）
- **MainActivity.kt**: Firebase Analytics のインスタンス化なし

**参照元**:
- [app/src/main/java/com/github/takayuki_hub/dashboard/MainApplication.kt](app/src/main/java/com/github/takayuki_hub/dashboard/MainApplication.kt)
- [app/src/main/java/com/github/takayuki_hub/dashboard/MainActivity.kt](app/src/main/java/com/github/takayuki_hub/dashboard/MainActivity.kt)

#### Analytics イベント記録
- 実装コード内でのイベント記録コードなし
- ユーザー行動の追跡コードなし

#### 未導入のFirebaseサービス
- Firebase Realtime Database
- Firebase Firestore
- Firebase Cloud Messaging (FCM)
- Firebase Authentication
- Firebase Storage
- Firebase Remote Config
- Firebase Crashlytics
- Firebase Performance Monitoring

### Firebase自動初期化の仕組み

Firebase SDKは以下の条件で自動初期化されます：
1. `google-services.json` が `app/` ディレクトリに配置されている
2. `com.google.gms.google-services` プラグインが適用されている
3. Firebase BOM と Firebase Analytics の依存関係が定義されている

現在の構成では、これらの条件を満たしているため、Firebase は自動的に初期化されます。

## 既存の要件定義

現時点で存在しません（`docs/spec/` ディレクトリ自体が未作成）。

## 既存の設計文書

現時点で存在しません（`docs/design/` ディレクトリ自体が未作成）。

## 関連実装

### Firebase関連の実装箇所

#### Gradle ビルドスクリプト
- **ルートレベル**: [build.gradle.kts](build.gradle.kts) - Google Services Plugin 定義
- **アプリレベル**: [app/build.gradle.kts](app/build.gradle.kts) - Firebase 依存関係
- **バージョンカタログ**: [gradle/libs.versions.toml](gradle/libs.versions.toml) - Firebase BOM とライブラリ定義

#### アプリケーションエントリーポイント
- **Application クラス**: [app/src/main/java/com/github/takayuki_hub/dashboard/MainApplication.kt](app/src/main/java/com/github/takayuki_hub/dashboard/MainApplication.kt)
- **Main Activity**: [app/src/main/java/com/github/takayuki_hub/dashboard/MainActivity.kt](app/src/main/java/com/github/takayuki_hub/dashboard/MainActivity.kt)
- **AndroidManifest**: [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml)

### 参考パターン

#### Hiltによる依存性注入パターン
- **Application レベル**: `@HiltAndroidApp` アノテーション
- **Activity レベル**: `@AndroidEntryPoint` アノテーション
- **ViewModel**: `@HiltViewModel` + `@Inject constructor()`
- **Repository**: インターフェースと実装の分離、`@Binds` でのバインド

**参照元**:
- [core/data/src/main/java/com/github/takayuki_hub/dashboard/core/data/di/RepositoryModule.kt](core/data/src/main/java/com/github/takayuki_hub/dashboard/core/data/di/RepositoryModule.kt)

### 共通モジュール・ユーティリティ

現在、Firebase関連の共通モジュールは存在しません。

## 技術的制約

### パフォーマンス制約
- **Min SDK 24**: Android 7.0 以上をサポート
- **Compose BOM**: バージョン 2026.09.00 を使用

### セキュリティ制約
- **APIキー管理**: News API キーは `local.properties` で管理（`BuildConfig.NEWS_API_KEY` にインジェクト）
- **位置情報**: ランタイムパーミッション必須
- **Firebase APIキー**: `google-services.json` で管理（現在Git追跡対象 - 要改善）

### 互換性制約
- **Kotlin**: 2.4.20
- **Java**: 11
- **Target SDK**: 36
- **Compile SDK**: 37 preview (37.1)

### データ制約
- **News API**: `local.properties` での設定が必要
- **位置情報**: FusedLocationProviderClient を使用

**参照元**: [.claude/CLAUDE.md](.claude/CLAUDE.md)

## 注意事項

### 開発時の注意点
- **KSP生成コード**: Hilt と Room の生成コード問題時は `./gradlew kspDebugKotlin` を実行
- **モジュール間依存**: `feature:*` モジュールは他の `feature:*` モジュールに直接依存してはならない
- **ライブラリ追加**: 必ず `gradle/libs.versions.toml` に定義してから使用
- **google-services.json**: APIキーを含むため、`.gitignore` への追加を検討

### セキュリティ上の注意点
- **google-services.json の扱い**: 現在Git追跡対象になっており、APIキーが公開される可能性がある
- **Gitleaks チェック**: pre-commit フックで自動実行
- **News API キー**: `local.properties` で管理し、Gitには含めない

### Firebase実装上の注意点
- **自動初期化**: 現在は自動初期化に依存しているが、明示的な初期化コードの追加を検討
- **Analytics イベント**: イベント記録コードが未実装のため、実際のアナリティクス機能は動作していない可能性
- **モジュール構成**: Firebaseを `:app` モジュールのみに限定するか、`:core` モジュールで管理するか検討が必要

## Git情報

### 現在のブランチ
main

### 最近のコミット
```
e938b63 chore: remove .kotlin directory from tracking
4e375b1 init
c7b6f5d refactor: gradlew.bat のエラーハンドリングと環境変数の設定を改善
e9bff1e build: Add gradlew.bat
```

### 開発状況
- **ステージング済み**:
  - `.claude/skills/create-feature-module.md` (新規)
  - `.claude/skills/run-ci-checks.md` (新規)
  - `.github/workflows/ci.yml` (新規)
- **変更済み**:
  - `app/build.gradle.kts`
  - `build.gradle.kts`
  - `gradle/libs.versions.toml`
- **未追跡**:
  - `.claude/CLAUDE.md`
  - `.claude/settings.json`
  - `app/google-services.json` (Firebase設定ファイル - `.gitignore` で除外済み)

## 収集したファイル一覧

### プロジェクト基本情報
- [.claude/CLAUDE.md](.claude/CLAUDE.md)
- [build.gradle.kts](build.gradle.kts)
- [settings.gradle.kts](settings.gradle.kts)
- [gradle/libs.versions.toml](gradle/libs.versions.toml)

### Firebase関連ファイル
- [app/build.gradle.kts](app/build.gradle.kts) - Firebase 依存関係定義
- [app/google-services.json](app/google-services.json) - Firebase プロジェクト設定
- [app/src/main/java/com/github/takayuki_hub/dashboard/MainApplication.kt](app/src/main/java/com/github/takayuki_hub/dashboard/MainApplication.kt)
- [app/src/main/java/com/github/takayuki_hub/dashboard/MainActivity.kt](app/src/main/java/com/github/takayuki_hub/dashboard/MainActivity.kt)

### 依存性注入関連
- [core/data/src/main/java/com/github/takayuki_hub/dashboard/core/data/di/RepositoryModule.kt](core/data/src/main/java/com/github/takayuki_hub/dashboard/core/data/di/RepositoryModule.kt)

---

**注意**: すべてのファイルパスはプロジェクトルートからの相対パスで記載しています。

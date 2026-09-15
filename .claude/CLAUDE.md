# CLAUDE.md

このファイルは、Claude Code (claude.ai/code) がこのリポジトリで作業する際のガイダンスを提供します。

## プロジェクト概要

このプロジェクトは、Jetpack Composeを使用して構築されたAndroidダッシュボードアプリケーションです。モジュラーなマルチモジュールアーキテクチャに従っており、天気情報、ニュース記事、タスク管理機能を統一されたダッシュボードに表示します。

**技術スタック:**
- Kotlin + Jetpack Compose（UI）
- Hilt（依存性注入）
- Retrofit（ネットワーク通信）
- Room（ローカルデータベース - タスク管理）
- Firebase Analytics
- Kotlin Serializationを使用した型安全なナビゲーション
- Coil（画像読み込み）

## アーキテクチャ

本プロジェクトは **レイヤード・モジュラーアーキテクチャ** を採用しており、関心事の明確な分離を実現しています。

## アーキテクチャルール
1. `feature:*` モジュールは他の `feature:*` モジュールに直接依存してはならない。
2. 全ての共通コード・コンポーネントは `core:*` モジュール（例: `core:designsystem`, `core:analytics`）に配置する。
3. ViewModel や Repository の作成時は必ず Hilt (`@HiltViewModel`, `@Inject`) を使用する。
4. 新規ライブラリの追加は必ず `gradle/libs.versions.toml` に定義してから使用する。

### モジュール構成

```
:app                    # メインアプリケーションモジュール（ナビゲーション、MainActivity）
├── :core:model        # 機能間で共有されるドメインモデル（WeatherData, Article, Task等）
├── :core:network      # ネットワーク層（Retrofit API: OpenMeteo, News API）
├── :core:data         # データ層（リポジトリ、Roomデータベース、位置情報トラッキング）
├── :core:designsystem # 共有UIコンポーネント（テーマ、カラー、タイポグラフィ）
├── :feature:home      # ダッシュボード画面（天気/ニュース/タスクのサマリー表示）
├── :feature:weather   # 天気詳細画面
├── :feature:news      # ニュース一覧・詳細画面
└── :feature:task      # タスク管理画面
```

### 依存関係フロー

- **featureモジュール** は以下に依存: `:core:model`, `:core:data`, `:core:designsystem`
- **`:core:data`** は以下に依存: `:core:model`, `:core:network`
- **`:app`** は以下に依存: 全featureモジュール + `:core:designsystem`
- **`:core:network`** と **`:core:designsystem`** は内部依存なし

### 主要パターン

**ナビゲーション:** `@Serializable`を使用した型安全なナビゲーション（各featureの`navigation`パッケージで定義）。`:app`モジュールの`AppNavHost`が全featureのナビゲーショングラフを統合します。

**依存性注入:** 各モジュールに`di`パッケージがあり、Hiltモジュールを配置。リポジトリは`:core:data/di/RepositoryModule`でバインド、ネットワーククライアントは`:core:network/di/*NetworkModule`で提供されます。

**データ層:** リポジトリパターンを採用。インターフェースは`:core:data/repository`に配置し、実装でネットワーク呼び出しとRoomデータベース操作を処理。`:core:data/mapper`のマッパーがネットワークDTOとドメインモデル間を変換します。

**UI層:** 各featureは次のパターンに従います: `Route` → `Screen` → `ViewModel` → `UiState`。Screenはコンポーザブル関数、ViewModelが状態を保持し、Routeがそれらを接続します。

## 主要コマンド

### ビルド・実行
```bash
# デバッグAPKをビルド
./gradlew assembleDebug

# 接続されたデバイス/エミュレータにインストール
./gradlew installDebug

# ビルドして実行
./gradlew installDebug && adb shell am start -n com.github.takayuki_hub.dashboard/.MainActivity
```

### テスト
```bash
# 全ユニットテストを実行
./gradlew test

# 特定モジュールのユニットテストを実行
./gradlew :core:data:test

# インストルメンテーションテストを実行（デバイス/エミュレータが必要）
./gradlew connectedAndroidTest

# 特定モジュールのインストルメンテーションテストを実行
./gradlew :app:connectedAndroidTest
```

### コード品質
```bash
# Gitleaksによるシークレットスキャンを実行（pre-commit経由）
pre-commit run gitleaks --all-files
```

### クリーンビルド
```bash
# ビルド成果物をクリーン
./gradlew clean

# クリーンして再ビルド
./gradlew clean assembleDebug
```

## 設定要件

**APIキー:** News APIは`local.properties`に保存されたAPIキーが必要です:
```properties
NEWS_API_KEY=your_api_key_here
```
このキーは`:core:data`モジュールの`BuildConfig.NEWS_API_KEY`にインジェクトされます。

**Firebase:** アプリはFirebase Analyticsを使用します。`app/google-services.json`が存在することを確認してください（Gitで追跡されていません）。

**パーミッション:** 天気機能には位置情報パーミッションが必要です。パーミッションは`app/src/main/AndroidManifest.xml`で宣言されています。

## ビルド設定

- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36
- **Compile SDK:** 37 preview (37.1)
- **Java Version:** 11
- **Kotlin Version:** 2.4.20

バージョンカタログは`gradle/libs.versions.toml`に集約され、`libs`アクセサを使用します。

## CI/CD

GitHub Actionsワークフロー（`.github/workflows/ci.yml`）がmain/develop/releaseブランチへのpush/PRで実行されます:
1. **Gitleaks:** ハードコードされたシークレットをチェック（featureブランチを含む全ブランチで実行）
2. **Build & Test:** ユニットテストを実行し、デバッグAPKをビルド（main/develop/releaseのみ）

## 開発ノート

**KSP生成コード:** HiltとRoomはKSP経由でコードを生成します。新しいDAOやDIモジュールを追加後に「unresolved reference」エラーが出た場合は、`./gradlew kspDebugKotlin`を実行してください。

**位置情報トラッキング:** アプリは`DefaultLocationTracker`（`:core:data/location`内）を使用し、ランタイム位置情報パーミッションが必要です。実装はFusedLocationProviderClientを使用しています。

**ナビゲーション拡張:** トップレベル遷移用のカスタムナビゲーションヘルパーは`app/src/main/java/.../navigation/NavigationExtensions.kt`にあります。

**Compose BOM:** プロジェクトはCompose BOM (Bill of Materials) バージョン`2026.09.00`を使用し、Composeライブラリのバージョンを一貫して管理しています。

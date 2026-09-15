---
name: create-feature-module
description: 新しい feature モジュールを作成し、必要なファイルとボイラープレートコードを生成します。
---

新しい Feature モジュールを作成する際は、以下の手順を実行してください：

## 前提条件
- モジュール名は小文字のケバブケース（例: `user-profile`, `settings`）
- パッケージ名: `com.github.takayuki_hub.dashboard.feature.<module_name>`

## 実行手順

### 1. ディレクトリ構造の作成
以下のディレクトリを作成：
- `feature/<module_name>/src/main/java/com/github/takayuki_hub/dashboard/feature/<module_name>/navigation/`
- `feature/<module_name>/src/main/java/com/github/takayuki_hub/dashboard/feature/<module_name>/ui/component/`
- `feature/<module_name>/src/test/java/com/github/takayuki_hub/dashboard/feature/<module_name>/`
- `feature/<module_name>/src/androidTest/java/com/github/takayuki_hub/dashboard/feature/<module_name>/`

### 2. AndroidManifest.xml の作成
`feature/<module_name>/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

</manifest>
```

### 3. build.gradle.kts の作成
既存の `feature/home/build.gradle.kts` をテンプレートとして使用し、
namespace を `com.github.takayuki_hub.dashboard.feature.<module_name>` に変更。

必須プラグイン:
- `alias(libs.plugins.android.library)`
- `alias(libs.plugins.kotlin.compose)`
- `alias(libs.plugins.hilt.android)`
- `alias(libs.plugins.ksp)`
- `alias(libs.plugins.kotlin.serialization)`

必須依存関係:
- `implementation(project(":core:model"))`
- `implementation(project(":core:data"))`
- `implementation(project(":core:designsystem"))`
- Hilt、Compose、Kotlinx Serialization関連のライブラリ

### 4. settings.gradle.kts への追加
`include(":feature:<module_name>")` を追加

### 5. 必須Kotlinファイルの作成

#### navigation/<ModuleName>NavGraph.kt
- `@Serializable object <ModuleName>Destination` を定義
- `NavGraphBuilder.<moduleName>Screen(...)` 拡張関数を実装

#### ui/<ModuleName>Destination.kt
既存の `feature/news/navigation/NewsNavGraph.kt` を参考

#### ui/<ModuleName>Route.kt
ViewModel を取得し Screen に渡す Composable

#### ui/<ModuleName>Screen.kt
実際のUI実装（Composable関数）

#### ui/<ModuleName>ViewModel.kt
`@HiltViewModel` アノテーション付き
`StateFlow<UiState>` を保持

#### ui/<ModuleName>UiState.kt
sealed interface または data class で状態を定義

### 6. app/build.gradle.kts への依存関係追加
dependencies ブロックに以下を追加：
```kotlin
implementation(project(":feature:<module_name>"))
```

### 7. Gradle Sync 実行
```bash
./gradlew kspDebugKotlin
```

### 8. AppNavHost への統合
`app/src/main/java/.../navigation/AppNavHost.kt` に
新しいfeatureのナビゲーション画面を追加

## 注意事項
- 既存の feature モジュール（home, weather, news, task）を参考にする
- アーキテクチャルール: feature モジュールは他の feature モジュールに依存しない
- 全ての ViewModel は `@HiltViewModel` を使用
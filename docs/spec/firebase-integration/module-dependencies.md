# Firebaseの導入 モジュール依存関係図

**作成日**: 2026-09-12
**関連要件定義**: [requirements.md](requirements.md)
**関連アーキテクチャ**: [architecture.md](architecture.md)

## 概要

Firebase Analytics導入に伴うモジュール依存関係の変更を図示します。新規作成する`:core:analytics`モジュールと既存モジュールとの依存関係を明確にします。

## モジュール依存関係図

### 全体構成

```
┌─────────────────────────────────────────────────────────────┐
│                         :app                                │
│                                                             │
│  - MainActivity                                             │
│  - MainApplication (Firebase Analytics設定)                  │
│  - BuildConfig (DEBUG判定)                                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
              │
              │ implementation(project(":core:analytics"))
              │ implementation(project(":core:model"))
              │ implementation(project(":core:designsystem"))
              │ implementation(project(":feature:home"))
              │ implementation(project(":feature:weather"))
              │ implementation(project(":feature:news"))
              │ implementation(project(":feature:task"))
              ↓
┌─────────────────────────────────────────────────────────────┐
│                     :feature:home                           │
│                     :feature:weather                        │
│                     :feature:news                           │
│                     :feature:task                           │
│                                                             │
│  - ViewModel (AnalyticsRepositoryをインジェクト)              │
│  - Screen (Composable)                                      │
│  - UiState                                                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
              │
              │ implementation(project(":core:analytics")) ← 追加
              │ implementation(project(":core:data"))
              │ implementation(project(":core:model"))
              │ implementation(project(":core:designsystem"))
              ↓
┌─────────────────────────────────────────────────────────────┐
│                  :core:analytics ★新規作成                   │
│                                                             │
│  - AnalyticsRepository (interface)                          │
│  - AnalyticsRepositoryImpl                                  │
│  - AnalyticsModule (Hilt DI)                                │
│  - AnalyticsEvents (定数)                                   │
│  - AnalyticsParams (定数)                                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
              │
              │ implementation(platform(libs.firebase.bom))
              │ implementation(libs.firebase.analytics)
              │ implementation(libs.hilt.android)
              │ implementation(libs.kotlinx.coroutines.android)
              ↓
┌─────────────────────────────────────────────────────────────┐
│                  Firebase Analytics SDK                     │
│                                                             │
│  - FirebaseAnalytics.getInstance()                          │
│  - FirebaseAnalytics.logEvent()                             │
│  - FirebaseAnalytics.setUserProperty()                      │
│  - FirebaseAnalytics.setAnalyticsCollectionEnabled()        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 詳細な依存関係

### :app モジュール

**既存依存関係**:
```kotlin
dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:home"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:news"))
    implementation(project(":feature:task"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
```

**追加依存関係**:
```kotlin
dependencies {
    // ... 既存依存関係 ...

    implementation(project(":core:analytics"))  // ← 追加
}
```

**変更理由**:
- MainApplicationでFirebaseAnalyticsインスタンスをインジェクトするため
- デバッグビルド時のAnalytics設定を実装するため

**関連要件**: REQ-301, REQ-302

---

### :feature:home モジュール

**既存依存関係**:
```kotlin
dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.compose.ui)
    // ... その他のCompose依存関係 ...
}
```

**追加依存関係**:
```kotlin
dependencies {
    // ... 既存依存関係 ...

    implementation(project(":core:analytics"))  // ← 追加
}
```

**変更理由**:
- HomeViewModelでAnalyticsRepositoryをインジェクトし、イベント記録を実装するため

**関連要件**: REQ-201, REQ-202

---

### :feature:weather モジュール

**既存依存関係**:
```kotlin
dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
```

**追加依存関係**:
```kotlin
dependencies {
    // ... 既存依存関係 ...

    implementation(project(":core:analytics"))  // ← 追加
}
```

**変更理由**:
- WeatherViewModelでAnalyticsRepositoryをインジェクトし、エラーイベントを記録するため

**関連要件**: REQ-201, REQ-204, REQ-208

---

### :feature:news モジュール

**既存依存関係**:
```kotlin
dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
```

**追加依存関係**:
```kotlin
dependencies {
    // ... 既存依存関係 ...

    implementation(project(":core:analytics"))  // ← 追加
}
```

**変更理由**:
- NewsViewModelでAnalyticsRepositoryをインジェクトし、記事クリックイベントを記録するため

**関連要件**: REQ-201, REQ-202, REQ-209

---

### :feature:task モジュール

**既存依存関係**:
```kotlin
dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
```

**追加依存関係**:
```kotlin
dependencies {
    // ... 既存依存関係 ...

    implementation(project(":core:analytics"))  // ← 追加
}
```

**変更理由**:
- TaskViewModelでAnalyticsRepositoryをインジェクトし、タスク操作イベントを記録するため

**関連要件**: REQ-201, REQ-202, REQ-203

---

### :core:analytics モジュール（新規作成）

**依存関係**:
```kotlin
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
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
```

**依存先**: なし（他のモジュールに依存しない）

**責務**:
- Firebase Analyticsのイベント記録機能を提供
- AnalyticsRepositoryインターフェースとその実装
- イベント名・パラメータ名の定数管理
- Hilt DIモジュール

**関連要件**: REQ-101, REQ-102, REQ-103, REQ-104, REQ-105

---

## 依存関係のルール遵守

### アーキテクチャルール（REQ-403）

1. **`:feature:*`モジュールは他の`:feature:*`モジュールに直接依存してはならない**
   - ✅ 遵守: `:core:analytics`は`:core`モジュールなので、全`:feature:*`から依存可能

2. **`:core:*`モジュールは他の`:core:*`モジュールに依存可能**
   - ✅ 遵守: `:core:analytics`は他の`:core:*`モジュールに依存しない（独立している）

3. **`:core:*`モジュールは`:feature:*`モジュールに依存してはならない**
   - ✅ 遵守: `:core:analytics`は`:feature:*`に依存しない

### Hilt DIパターン（REQ-404）

1. **ViewModelは`@HiltViewModel`を使用**
   - ✅ 各featureモジュールのViewModelで`@HiltViewModel`を使用

2. **Repositoryは`@Binds`でバインド**
   - ✅ `AnalyticsModule`で`@Binds`を使用してバインド

3. **依存性注入はコンストラクタインジェクション**
   - ✅ `AnalyticsRepositoryImpl`で`@Inject constructor()`を使用

---

## モジュール追加手順

### 1. settings.gradle.kts への追加

```kotlin
include(":core:analytics")
```

### 2. ディレクトリ構成作成

```
core/
└── analytics/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   └── java/com/github/takayuki_hub/dashboard/core/analytics/
        │       ├── constants/
        │       │   ├── AnalyticsEvents.kt
        │       │   └── AnalyticsParams.kt
        │       ├── di/
        │       │   └── AnalyticsModule.kt
        │       └── repository/
        │           ├── AnalyticsRepository.kt
        │           └── AnalyticsRepositoryImpl.kt
        └── test/
            └── java/com/github/takayuki_hub/dashboard/core/analytics/
                └── repository/
                    └── AnalyticsRepositoryImplTest.kt
```

### 3. 既存モジュールのbuild.gradle.kts更新

以下のモジュールに`implementation(project(":core:analytics"))`を追加:
- `:app`
- `:feature:home`
- `:feature:weather`
- `:feature:news`
- `:feature:task`

---

## 依存関係検証

### Gradle依存関係確認コマンド

```bash
# :core:analyticsモジュールの依存関係確認
./gradlew :core:analytics:dependencies

# :feature:homeモジュールの依存関係確認
./gradlew :feature:home:dependencies

# 全モジュールのビルド確認
./gradlew build
```

### 依存関係グラフの可視化

```bash
# プロジェクト全体の依存関係グラフを生成
./gradlew :app:dependencies --configuration debugRuntimeClasspath > dependencies.txt
```

---

## セキュリティ考慮事項

### google-services.jsonの配置（REQ-401, REQ-402）

- `google-services.json`は`:app`モジュールにのみ配置
- `:core:analytics`モジュールには配置しない
- `.gitignore`に`app/google-services.json`を追加

### Firebase SDKへの直接依存

- Firebase Analytics SDKへの直接依存は`:core:analytics`モジュールのみ
- featureモジュールからはFirebase SDKに直接アクセスしない
- `AnalyticsRepository`インターフェースを経由してアクセス

---

## パフォーマンス考慮事項

### モジュール分割によるビルド時間への影響

- `:core:analytics`は軽量なモジュール（5ファイル程度）
- Firebase BOMは既存の`:app`モジュールで使用済み
- 増分ビルド時の影響は最小限

### ビルドキャッシュの活用

- Gradleビルドキャッシュが有効な場合、`:core:analytics`は変更がない限り再ビルド不要
- KSP生成コード（Hilt）は変更時のみ再生成

---

## テスト戦略

### ユニットテスト

**:core:analyticsモジュール**:
- `AnalyticsRepositoryImplTest.kt`でRepositoryロジックをテスト
- MockKでFirebaseAnalyticsをモック化

**:feature:*モジュール**:
- ViewModelテストで`FakeAnalyticsRepository`を使用
- イベント記録の呼び出しを検証

### インストルメンテーションテスト

**:appモジュール**:
- MainApplicationのFirebase Analytics設定をテスト
- デバッグビルド時の無効化を検証

---

## 関連文書

- **要件定義書**: [requirements.md](requirements.md)
- **アーキテクチャ設計**: [architecture.md](architecture.md)
- **データフロー**: [dataflow.md](dataflow.md)
- **インターフェース定義**: [interfaces.kt](interfaces.kt)
- **受け入れ基準**: [acceptance-criteria.md](acceptance-criteria.md)

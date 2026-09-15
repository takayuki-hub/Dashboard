# Firebaseの導入 設計ヒアリング記録

**作成日**: 2026-09-12
**関連要件定義**: [requirements.md](requirements.md)
**関連コンテキスト**: [note.md](note.md)

## ヒアリング目的

Firebase Analytics導入の技術設計を作成するにあたり、実装方針とアーキテクチャ上の設計判断を明確化するためのヒアリングを実施しました。

## 質問と回答

### Q1: AnalyticsRepositoryの実装方法について、どちらのアプローチを採用しますか?

**質問日時**: 2026-09-12
**カテゴリ**: アーキテクチャ設計
**背景**: 既存のRepositoryパターンとの一貫性、テスタビリティ、パフォーマンスを考慮した実装方針の決定

**選択肢**:
1. **suspend関数**: Coroutines対応のsuspend関数でAnalyticsイベントを記録。既存のRepositoryパターンと一貫性があり、テストも容易。UIスレッドをブロックしない。
2. **通常関数**: 通常の関数として実装し、内部でバックグラウンドスレッド処理。Firebase Analytics SDKは非ブロッキングだが、明示的なCoroutines対応がない。

**回答**: suspend関数（推奨）

**設計への影響**:
- `AnalyticsRepository`の全メソッドを`suspend fun`として定義
- ViewModelから`viewModelScope.launch`でイベント記録を呼び出し
- テストコードで`runTest`によるCoroutinesテストが可能
- 既存の`:core:data`モジュールのRepositoryパターンと一貫性を保つ
- 非機能要件NFR-001（UIスレッドブロック禁止）を満たす設計

**参照要件**: REQ-102, REQ-103, NFR-001

---

### Q2: デバッグビルドでのAnalytics無効化はどこで実装しますか?

**質問日時**: 2026-09-12
**カテゴリ**: デバッグ設定
**背景**: デバッグビルド時のAnalytics無効化をどこで一元管理するかの設計判断

**選択肢**:
1. **MainApplicationで一括設定**: MainApplication.onCreateでBuildConfig.DEBUGを判定し、FirebaseAnalytics.setAnalyticsCollectionEnabled(false)を呼び出す。シンプルで一元管理。
2. **RepositoryImplで判定**: AnalyticsRepositoryImplの各メソッド内でBuildConfig.DEBUGを判定。より細かい制御が可能だが、パフォーマンスオーバーヘッドあり。

**回答**: MainApplicationで一括設定（推奨）

**設計への影響**:
- `MainApplication.onCreate()`でFirebaseAnalyticsの有効/無効を設定
- `BuildConfig.DEBUG`による環境判定ロジックを1箇所に集約
- Logcat出力で設定状態を確認可能にする
- `:app`モジュールの`MainApplication.kt`に実装
- `:core:analytics`モジュールには環境判定ロジックを含めない（責務の分離）

**参照要件**: REQ-301, REQ-302

---

### Q3: イベント名とパラメータ名の管理方法は?

**質問日時**: 2026-09-12
**カテゴリ**: コーディング規約・保守性
**背景**: イベント名とパラメータ名の一元管理によるタイポ防止と保守性向上

**選択肢**:
1. **objectで定数管理**: AnalyticsEventsとAnalyticsParamsのobjectでイベント名・パラメータ名を定数として一元管理。タイポ防止と保守性向上。
2. **文字列リテラル**: イベント記録時に直接文字列で指定。柔軟性は高いがタイポのリスクあり。

**回答**: objectで定数管理（推奨）

**設計への影響**:
- `com.github.takayuki_hub.dashboard.core.analytics.constants.AnalyticsEvents`オブジェクトを作成
- `com.github.takayuki_hub.dashboard.core.analytics.constants.AnalyticsParams`オブジェクトを作成
- Firebase Analytics標準イベント（`screen_view`等）とカスタムイベントを分離
- イベント名・パラメータ名の命名規則を明確化（スネークケース、最大40文字）
- Edgeケース要件（EDGE-102, EDGE-103）への対応を設計時に織り込む

**参照要件**: NFR-201, EDGE-102, EDGE-103

---

## ヒアリング結果サマリー

### 確定した設計方針

1. **Repositoryパターン**:
   - suspend関数によるCoroutines対応
   - 既存の`:core:data`パターンとの一貫性
   - ViewModelからviewModelScope経由で呼び出し

2. **デバッグ設定**:
   - MainApplicationでの一括設定
   - BuildConfig.DEBUGによる環境判定
   - Logcat出力による設定確認

3. **定数管理**:
   - objectによるイベント名・パラメータ名の一元管理
   - スネークケース命名規則
   - 最大40文字制限の遵守

### 次のステップ

以下の設計文書を作成します:

1. **architecture.md**: `:core:analytics`モジュールの構成とマルチモジュール統合
2. **dataflow.md**: イベントトラッキングのデータフロー図
3. **interfaces.kt**: Kotlinインターフェース定義
4. **module-dependencies.md**: モジュール依存関係図

### 設計上の重要な決定事項

- `:core:analytics`モジュールは`:app`以外のfeatureモジュールからも利用可能
- Firebase Analyticsの初期化は既存の自動初期化を継続（明示的初期化コード不要）
- デバッグ設定のみ`:app`モジュールのMainApplicationで管理
- イベント記録ロジックは全て`:core:analytics`に集約

## 関連文書

- **要件定義書**: [requirements.md](requirements.md)
- **ユーザストーリー**: [user-stories.md](user-stories.md)
- **受け入れ基準**: [acceptance-criteria.md](acceptance-criteria.md)
- **コンテキストノート**: [note.md](note.md)

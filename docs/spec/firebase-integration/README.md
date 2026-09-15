# Firebaseの導入 設計文書

**作成日**: 2026-09-12
**プロジェクト**: Dashboard - Android ダッシュボードアプリケーション
**優先度**: Should Have（次のリリースに向けて実装推奨）

## 概要

このディレクトリには、Firebase Analytics導入に関する全ての設計文書が含まれています。既存のマルチモジュール構成に`:core:analytics`モジュールを追加し、全featureモジュールから統一的にAnalytics機能を利用できるようにします。

## 設計文書一覧

### 1. 開発コンテキストノート
**ファイル**: [note.md](note.md)
**内容**:
- プロジェクトの技術スタック
- Firebase現在の実装状況
- アーキテクチャルール
- 既存の実装パターン

**推奨読者**: 実装開始前に必読

---

### 2. 要件定義書
**ファイル**: [requirements.md](requirements.md)
**内容**:
- 機能要件（28件）: EARS記法で記述
- 非機能要件（7件）: パフォーマンス、セキュリティ、保守性、互換性
- Edgeケース（3件）: パラメータ数制限、イベント名長制限

**信頼性レベル**:
- 🔵 青信号: 25件 (89.3%) - ユーザヒアリング・既存実装から確実
- 🟡 黄信号: 3件 (10.7%) - 妥当な推測
- 🔴 赤信号: 0件 (0%)

**推奨読者**: プロダクトマネージャー、開発者

---

### 3. ユーザストーリー
**ファイル**: [user-stories.md](user-stories.md)
**内容**:
- 5エピック、11ユーザストーリー
- 詳細なシナリオ、前提条件、制約事項
- 優先度（Must Have / Should Have）

**エピック**:
1. コアモジュール構築
2. イベントトラッキング実装
3. ユーザープロパティ実装
4. デバッグ設定
5. セキュリティ対策

**推奨読者**: 開発者、QA

---

### 4. 受け入れ基準
**ファイル**: [acceptance-criteria.md](acceptance-criteria.md)
**内容**:
- 16テストケース（正常系13、異常系1、境界値2）
- Given-When-Then形式のテストケース
- 非機能要件テスト、Edgeケーステスト

**テスト実施計画**:
- Phase 1: コアモジュール機能テスト
- Phase 2: イベントトラッキングテスト
- Phase 3: セキュリティ・非機能テスト

**推奨読者**: QA、開発者

---

### 5. 準備タスク
**ファイル**: [prep.md](prep.md)
**内容**:
- 必須タスク（2件）: 実装開始前に完了が必要
- 推奨タスク（2件）: 実装中に用意できればOK
- 確認事項（2件）: 早めの判断・確認が推奨

**必須タスク**:
1. google-services.jsonを.gitignoreに追加
2. google-services.json.sampleの作成

**推奨読者**: 開発者（実装開始前）

---

### 6. 設計ヒアリング記録
**ファイル**: [design-interview.md](design-interview.md)
**内容**:
- 設計方針の決定事項
- Repositoryパターンのアプローチ（suspend関数採用）
- デバッグ設定の実装場所（MainApplication採用）
- イベント定数の管理方法（object採用）

**推奨読者**: アーキテクト、開発者

---

### 7. アーキテクチャ設計
**ファイル**: [architecture.md](architecture.md)
**内容**:
- `:core:analytics`モジュールの構成
- パッケージ構造、依存関係
- Hilt DIパターン
- デバッグ設定の実装
- セキュリティ・パフォーマンス考慮事項

**主要な設計決定**:
- suspend関数によるCoroutines対応
- MainApplicationでのデバッグ設定一括管理
- objectによるイベント名・パラメータ名の定数管理

**推奨読者**: アーキテクト、開発者

---

### 8. データフロー設計
**ファイル**: [dataflow.md](dataflow.md)
**内容**:
- 画面遷移イベントのデータフロー
- ユーザー操作イベントのデータフロー
- エラー・例外イベントのデータフロー
- アプリライフサイクルイベントのデータフロー
- ユーザープロパティ設定のデータフロー
- デバッグビルドでのAnalytics無効化フロー

**推奨読者**: 開発者（実装時の参考）

---

### 9. Kotlinインターフェース定義
**ファイル**: [interfaces.kt](interfaces.kt)
**内容**:
- `AnalyticsRepository`インターフェース
- `AnalyticsRepositoryImpl`実装クラス
- `AnalyticsEvents`定数オブジェクト
- `AnalyticsParams`定数オブジェクト
- `AnalyticsModule` Hilt DIモジュール
- `FakeAnalyticsRepository`テスト用モック

**推奨読者**: 開発者（コピー&ペーストで実装可能）

---

### 10. モジュール依存関係
**ファイル**: [module-dependencies.md](module-dependencies.md)
**内容**:
- モジュール依存関係図
- 各モジュールの依存関係詳細
- 依存関係ルールの遵守確認
- モジュール追加手順

**推奨読者**: アーキテクト、開発者

---

## 実装開始前のチェックリスト

### 準備タスク（必須）

- [ ] **google-services.jsonを.gitignoreに追加**
  - 手順: [prep.md](prep.md) の「必須」セクションを参照
  - 関連要件: REQ-401, REQ-402

- [ ] **google-services.json.sampleの作成**
  - 手順: [prep.md](prep.md) の「必須」セクションを参照
  - 関連要件: REQ-401, REQ-402

### 設計文書の確認

- [ ] [note.md](note.md) - 既存実装状況を理解
- [ ] [requirements.md](requirements.md) - 要件を把握
- [ ] [architecture.md](architecture.md) - アーキテクチャを理解
- [ ] [dataflow.md](dataflow.md) - データフローを確認
- [ ] [interfaces.kt](interfaces.kt) - 実装すべきインターフェースを確認
- [ ] [module-dependencies.md](module-dependencies.md) - モジュール追加手順を確認

## 実装フェーズ

### Phase 1: コアモジュール構築（Must Have）

**タスク**:
1. `:core:analytics`モジュールの作成
2. `AnalyticsRepository`インターフェースの定義
3. `AnalyticsRepositoryImpl`の実装
4. `AnalyticsModule` Hilt DIモジュールの作成
5. `AnalyticsEvents`/`AnalyticsParams`定数の定義

**関連要件**: REQ-101~REQ-105

**テストケース**: TC-101-01, TC-101-02, TC-102-01

**成果物**:
- `core/analytics/build.gradle.kts`
- `AnalyticsRepository.kt`
- `AnalyticsRepositoryImpl.kt`
- `AnalyticsModule.kt`
- `AnalyticsEvents.kt`
- `AnalyticsParams.kt`

---

### Phase 2: イベントトラッキング実装（Must Have）

**タスク**:
1. 画面遷移イベントの記録
2. ユーザー操作イベントの記録
3. エラー・例外イベントの記録（Should Have）
4. アプリライフサイクルイベントの記録（Should Have）

**関連要件**: REQ-201~REQ-210

**テストケース**: TC-201-01~TC-201-04, TC-202-01~TC-202-02

**成果物**:
- 各featureモジュールのViewModelにイベント記録ロジック追加
- MainApplication.kt/MainActivity.ktにライフサイクルイベント追加

---

### Phase 3: セキュリティ・非機能対応（Must Have）

**タスク**:
1. `google-services.json`を.gitignoreに追加
2. デバッグビルド時のAnalytics無効化
3. 個人情報（PII）除外ロジックの実装

**関連要件**: REQ-301, REQ-401, NFR-101

**テストケース**: TC-301-01, TC-301-E01, TC-401-01, TC-401-02, TC-NFR-101-01

**成果物**:
- `.gitignore`更新
- `google-services.json.sample`作成
- MainApplication.ktにデバッグ設定追加

---

### Phase 4: ユーザープロパティ実装（Should Have）

**タスク**:
1. ユーザープロパティ設定機能の実装
2. アプリバージョンの記録
3. 初回起動時刻の記録

**関連要件**: REQ-211~REQ-213

**成果物**:
- MainApplication.ktにユーザープロパティ設定ロジック追加

---

## 信頼性レベルサマリー

### 要件定義（全28要件）
- 🔵 青信号: 25件 (89.3%)
- 🟡 黄信号: 3件 (10.7%)
- 🔴 赤信号: 0件 (0%)

### ユーザストーリー（全11ストーリー）
- 🔵 青信号: 11件 (100%)
- 🟡 黄信号: 0件 (0%)
- 🔴 赤信号: 0件 (0%)

### 受け入れ基準（全16テストケース）
- 🔵 青信号: 14件 (87.5%)
- 🟡 黄信号: 2件 (12.5%)
- 🔴 赤信号: 0件 (0%)

**品質評価**: 高品質（大部分がユーザヒアリングまたは既存実装・仕様に基づいている）

---

## 関連技術スタック

- **言語**: Kotlin 2.4.20
- **Firebase BOM**: 34.19.0
- **Firebase Analytics**: BOM経由
- **Hilt**: 2.60.1
- **Coroutines**: Kotlin Coroutines Android
- **Min SDK**: 24
- **Target SDK**: 36
- **Compile SDK**: 37 preview (37.1)

---

## セキュリティ注意事項

### google-services.jsonの取り扱い

- ❌ **絶対にGitにコミットしない**
- ✅ `.gitignore`に追加済み
- ✅ `google-services.json.sample`をプレースホルダーとして作成

### 個人情報（PII）の除外

- メールアドレス、電話番号、氏名、住所を記録しない
- エラーメッセージは100文字に制限
- タスクの内容やニュース記事のタイトルは記録しない（IDのみ）

---

## パフォーマンス考慮事項

- 全Analyticsメソッドはsuspend関数として定義
- `withContext(Dispatchers.IO)`でバックグラウンドスレッド実行
- Firebase Analytics SDKは内部で非ブロッキング処理
- ネットワーク未接続時は自動的にローカルキューに保存

---

## 次のステップ

1. **準備タスクの完了**: [prep.md](prep.md) を参照
2. **Phase 1実装開始**: `:core:analytics`モジュールの作成
3. **ユニットテスト実装**: 各Phaseの実装後にテストを追加
4. **インストルメンテーションテスト**: Firebase Analytics DebugViewで検証

---

## 質問・相談先

設計文書に関する質問や実装中の相談は、以下の文書を参照してください:

- **アーキテクチャに関する質問**: [architecture.md](architecture.md)
- **データフローに関する質問**: [dataflow.md](dataflow.md)
- **実装詳細に関する質問**: [interfaces.kt](interfaces.kt)
- **テストに関する質問**: [acceptance-criteria.md](acceptance-criteria.md)

---

**生成日時**: 2026-09-12
**バージョン**: 1.0.0
**ステータス**: 設計完了、実装準備完了

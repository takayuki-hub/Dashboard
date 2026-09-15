# firebase-integration タスク概要

**作成日**: 2026-09-14
**プロジェクト期間**: 2026-09-14 - 2026-09-30（11営業日）
**推定工数**: 88時間
**総タスク数**: 11件

## 関連文書

- **要件定義書**: [📋 requirements.md](../../spec/firebase-integration/requirements.md)
- **コンテキストノート**: [📝 note.md](../../spec/firebase-integration/note.md)
- **CLAUDE.md**: [📐 CLAUDE.md](../../../.claude/CLAUDE.md)

## フェーズ構成

| フェーズ | 期間 | 成果物 | タスク数 | 工数 | ファイル |
|---------|------|--------|----------|------|----------|
| Phase 1 | 2.5日 | :core:analyticsモジュール、AnalyticsRepository | 3件 | 20h | [TASK-0001~0003](#phase-1-基盤構築) |
| Phase 2 | 4日 | イベントトラッキング実装 | 4件 | 32h | [TASK-0004~0007](#phase-2-イベントトラッキング実装) |
| Phase 3 | 1.5日 | セキュリティ設定、デバッグ設定 | 2件 | 12h | [TASK-0008~0009](#phase-3-統合セキュリティ対策) |
| Phase 4 | 2日 | 統合テスト、E2Eテスト | 2件 | 16h | [TASK-0010~0011](#phase-4-テスト検証) |
| **合計** | **10日** | **Firebase Analytics完全統合** | **11件** | **80h** | - |

## タスク番号管理

**使用済みタスク番号**: TASK-0001 ~ TASK-0011
**次回開始番号**: TASK-0012

## 全体進捗

- [ ] Phase 1: 基盤構築
- [ ] Phase 2: イベントトラッキング実装
- [ ] Phase 3: 統合・セキュリティ対策
- [ ] Phase 4: テスト・検証

## マイルストーン

- **M1: 基盤完成** (2026-09-17): :core:analyticsモジュール、AnalyticsRepository完成
- **M2: イベント実装完成** (2026-09-23): 全イベントトラッキング実装完了
- **M3: セキュリティ対策完成** (2026-09-25): google-services.json保護、デバッグ設定完了
- **M4: リリース準備完了** (2026-09-30): 全テスト完了、Firebase Console検証完了

---

## Phase 1: 基盤構築

**期間**: 2.5日（20時間）
**目標**: :core:analyticsモジュール作成、AnalyticsRepository定義、Hilt DI設定
**成果物**: :core:analyticsモジュール、AnalyticsRepository、AnalyticsModule

### タスク一覧

- [ ] [TASK-0001: :core:analyticsモジュール作成とGradle設定](TASK-0001.md) - 8h (DIRECT) 🔵
- [ ] [TASK-0002: AnalyticsRepositoryインターフェース定義](TASK-0002.md) - 8h (TDD) 🔵
- [ ] [TASK-0003: Hilt DIモジュール作成](TASK-0003.md) - 4h (DIRECT) 🔵

### 依存関係

```
TASK-0001 → TASK-0002 → TASK-0003
```

---

## Phase 2: イベントトラッキング実装

**期間**: 4日（32時間）
**目標**: アプリライフサイクル、画面遷移、ユーザー操作、エラートラッキング実装
**成果物**: AnalyticsEvents定数、各ViewModelのイベント記録

### タスク一覧

- [ ] [TASK-0004: アプリライフサイクルイベント実装](TASK-0004.md) - 8h (TDD) 🔵
- [ ] [TASK-0005: 画面遷移イベント実装](TASK-0005.md) - 8h (TDD) 🔵
- [ ] [TASK-0006: ユーザー操作イベント実装](TASK-0006.md) - 8h (TDD) 🔵
- [ ] [TASK-0007: エラートラッキング実装](TASK-0007.md) - 8h (TDD) 🔵

### 依存関係

```
TASK-0003 → TASK-0004 → TASK-0005 → TASK-0006 → TASK-0007
```

---

## Phase 3: 統合・セキュリティ対策

**期間**: 1.5日（12時間）
**目標**: google-services.json保護、デバッグ設定、ユーザープロパティ実装
**成果物**: google-services.json.sample、デバッグ設定、ユーザープロパティ実装

### タスク一覧

- [ ] [TASK-0008: セキュリティ設定（google-services.json保護）](TASK-0008.md) - 4h (DIRECT) 🔵
- [ ] [TASK-0009: デバッグ設定とユーザープロパティ実装](TASK-0009.md) - 8h (TDD) 🔵

### 依存関係

```
TASK-0007 → TASK-0008 → TASK-0009
```

---

## Phase 4: テスト・検証

**期間**: 2日（16時間）
**目標**: 統合テスト、E2Eテスト（Firebase Console検証）
**成果物**: 統合テストスイート、E2E検証レポート

### タスク一覧

- [ ] [TASK-0010: 統合テスト実装](TASK-0010.md) - 8h (TDD) 🔵
- [ ] [TASK-0011: E2Eテスト（Firebase Console検証）](TASK-0011.md) - 8h (DIRECT) 🔵

### 依存関係

```
TASK-0009 → TASK-0010 → TASK-0011
```

---

## 信頼性レベルサマリー

### 全タスク統計

- **総タスク数**: 11件
- 🔵 **青信号**: 11件 (100%)
- 🟡 **黄信号**: 0件 (0%)
- 🔴 **赤信号**: 0件 (0%)

### フェーズ別信頼性

| フェーズ | 🔵 青 | 🟡 黄 | 🔴 赤 | 合計 |
|---------|-------|-------|-------|------|
| Phase 1 | 3 | 0 | 0 | 3 |
| Phase 2 | 4 | 0 | 0 | 4 |
| Phase 3 | 2 | 0 | 0 | 2 |
| Phase 4 | 2 | 0 | 0 | 2 |

**品質評価**: ✅ 高品質

全タスクが要件定義書・設計文書・ユーザヒアリング・既存実装に基づいており、信頼性が非常に高いタスク構成です。

## クリティカルパス

```
TASK-0001 → TASK-0002 → TASK-0003 → TASK-0004 → TASK-0005 → TASK-0006 → TASK-0007 → TASK-0008 → TASK-0009 → TASK-0010 → TASK-0011
```

**クリティカルパス工数**: 80時間（10営業日）
**並行作業可能工数**: 0時間（全タスクが順次依存）

## ヒアリング結果サマリー

### 作業規模
- **選択**: 詳細タスク分割（推奨）
- **理由**: 詳細な実装手順、包括的なテストケース、完全な依存関係分析、UI/UX要件を含む

### タスク粒度
- **選択**: 1日単位（8時間）で問題ない
- **理由**: 既存プロジェクトの標準粒度に合わせる

### 実装優先順序
- **選択**: 基盤→実装→テスト（推奨）
- **理由**: モジュール作成→Repository実装→イベント記録の順で段階的に構築

### テスト要件
- **選択**: E2Eテストも必要
- **理由**: Firebase Consoleでの実機検証までカバーする

### UI/UX要件
- **選択**: ローディング・エラー両方不要（推奨）
- **理由**: Analyticsはバックグラウンド動作でユーザーに影響を与えない

## 開発コンテキスト

### 技術スタック
- **言語**: Kotlin 2.4.20
- **UI**: Jetpack Compose（BOM 2026.09.00）
- **DI**: Hilt 2.60.1
- **アナリティクス**: Firebase Analytics（BOM 34.19.0）
- **テスト**: JUnit 4.13.2、MockK、Coroutines Test

### アーキテクチャルール（CLAUDE.mdより）
1. `feature:*`モジュールは他の`feature:*`モジュールに直接依存してはならない
2. 全ての共通コード・コンポーネントは`core:*`モジュールに配置する
3. ViewModelやRepositoryの作成時は必ずHilt (`@HiltViewModel`, `@Inject`) を使用する
4. 新規ライブラリの追加は必ず`gradle/libs.versions.toml`に定義してから使用する

## 次のステップ

タスクを実装するには:
- 全タスク順番に実装: `/tsumiki-legacy:kairo-implement`
- 特定タスクを実装: `/tsumiki-legacy:kairo-implement TASK-0001`

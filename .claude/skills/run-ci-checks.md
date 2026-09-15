---
name: run-ci-checks
description: CI（GitHub Actions）で実行されるチェックと同等のビルド・テスト・シークレットスキャンをローカルで実行し、コードの整合性を検証します。
---

コードの変更を行った後、またはコミット/push する前に、以下の手順でローカル検証を実行してください。

## 実行する検証ステップ

### CIで実行されるチェック（必須）

#### 1. Gitleaks スキャン（機密情報の混入チェック）
**目的**: ハードコードされたAPIキー、パスワード、トークンなどの漏洩を防ぐ

**実行方法（いずれか）**:
```bash
# 方法1: pre-commit経由（推奨）
pre-commit run gitleaks --all-files

# 方法2: gitleaksコマンド直接実行
gitleaks detect --source . -v
```

**エラー時の対応**:
- **重要**: 検出された箇所を必ず開発者に確認してもらう
- 検出内容を開発者に提示し、本当にシークレット情報かどうか判断を求める
- 実際のシークレットの場合: 該当箇所を削除し、環境変数/設定ファイル（`local.properties`等）に移動
- 誤検知であることを開発者が確認した場合のみ: `.gitleaks.toml` に例外を追加
- **絶対に自動的に例外追加や無視をしないこと**

#### 2. ユニットテストの実行
**目的**: 全モジュールの単体テストが正常に動作することを確認

**実行方法**:
```bash
./gradlew test
```

**レポート確認**:
- HTML レポート: `*/build/reports/tests/testDebugUnitTest/index.html`
- 失敗したテストの詳細を確認

**エラー時の対応**:
- テスト失敗の原因を特定（ロジックエラー、データ不整合など）
- 実装コードまたはテストコードを修正
- 修正後、再度テストを実行して確認

#### 3. Debug APK のビルド確認
**目的**: コンパイルエラーや依存関係の問題がないことを確認

**実行方法**:
```bash
./gradlew assembleDebug
```

**エラー時の対応**:
- コンパイルエラーの修正
- 依存関係の問題解決（`./gradlew --refresh-dependencies` など）
- KSP生成コードの問題の場合は `./gradlew kspDebugKotlin` を実行

### 追加の推奨チェック（任意）

#### 4. Android Lint / 静的解析
**目的**: コードスタイルや潜在的なバグをチェック

**実行方法**:
```bash
./gradlew lint
```

**レポート確認**:
- HTML レポート: `*/build/reports/lint-results-debug.html`

**注意**: このチェックは現在のCIには含まれていませんが、コード品質向上のため推奨されます。

## 全チェックを一度に実行

すべてのチェックを順次実行するには：
```bash
pre-commit run gitleaks --all-files && ./gradlew test assembleDebug
```

Lintも含める場合：
```bash
pre-commit run gitleaks --all-files && ./gradlew test lint assembleDebug
```

## チェック実行のタイミング

- **コミット前**: 少なくとも Gitleaks を実行（pre-commit フックで自動化推奨）
- **push 前**: test と assembleDebug を実行
- **PR 作成前**: すべてのチェック（Lint含む）を実行

## エラー発生時の対応フロー

1. エラーメッセージとログを確認
2. 問題の原因を特定
3. Gitleaksエラーの場合は必ず開発者に確認を求める
4. コードを修正
5. 該当するチェックを再実行
6. すべてのチェックがパスするまで 1-5 を繰り返す
7. すべて成功したら、その旨をユーザーに報告

## 注意事項

- CIでは main/develop/release ブランチのみビルド・テストが実行されますが、ローカルではブランチに関わらず全チェックを推奨
- Gitleaks はすべてのブランチで実行されます
- テスト実行には数分かかる場合があります
- KSP関連のエラーが発生した場合は `./gradlew clean` してから再実行
- **Gitleaksで検出された内容は必ず開発者に確認を求め、勝手に例外追加しないこと**
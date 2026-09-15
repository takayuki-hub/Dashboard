# Firebaseの導入 準備タスク（ユーザー作業）

> **仕様**: [requirements.md](requirements.md)
> **生成日**: 2026-09-12 00:58:26

**【信頼性レベル凡例】**:
- 🔵 **青信号**: 要件定義書・設計文書・ユーザヒアリングで明確に必要と判明したタスク
- 🟡 **黄信号**: 要件定義書・設計文書から妥当に推測されるタスク
- 🔴 **赤信号**: 推測による予防的タスク（実装時に不要と判明する可能性あり）

## 必須（実装開始前に完了が必要）

以下のタスクが完了していないと、実装フェーズでブロッカーになります。

- [ ] **google-services.jsonを.gitignoreに追加** 🔵 *ユーザヒアリング2026-09-12 Q2より*
  - 手順:
    1. `.gitignore`ファイルを開く
    2. `app/google-services.json`を追加
    3. 既にGit追跡されている場合は`git rm --cached app/google-services.json`を実行
    4. `git status`で`google-services.json`が未追跡になっていることを確認
  - 関連要件: REQ-401, REQ-402

- [ ] **google-services.json.sampleの作成** 🟡 *セキュリティベストプラクティスから妥当な推測*
  - 手順:
    1. `app/google-services.json`をコピーして`app/google-services.json.sample`を作成
    2. サンプルファイル内の認証情報を`YOUR_PROJECT_ID_HERE`などのプレースホルダーに置換
    3. `google-services.json.sample`をGitにコミット
  - 関連要件: REQ-401, REQ-402

## 推奨（実装中に用意できればOK）

実装を開始できますが、該当機能の実装前までに準備してください。

- [ ] **Firebase Consoleでのプロジェクト設定確認** 🟡 *Firebase運用ベストプラクティスから妥当な推測*
  - 手順:
    1. Firebase Console (https://console.firebase.google.com/) にアクセス
    2. プロジェクト「dashboard-2fcc1」を選択
    3. 「プロジェクトの設定」→「全般」でアプリが正しく登録されていることを確認
    4. 「Analytics」タブでGoogle Analyticsが有効になっていることを確認
  - 必要になるフェーズ: Phase 1（コアモジュール構築）
  - 関連要件: REQ-001

- [ ] **デバッグモード設定の準備（オプション）** 🟡 *Firebase開発ガイドラインから妥当な推測*
  - 手順:
    1. Android Studioで`adb shell setprop debug.firebase.analytics.app com.github.takayuki_hub.dashboard`を実行
    2. Firebase Console の「DebugView」でリアルタイムにイベントを確認できるようになる
  - 必要になるフェーズ: Phase 2（イベントトラッキング実装）
  - 関連要件: REQ-201~REQ-213

## 確認事項（判断が必要）

実装方針に影響するため、早めの判断・確認が推奨されます。

- [ ] **Firebase Analyticsのデータ保持期間の設定** 🟡 *Firebase運用ベストプラクティスから妥当な推測*
  - 背景: デフォルトでは2ヶ月間のデータ保持。長期分析が必要な場合は設定変更が必要
  - 選択肢:
    - デフォルト（2ヶ月）のまま使用
    - 14ヶ月に延長（Firebase Console で設定可能）
  - 判断の影響範囲: 長期的なユーザー行動分析の可否
  - 関連要件: 要件定義書には明記されていないが、運用上重要

- [ ] **BigQueryへのエクスポート設定の検討** 🟡 *Firebase高度な分析機能から妥当な推測*
  - 背景: Firebase AnalyticsデータをBigQueryにエクスポートすると、SQLでの高度な分析が可能
  - 選択肢:
    - 当面は不要（Firebase Consoleの標準レポートのみ使用）
    - BigQueryエクスポートを有効化（無料枠あり、大規模分析時は有料）
  - 判断の影響範囲: データ分析の柔軟性、コスト
  - 関連要件: 要件定義書には明記されていないが、将来的な拡張性に影響

---

## サマリー

| 優先度 | 件数 | 🔵 | 🟡 | 🔴 |
|--------|------|-----|-----|-----|
| 必須 | 2 | 1 | 1 | 0 |
| 推奨 | 2 | 0 | 2 | 0 |
| 確認事項 | 2 | 0 | 2 | 0 |

## 関連文書

- **要件定義書**: [requirements.md](requirements.md)
- **ヒアリング記録**: [interview-record.md](interview-record.md)

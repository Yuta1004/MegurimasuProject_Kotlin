# ユーティリティ
様々なクラスから扱いたい関数や変数を置く

## 関数
- getAgentPosFromQR(): Map<String, Array<Int>>
	- QRデータからエージェントの初期位置を読み込んで返す

- getScoreDataFromQR(): Array<Array<Int>>
	- QRデータからスコアデータを読み込んで返す

## 変数
- movementValue: Map<Integer, Map<String, Int>>
	- エージェントをある方向へ移動させるための移動量を持つ
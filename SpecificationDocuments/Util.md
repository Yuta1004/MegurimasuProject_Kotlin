# ユーティリティ
様々なクラスから扱いたい関数や変数を置く

## 関数
- getAgentPosFromQR(): Map<String, Array<Int>>
	- QRデータからエージェントの初期位置を読み込んで返す

- getScoreDataFromQR(): Array<Array<Int>>
	- QRデータからスコアデータを読み込んで返す

- getMovedPos(x: Int, y: Int, type: Int): Pair<Int, Int>
	- 引数typeの通りに行動を起こした時の影響を及ぼす座標を計算する

- getTeamID(agentName: String): Int
	- エージェントIDに対応したチームIDを返す
	- A -> 1, B -> 2

- calDict(x: Int, y: Int, x_1: Int, y_1: Int): Double
	- (x, y)と(x_1, y_1)の2点間の距離を計算して返す

## 変数
- movementValue: Map<Integer, Map<String, Int>>
	- エージェントをある方向へ移動させるための移動量を持つ
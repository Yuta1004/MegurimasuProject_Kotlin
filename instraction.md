# 座標
**左上を(0, 0)として，右方向へ行くほどx, 下方向へ行くほどyが増加していく**


***
# 巡りマスシミュレータ本体(MegurimasuSimulator)

## メンバ変数
- **[val private]** width, height: Int → フィールドのサイズ
- **[val private]** agents: Map<String, Agent> → エージェントクラス保持
- **[var]** scoreData: Array → フィールドのスコアデータ保持
- **[var]** encampmentData: Array → フィールドの陣地データ保持

## コンストラクタ
- agentInitPos: Map<String, Array<Int> → エージェントの初期位置．キーに対してArrayで座標を渡す
- scoreData: Array → フィールドのスコアデータ(QRで読み込んだもの)

## メソッド
- agentInit(agentInitPos): Map<String, Agent> **[private]**
	- コンストラクタで与えられたagentInitPosを元にエージェントクラスのMapを生成する
- getTeamID(agentName: String): Int **[private]**
	- エージェントIDに対応したチームIDを返す
	- A -> 1, B -> 2
- action(behavior: Map<String, Int>)
	- 引数behaviorには4つのキーを含めること["A_1", "A_2", "B_1", "B_2"]
	- それぞれのキーの値に基づいてエージェント・陣地情報を更新する
- actionSimulation(behavior: Map<String, Int>): Map<Stringm, Any> **[private]**
	- moveと基本仕様は同じ．
	- 引数behavior通りに行動を行った時のエージェントの座標や盤面情報，得点を返す
	- moveと異なり，メンバ変数を更新しない(動きのシミュレーションを行う)
- duplicateDetection(target: Map<String, Int>): Map<String, Boolean> **[private]**
  - 与えられたMapの値の重複をチェックする
  - 重複がある値のキーをtrueにして返す
- calScore(): Map<String, Int>
	- 得点を計算してArrayで返す
- recursionSearch(x: Int, y: Int, teamID: Int, argFillEncampment: Array<Array<Int>>?): Array<Array<Int>> **[private]**
	- 陣地スコア計算時に再帰探索を行う関数
	- ある地点から広がれる範囲へ次々に探索を行う
- isWithInRange(x: Int, y: Int): Boolean **[private]**
	- 引数x,yが正常な値かどうか(場外にはみ出ていないか)検査して結果をBoolで返す
- conversion(): String
	- 盤面情報を文字列にして返す
	- DataConversionクラスのconversionを呼ぶ


# エージェントクラス(Agent)
*巡りマスシミュレータ本体の内部クラスとして定義する*  
*シミュレータ本体のプロパティへアクセスを行う*

## メンバ変数
- agentName: String → エージェントの名前(ID)
- x: Int → エージェントの座標x
- y: Int → エージェントの座標y

## コンストラクタ
- agentName: String → エージェントの名前 (ID)
- x: Int → エージェントの座標x
- y: Int → エージェントの座標y

## メソッド
- action(type: Int): Boolean
	- 引数で指定された通りにメンバ変数(座標)を更新
	- 更新失敗時はfalseを返す
- canAction(type: Int): Boolean **[private]**
	- 引数type通りに行動を起こそうとした時，それが成功するかどうかを返す
	- 移動 → 移動先が場外でないか，相手のパネルでないか
	- パネル除去 → 除去をする場所が場外でないか
- takeActionPos(type: Int): Map<String, Int>
	- 引数type通りに行動を起こした時の，その影響を受ける座標を返す

# データ変換クラス(DataComversion)
陣地データやスコアデータ，エージェント位置などを文字列に
文字列データから陣地データやスコアデータなどに変換するクラス

## コンストラクタ
なし

## メンバ変数
なし

## メソッド
全てstaticなメソッド
- comversion(scoreData: Array<Array<Int>>, encampmentData: Array<Array<Int>>, agents: Map<String, MegurimasuSimulator.Agent>): String
    - 与えられたデータを文字列にして返す
- decomversion(target: String)
    - 与えられた文字列を盤面情報にして返す
- numAtoB(numStr: String, A: Int, B: Int): Int
    - A進数のnumStrをB進数に基数変換して返す 


# 移動量クラス(MovementValues)
*エージェントを移動させるために必要な移動量を保持する*  
*シミュレータの内部クラスとして定義するとメモリを無駄に消費するので外部に置く*


## メンバ変数
staticな変数valuesをもつ

## 変数へのアクセス
- エージェントを1方向へ移動させるために必要なx移動量を取得する
	- MovementValues.values[1]["x"]

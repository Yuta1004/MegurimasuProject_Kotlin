# データ変換クラス(util.DataConversion)
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

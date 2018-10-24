# 巡りマス GUIクラス(MegurimasuGUI.kt)
巡りマスの盤面情報などをGUIで表現するためのクラス

## コンストラクタ
- megurimasu: MegurimasuSimulator
    - 巡りマスシミュレータクラス(MegurimasuSimulator.kt)のインスタンス

## メンバ変数
- bufImage: Image?
    - 盤面情報を描画するために必要なもの
- logTextArea: textArea?
    - ログ描画用
- megurimasu: MegurimasuSimulator
    - 巡りマスシミュレータのインスタンス

## メンバメソッド
- updateBoard(megurimasu: MegurimasuSimulator)
    - GUI更新

- viewBestBehavior(bestBehavior: Map<String, Int>)
    - 最善手を別ウィンドウで表示する
    - ViewBestBehaviorクラスを使う

- writeLog(text: String)
    - ログ更新

- paint(g: Graphics)
    - JFrameクラス内のメソッドをオーバーライドしたもの
    - ウィンドウを再描画する時に呼ばれる

- update(g: Graphics?)
    - JFrameクラス内のメソッドをオーバーライドしたもの
    - ウィンドウを再描画する時に呼ばれる

- setUI() **[private]**
    - UIの部品を定義

- drawBoard() **[private]**
    - 盤面描画

- drawEncampment(g: Graphics2D, x: Int, y: Int) **[private]**
    - 指定座標の陣地情報を描画する

- drawScoreText(g: Graphics2D, argX: Int, argY:Int) **[private]**
    - 指定座標に陣地スコアを描画する

- getDrawCenterPos(g: Graphics2D, text: String, x: Int, y: Int): Pair<Int, Int> **[private]**
    - テキストをセンタリングする時の座標を計算して返す

- getEncampmentColor(x: Int, y: Int): Color **[private]**
    - 陣地情報を描画する時に必要な色を返す

# 最善手表示クラス(ViewBestBehavior.kt)
計算された最善手を別ウィンドウで表示するためのクラス
JFrameを継承する

## コンストラクタ
- bestBehavior: Map<String, Int>
    -　表示する最善手情報

## メンバ変数
- bufImage: Image?
    - 描画するために必要

- bestBehaviot: Mao<String,  Int>
    - 最善手情報

## メンバメソッド
- viewBestBehavior(bestBehavior: Map<String, Int> **[private]**
    - 最善手情報を描画する

- keyReleased(e: KeyEvent?)
    - キー入力を受け付けるメソッド
    - KeyListnerインタフェースを実装するために必ず実装しなきゃいけない
    - 空実装

- keyTyped(e: KeyEvent?)
    - キー入力を受け付けるメソッド
    - KeyListnerインタフェースを実装するために必ず実装しなきゃいけない
    - 空実装

- keyPressed(e: KeyEvent?)
    - ENTERが押されたらウィンドウを閉じるために実装

- paint(g: Graphics)
    - ウィンドウ更新の時に呼ばれる

- update(g: Graphics)
    - ウィンドウ更新の時の呼ばれる

- getDrawCenterPos(g: Graphics2D, text: String, x: Int, y: Int): Pair<Int, Int> **[private]**
    - テキストをセンタリングする時に必要な座標を計算して返す

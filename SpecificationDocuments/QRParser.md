# QRデータパースクラス(util.QRParser)
スコア情報，エージェントの初期位置が記録されたQRコードの内容を解析する
メソッドは全てstatic

## コンストラクタ
- qrText: String -> QRコード平文

## メソッド
- getAgentPos()
    - QRデータを解析してエージェントの初期位置を返す

- getScoreData()
    - QRデータを解析してスコア情報を返す

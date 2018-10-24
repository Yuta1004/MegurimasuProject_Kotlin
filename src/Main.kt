import manager.TCPConnectionManager
import solver.searchBestBehavior
import util.QRParser

var qrData = "Waiting"
var manualActionData = "Waiting"
var posData = "Waiting"
var depth = 1
var probability = arrayOf(5, 0, 0)
var manualControl = false

val tcpConnectionManager = TCPConnectionManager("localhost", 6666, ::tcpReceiver)
var megurimasu: MegurimasuSimulator? = null
var megurimasuGUI: MegurimasuGUI? = null

fun main(args: Array<String>){
    // 通信初期化処理
    tcpConnectionManager.receiveStart()
    println("スマートフォンに接続してください")
    while(!tcpConnectionManager.connecting){ Thread.sleep(50) }
    println("スマートフォンに接続されました")

    // TODO: ゲーム中でも変更できるようにする
    // 何手先まで読むか尋ねる
    print("何手先まで読むかを入力してください(推奨: 2 or 3) > ")
    val inpValue = readLine()?: "3"
    depth = inpValue.toInt()

    // 戦略を尋ねる
    listOf("ゴリ押し", "ストーカー", "ランダム").forEachIndexed { idx, strategy ->
        print("[${strategy}作戦] をいくつ採用するか入力してください > ")
        val inpStrategyUseValue = readLine()?: "2"
        probability[idx] = inpStrategyUseValue.toInt()
    }

    // QRデータ待機
    println("QRコードをアプリで撮影してください")
    while(qrData == "Waiting"){ Thread.sleep(5) }
//    qrData = "10 11:4 0 7 12 15 -11 15 12 7 0 4:13 11 13 3 7 12 7 3 13 11 13:0 5 6 13 5 6 5 13 6 5 0:8 10 -5 4 14 5 14 4 -5 10 8:14 15 10 5 -2 2 -2 5 10 15 14:14 15 10 5 -2 2 -2 5 10 15 14:8 10 -5 4 14 5 14 4 -5 10 8:0 5 6 13 5 6 5 13 6 5 0:13 11 13 3 7 12 7 3 13 11 13:4 0 7 12 15 -11 15 12 7 0 4:3 1:8 11:"
    println("QRデータを受信しました")

    // スコアデータとエージェント初期位置取得
    val qrParser = QRParser(qrData)
    val scoreData = qrParser.getScoreData()
    val agentPos = qrParser.getAgentPos()

    // MegurimasuSimulator初期化 & GUI初期化
    megurimasu = MegurimasuSimulator(agentPos, scoreData)
    megurimasuGUI = MegurimasuGUI(megurimasu!!)

    // 思考ループ
    val doLoop = true
    while(doLoop){
        // 次の手を探索
        val (maxScore, bestBehavior) = getNextBehavior()

        // メッセージ出力
        if(manualControl){
            writeLog("入力を受け付けました")
        }else {
            writeLog("探索が終了しました…")
            writeLog("盤面評価値：$maxScore")
            megurimasuGUI!!.viewBestBehavior(bestBehavior)
        }

        // 相手の行動が入力されるのを待機
        writeLog("相手エージェントの行動をアプリで入力してください")
        posData = "Waiting"
        while(posData == "Waiting"){ Thread.sleep(5) }
        writeLog("相手エージェントの行動情報を受信しました")

        // 相手の行動を取得
        val agentB1Action = posData.split(":")[0].toInt()
        val agentB2Action = posData.split(":")[1].toInt()

        // 場面更新
        val behavior = mapOf(
                "A_1" to bestBehavior["A_1"]!!, "A_2" to bestBehavior["A_2"]!!,
                "B_1" to agentB1Action, "B_2" to agentB2Action
        )
        megurimasu!!.action(behavior)
        megurimasuGUI!!.updateBoard(megurimasu!!)
    }
}

fun getNextBehavior(): Pair<Int, Map<String, Int>>{
    // マニュアルモードなら
    if(manualControl){
        // 入力されるまで待機
        writeLog("自チームの行動情報を入力してください")
        manualActionData = "Waiting"
        while(manualActionData == "Waiting"){ Thread.sleep(5) }

        // 情報取り出す
        val nextBehavior = mapOf(
                "A_1" to manualActionData.split(":")[0].toInt(),
                "A_2" to manualActionData.split(":")[1].toInt()
        )

        return Pair(0, nextBehavior)
    }

    // 最善手探索
    writeLog("最善手を探索しています…")
    return searchBestBehavior(megurimasu!!, depth, probability)
}

fun writeLog(text: String){
    if(megurimasuGUI == null) return
    megurimasuGUI!!.writeLog(text)
}

fun tcpReceiver(text: String) {
    when(text){
        "close" -> {
            writeLog("スマートフォンとの接続が切断されました")
            return
        }
        "reconnect" -> {
            writeLog("スマートフォンに接続できません．2秒後に再接続を試みます")
            return
        }
        "open" -> {
            writeLog("スマートフォンが接続されました")
            return
        }
    }

    val dividedText = text.split("@")
    val type = dividedText[0]
    val data = dividedText[1]

    when(type){
        "QRData" -> qrData = data
        "OpponentPos" ->{
            if(posData != "Waiting"){ return }
            posData = data
        }
        "ManualActionData" -> {
            if(manualActionData != "Waiting"){ return }
            manualActionData = data
        }
        "SwitchControl" -> {
            if(data == "Manual"){ manualControl = true; writeLog("＊＊＊モードが「マニュアル」になりました＊＊＊") }
            else if(data == "AI"){ manualControl = false; writeLog("＊＊＊モードが「オート」になりました＊＊＊") }
        }
    }
}
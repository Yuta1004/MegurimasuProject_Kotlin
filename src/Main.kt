import manager.TCPConnectionManager
import solver.searchBestBehavior
import util.QRParser

var qrData: String? = null
var posData: String? = null
var depth = 1
var probability = arrayOf(5, 0, 0)

val tcpConnectionManager = TCPConnectionManager("localhost", 6666, ::tcpReceiver)
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
    println()

    // QRデータ待機
    println("QRコードをアプリで撮影してください")
//    while(qrData == null){ Thread.sleep(5) }
    qrData = "10 11:4 0 7 12 15 -11 15 12 7 0 4:13 11 13 3 7 12 7 3 13 11 13:0 5 6 13 5 6 5 13 6 5 0:8 10 -5 4 14 5 14 4 -5 10 8:14 15 10 5 -2 2 -2 5 10 15 14:14 15 10 5 -2 2 -2 5 10 15 14:8 10 -5 4 14 5 14 4 -5 10 8:0 5 6 13 5 6 5 13 6 5 0:13 11 13 3 7 12 7 3 13 11 13:4 0 7 12 15 -11 15 12 7 0 4:3 1:8 11:"
    println("QRデータを受信しました")

    // スコアデータとエージェント初期位置取得
    val qrParser = QRParser(qrData!!)
    val scoreData = qrParser.getScoreData()
    val agentPos = qrParser.getAgentPos()

    // MegurimasuSimulator初期化 & GUI初期化
    val megurimasu = MegurimasuSimulator(agentPos, scoreData)
    megurimasuGUI = MegurimasuGUI(megurimasu)

    // 思考ループ
    val doLoop = true
    while(doLoop){
        // 最善手探索
        writeLog("最善手を探しています…")
        val (maxScore, bestBehavior) = searchBestBehavior(megurimasu, depth, probability)
        writeLog("探索が終了しました")
        writeLog("盤面評価値：$maxScore")
        megurimasuGUI!!.viewBestBehavior(bestBehavior)

        // 相手の行動が入力されるのを待機
        writeLog("相手エージェントの行動をアプリで入力してください")
        posData = "Waiting"
        while(posData == "Waiting"){ Thread.sleep(5) }
        writeLog("相手エージェントの行動情報を受信しました")

        // 相手の行動を取得
        val agentB1Action = posData!!.split(":")[0].toInt()
        val agentB2Action = posData!!.split(":")[1].toInt()
        posData = null

        // 場面更新
        val behavior = mapOf(
                "A_1" to bestBehavior["A_1"]!!, "A_2" to bestBehavior["A_2"]!!,
                "B_1" to agentB1Action, "B_2" to agentB2Action
        )
        megurimasu.action(behavior)
        megurimasuGUI!!.updateBoard(megurimasu)
    }
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
    }
}
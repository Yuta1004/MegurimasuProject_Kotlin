var qrData: String? = null
var posData: String? = null
var depth = 1
var probability = arrayOf(5, 0, 0)

fun main(args: Array<String>){
    TCPConnectionManager("localhost", 6666, ::tcpReceiver).receiveStart()

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
    while(qrData == null){ Thread.sleep(5) }
    println("QRデータを受信しました")

    // スコアデータとエージェント初期位置取得
    val qrParser = QRParser(qrData!!)
    val scoreData = qrParser.getScoreData()
    val agentPos = qrParser.getAgentPos()

    // MegurimasuSimulator初期化
    val megurimasu = MegurimasuSimulator(agentPos, scoreData)

    // 思考ループ
    val doLoop = true
    while(doLoop){
        // 最善手探索
        println("最善手を探しています…")
        val (maxScore, bestBehavior) = searchBestBehavior(megurimasu, depth, probability)
        printInfo(maxScore, bestBehavior, megurimasu)

        // 相手の行動が入力されるのを待機
        println("相手エージェントの行動をアプリで入力してください")
        posData = "Waiting"
        while(posData == "Waiting"){ Thread.sleep(5) }
        println("相手エージェントの行動情報を受信しました")

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
    }
}

fun printInfo( maxScore: Int, bestBehavior: Map<String, Int>, megurimasu: MegurimasuSimulator){
    println()
    println("---")
    println("最善手: A_1 -> ${bestBehavior["A_1"]}, A_2 -> ${bestBehavior["A_2"]}")
    println("盤面の評価値: $maxScore")
    println("現在の盤面情報: ")
    megurimasu.encampmentData.forEach { it.forEach { print("$it ") }; println() }
    println("現在のエージェント座標: ")
    megurimasu.agents.forEach { key, pos -> println("$key -> (${pos.x}, ${pos.y})") }
    println("スコア: A ${megurimasu.calScore()["A"]} vs ${megurimasu.calScore()["B"]} B")
    println("---")
}

fun tcpReceiver(text: String) {
    if(text == "close"){ System.exit(0) }

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
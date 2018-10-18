var qrData: String? = null
var posData: String? = null
var depth = 1
var probability = arrayOf(5, 0, 0)

fun main(args: Array<String>){
    TCPConnectionManager("localhost", 6666, ::tcpReceiver).receiveStart()

    // TODO: ゲーム中でも変更できるようにする
    // 何手先まで読むか尋ねる
    print("Please Input Search Depth > ")
    val inpValue = readLine()?: "3"
    depth = inpValue.toInt()

    // 戦略を尋ねる
    listOf("BruteForce", "Stalker", "PrayToGod").forEachIndexed { idx, strategy ->
        print("Use Strategy [$strategy] > ")
        val inpStrategyUseValue = readLine()?: "2"
        probability[idx] = inpStrategyUseValue.toInt()
    }
    println()

    // QRデータ待機
    println("Please Input QR Data")
    while(qrData == null){ Thread.sleep(5) }
    println("Received QR Data")

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
        println("Searching Best Behavior...")
        val (maxScore, bestBehavior) = searchBestBehavior(megurimasu, depth, probability)
        printInfo(maxScore, bestBehavior, megurimasu)

        // 相手の行動が入力されるのを待機
        println("Please Input Opponent Action")
        posData = "Waiting"
        while(posData == "Waiting"){ Thread.sleep(5) }
        println("Received Opponent Action Data")

        // 相手の行動を取得
        val agentB1Action = posData!!.split(":")[0].toInt()
        val agentB2Action = posData!!.split(":")[1].toInt()
        posData = null

        println("$agentB1Action, $agentB2Action")

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
    println("BestBehavior: A -> ${bestBehavior["A_1"]}, B -> ${bestBehavior["A_2"]}")
    println("MaxScore: $maxScore")
    println("EncampmentData: ")
    megurimasu.encampmentData.forEach { it.forEach { print("$it ") }; println() }
    println("AgentPos: ")
    megurimasu.agents.forEach { key, pos -> println("$key -> (${pos.x}, ${pos.y})") }
    println("Score: A ${megurimasu.calScore()["A"]} vs ${megurimasu.calScore()["B"]} B")
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
            println("Input")
        }
    }
}
var qrData: String? = null
var posData: String? = null
var depth = 1
var probability = arrayOf(5, 0, 0)

fun main(args: Array<String>){
    TCPConnectionManager("localhost", 6666, ::tcpReceiver).receiveStart()

    // QRデータ待機
    println("Please Input QR Data")
//    while(qrData == null){ Thread.sleep(100) }
    println("Received QR Data")

    qrData = "12 9:13 14 12 6 14 6 12 14 13:2 0 10 13 15 13 10 0 2:7 10 14 0 3 0 14 10 7:4 13 14 11 6 11 14 13 4:8 7 0 6 2 6 0 7 8:8 4 9 11 8 11 9 4 8:8 4 9 11 8 11 9 4 8:8 7 0 6 2 6 0 7 8:4 13 14 11 6 11 14 13 4:7 10 14 0 3 0 14 10 7:2 0 10 13 15 13 10 0 2:13 14 12 6 14 6 12 14 13:5 3:8 7:"

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
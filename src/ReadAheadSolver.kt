import java.lang.IndexOutOfBoundsException
import java.util.Random;
import kotlin.math.max
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

val random = Random()

fun main(args: Array<String>){
    val qrDecorder = QRParser("8 11:-2 1 0 1 2 0 2 1 0 1 -2:1 3 2 -2 0 1 0 -2 2 3 1:1 3 2 1 0 -2 0 1 2 3 1:2 1 1 2 2 3 2 2 1 1 2:2 1 1 2 2 3 2 2 1 1 2:1 3 2 1 0 -2 0 1 2 3 1:1 3 2 -2 0 1 0 -2 2 3 1:-2 1 0 1 2 0 2 1 0 1 -2:2 2:7 10:")
    val agentPos = qrDecorder.getAgentPos()
    val scoreData = qrDecorder.getScoreData()
    val megurimasu = MegurimasuSimulator(agentPos, scoreData)

    // 速度計算・デモ用
    for(i: Int in 0 until 100) {
        var result = Pair(0, mapOf("Null" to 0))
        val time = measureTimeMillis { result = searchBestBehavior(megurimasu, 3, arrayOf(3, 2, 1)) }
        val (maxScore, bestBehavior) = result

        println("Time: $time ms")
        println("MaxScore: $maxScore")
        println("BestBehavior: A_1 -> ${bestBehavior["A_1"]}, A_2 -> ${bestBehavior["A_2"]}")
//        println()

        val behavior = mapOf(
                "A_1" to bestBehavior["A_1"]!!,
                "A_2" to bestBehavior["A_2"]!!,
                "B_1" to random.nextInt(8),
                "B_2" to random.nextInt(8)
        )
        megurimasu.action(behavior)
        val score = megurimasu.calScore()
        println("Score: A -> ${score["A"]}, B -> ${score["B"]}")
        megurimasu.encampmentData.forEach { it.forEach { print("$it ") }; println() }
        println()
    }
}

// 再帰でより良い手を探す
fun searchBestBehavior(megurimasu: MegurimasuSimulator, depth: Int, probability: Array<Int>): Pair<Int, Map<String, Int>>{
    // 葉ならスコアを計算して返す
    if(depth == 0){
        val score = megurimasu.calScore()
        return Pair(score["A"]!! - score["B"]!!, mapOf())
    }

    // 次の手を列挙
    val agentsAction = listOf("A_1", "A_2", "B_1", "B_2")
            .map{ agentName ->
                val bruteforce = strategyOfBruteForce(megurimasu, agentName, probability[0])
                val stalker = strategyOfStalker(megurimasu, agentName, probability[1])
                val prayToGod = strategyOfPrayToGod(probability[2])

                agentName to bruteforce + stalker + prayToGod
            }
            .toMap()

    // それぞれのエージェントが選択した手を合わせて次の盤面を決める
    val nextBehaviors = arrayListOf<Map<String, Int>>()
    val total = probability.sum()
    for(i: Int in 0 until total * total){
        nextBehaviors.add(mapOf(
                "A_1" to agentsAction["A_1"]!![i / total],
                "A_2" to agentsAction["A_2"]!![i % total],
                "B_1" to agentsAction["B_1"]!![i / total],
                "B_2" to agentsAction["B_2"]!![i % total]
        ))
    }

    // リードが一番大きくなるような手を見つける
    val nowBoard = megurimasu.conversion()
    var maxScore = -99
    val bestBehavior = nextBehaviors
            .asSequence()
            .maxBy { it ->
                megurimasu.action(it)
                val (score, _) = searchBestBehavior(megurimasu, depth - 1, probability)
                megurimasu.deconversion(nowBoard)

                maxScore = max(score, maxScore)
                score
            }!!

    return Pair(maxScore, mapOf("A_1" to bestBehavior["A_1"]!!, "A_2" to bestBehavior["A_2"]!!))
}

fun strategyOfBruteForce(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>{
    val actionedScoreList = arrayListOf<Array<Int>>()
    for(i in 0..7){
        var _i = i
        val movableList = listOf(0, 1, 2, 3, 4, 5, 6, 7).filter { it -> it != (i+4)%8 }

        // 現在の盤面から1つ手を選択した時，それに対して新たに手を選択した合計2手のスコアを計算して集計する
        // 必要なのは1手後の情報だけなので，2手後の選択については特に選択した手の保持などをしない
        val maxValue = arrayOf(-99, 0)
        movableList.forEach{ type ->
            // 必要な座標を取得
            val agentX = megurimasu.agents[agentName]!!.x
            val agentY = megurimasu.agents[agentName]!!.y
            val (actionX, actionY) = getActionPos(agentX, agentY, i)
            val (actionXTwo, actionYTwo) = getActionPos(actionX, actionY, type)

            // 範囲外
            try { megurimasu.encampmentData[actionY][actionX]; megurimasu.encampmentData[actionYTwo][actionXTwo]}
            catch (e: ArrayIndexOutOfBoundsException){ return@forEach }

            // 既に自分の陣地であるか敵の陣地だった場合は負の評価を与えたのちに集計する
            var score = megurimasu.scoreData[actionY][actionX] + megurimasu.scoreData[actionYTwo][actionXTwo]
            when(megurimasu.encampmentData[actionY][actionX]){
                0 -> { }
                getTeamID(agentName) -> score -= 10
                else -> {score -= 6; _i += 10}
            }

            // 最大値更新
            if(maxValue[0] < score){
                maxValue[0] = score
                maxValue[1] = _i
            }

            if(_i > 10){ _i %= 10 }
        }

        actionedScoreList.add(maxValue)
    }

    // スコアを降順にソートして指定数だけ選択してそのidxを返す
    return actionedScoreList
            .asSequence()
            .sortedByDescending { ( score, _) -> score }
            .take(num)
            .map { it[1] }
            .toList()
}

fun strategyOfStalker(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>{
    // 存在しないエージェントの名前が引数で与えられたとき時は全てが8のListを返す
    if(agentName !in megurimasu.agents.keys){
        return Array(num){ _ -> 8}.toList()
    }

    // 一番近い敵エージェントを探す
    val enemyTeam = if("A" in agentName) "B" else "A"
    val agent = megurimasu.agents[agentName]!!
    val enemyAgents = arrayOf(megurimasu.agents["${enemyTeam}_1"]!!, megurimasu.agents["${enemyTeam}_2"]!!)
    val minDistAgent = enemyAgents
            .minBy { calDist(agent.x, agent.y, it.x, it.y) }!!

    // 一番近いエージェントに近づくための行動タイプを探す
    val meAgentDegree = calDegree2Points(agent.x, agent.y, minDistAgent.x, minDistAgent.y).toInt()
    val optimalActionType = (meAgentDegree % 360 / 45 + 2) % 8

    // 評価の高いものから順にListに放り込む
    val retList = mutableListOf(optimalActionType)
    for(i: Int in 1..4){
        retList.add((optimalActionType + i + 8) % 8)
        retList.add((optimalActionType + (i * -1) + 8) % 8)
    }

    // 敵陣地だった場合はパネル除去を行うように
    retList.forEachIndexed { idx, elem  ->
        val agentX = megurimasu.agents[agentName]!!.x
        val agentY = megurimasu.agents[agentName]!!.y
        val (actionX, actionY) = getActionPos(agentX, agentY, elem)

        // 範囲外
        try { megurimasu.encampmentData[actionY][actionX]}
        catch (e: IndexOutOfBoundsException){ return@forEachIndexed }

        if(!listOf(0, getTeamID(agentName)).contains(megurimasu.encampmentData[actionY][actionX])){
            retList[idx] += 10
        }
    }

    return retList.take(num)
}

fun strategyOfPrayToGod(num: Int): List<Int>{
    // ランダムに値を選択してListに詰めて返す
    val retList = mutableListOf<Int>()
    for(i in 0 until num){
        var randValue = 0
        do{
            randValue = random.nextInt(8) + random.nextInt(2) * 10
        }while(retList.contains(randValue))

        retList.add(randValue)
    }

    return retList
}
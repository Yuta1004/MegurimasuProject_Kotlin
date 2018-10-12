import java.lang.IndexOutOfBoundsException
import java.util.Random;
import kotlin.math.max

val random = Random()

fun main(args: Array<String>){
    val agentPos = getAgentPosFromQR()
    val scoreData = getScoreDataFromQR()
    val megurimasu = MegurimasuSimulator(agentPos, scoreData)
}

fun strategyOfBruteForce(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>{
    val moveScoreList = arrayListOf<Int>()
    for(i in 0..7){
        val movableList = listOf(0, 1, 2, 3, 4, 5, 6, 7).filter { it -> it != (i+4)%8 }

        // 現在の盤面から1つ手を選択した時，それに対して新たに手を選択した合計2手のスコアを計算して集計する
        // 必要なのは1手後の情報だけなので，2手後の選択については特に選択した手の保持などをしない
        var maxValue = -99
        movableList.forEach{ type ->
            val agentX = megurimasu.agents[agentName]!!.x
            val agentY = megurimasu.agents[agentName]!!.y
            val (movedX, movedY) = getMovedPos(agentX, agentY, i)
            val (movedXTwo, movedYTwo) = getMovedPos(movedX, movedY, type)

            try {
                maxValue = max(megurimasu.scoreData[movedY][movedX] + megurimasu.scoreData[movedYTwo][movedXTwo], maxValue)
            } catch (e: IndexOutOfBoundsException) {
                // 要素外参照エラー
                // このエラーが起きた時は集計しない
            }
        }

        moveScoreList.add(maxValue)
    }

    // スコアを降順にソートして指定数だけ選択してそのidxを返す
    return moveScoreList
            .toIntArray()
            .mapIndexed{ idx, elem -> idx to elem }
            .sortedByDescending { ( _, value) -> value }
            .take(num)
            .map { it.first }
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

    return retList.take(num)
}

fun strategyOfPrayToGod(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>{
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
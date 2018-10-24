import util.DataConversion
import util.getActionPos
import util.getTeamID
import kotlin.math.abs

class MegurimasuSimulator(agentInitPos: Map<String, Array<Int>>, var scoreData: Array<Array<Int>>){
    private var width = scoreData[0].size
    private var height = scoreData.size
    var agents = agentInit(agentInitPos)
    var encampmentData = arrayOf<Array<Int>>()

    inner class Agent(private val agentName: String, var x: Int, var y: Int) {
        fun action(type: Int): Boolean {
            if(!canAction(type)) return false

            when(type){
                // 移動
                in 0..7 ->{
                    val movedValues = takeActionPos(type)
                    x = movedValues["x"]!!
                    y = movedValues["y"]!!
                    encampmentData[y][x] = getTeamID(agentName)
                }
                // パネル除去
                in 10..17 ->{
                    val movedValues = takeActionPos(type)
                    encampmentData[movedValues["y"]!!][movedValues["x"]!!] = 0
                }
            }

            return true
        }

        private fun canAction(type: Int): Boolean {
            if(type !in 0..8 && type !in 10..18) return false

            val (xCopy, yCopy) = getActionPos(x, y, type % 10)

            if(!isWithInRange(xCopy, yCopy)){ return false }
            val encampment = encampmentData[yCopy][xCopy]

            when(type){
                // 移動の場合: 移動先が敵の陣地であれば(=自分の陣地でないかつ空白ではない)場合は移動不許可
                in 0..8 -> {
                    if(encampment != getTeamID(agentName) && encampment != 0){ return false }
                }

                // パネル除去の場合: 移動先が空白の場合は除去不許可
                else -> if(encampment == 0){ return false }
            }

            return true
        }

        fun takeActionPos(type: Int): Map<String, Int>{
            // typeが範囲外であったり行動できなかったりする場合は計算せずに返す
            if(type !in 0..8 && type !in 10..18){ return mapOf("x" to 0, "y" to 0) }
            if(!canAction(type)){ return mapOf("x" to x, "y" to y)}

            val (retX, retY) = getActionPos(x, y, type % 10)
            return mapOf("x" to retX, "y" to retY)
        }
    }

    init{
        // 盤面初期化
        encampmentData = Array(scoreData.size) { _ -> Array(scoreData[0].size) {0}}
        agents.forEach { key, pos ->
            encampmentData[pos.y][pos.x] = getTeamID(key)
        }
    }

    private fun agentInit(agentInitPos: Map<String, Array<Int>>): Map<String, Agent>{
        val agents = mutableMapOf<String, Agent>()
        agentInitPos.forEach { key, pos ->
            agents[key] = Agent(key, pos[0], pos[1])
        }

        return agents
    }

    fun action(behavior: Map<String, Int>){
        val takeActionPositions = mutableMapOf<String, Int>()

        // 行動後の座標を取得する
        actionSimulation(behavior).forEach { agentName, pos ->
            takeActionPositions[agentName] = pos["x"]!!*100 + pos["y"]!!
        }

        // エージェントを行動させる(重複してないかつ条件を満たしたものだけ)
        duplicateDetection(takeActionPositions)
                .forEach { agentName, isDuplicate ->
                    if(isDuplicate || !agents.containsKey(agentName) || !behavior.containsKey(agentName)) {
                        return@forEach
                    }
                    agents[agentName]!!.action(behavior[agentName]!!)
                }

        // エージェント位置反映
        agents.forEach { agentName, pos ->
            encampmentData[pos.y][pos.x] = getTeamID(agentName)
        }
    }

    private fun actionSimulation(behavior: Map<String, Int>): Map<String, Map<String, Int>>{
        val takeActionPositions = mutableMapOf<String, Map<String, Int>>()
        behavior.forEach { agentName, type ->
            if(!agents.containsKey(agentName)){ return@forEach }
            takeActionPositions[agentName] = agents[agentName]!!.takeActionPos(type)
        }

        return takeActionPositions
    }

    private fun duplicateDetection(target: Map<String, Int>): Map<String, Boolean>{
        // 重複があればduplicateCheckMapの値がtrueになる
        val duplicateCheckMap = mutableMapOf<String, Boolean>()
        target.forEach { agentName, value ->
            duplicateCheckMap[agentName] = target.count { it.value == value} >= 2
        }

        return duplicateCheckMap
    }

    fun calScore(): Map<String, Int>{
        val score = mutableMapOf("A" to 0, "B" to 0)
        var flatScoreDara = scoreData.flatten().toIntArray()

        // パネルスコア
        score.forEach { key, _ ->
            val teamID = getTeamID(key)
            score[key] = flatScoreDara
                    .filterIndexed { idx, _ -> encampmentData[idx/width][idx%width] == teamID; }
                    .sum()
        }

        // 陣地スコア
        flatScoreDara = flatScoreDara.map { abs(it) }.toIntArray()
        arrayOf("A", "B").forEach { teamIDStr ->
            var fillEncampment: Array<Array<Int>>? = Array(height) { _ -> Array(width){0} }
            val teamID = getTeamID(teamIDStr)

            // 外周を除いた全ての座標を起点として陣地探索をする(再帰)
            for(y in 1 until height-1){
                for(x in 1 until width-1) {
                    if(fillEncampment!![y][x] == 1 || encampmentData[y][x] == teamID){ continue }

                    // 探索結果がnullなら探索失敗，fillEncampmentを元に戻す
                    val copyFillEncampment = fillEncampment.map{ it.clone() }.toTypedArray()
                    fillEncampment = recursionSearch(x, y, teamID, fillEncampment)?: copyFillEncampment
                }
            }

            // 探索結果をスコアに反映
            val encScore = flatScoreDara
                    .filterIndexed { idx, _ -> fillEncampment!![idx/width][idx%width] == 1 }
                    .sum()
            score[teamIDStr] = score[teamIDStr]!!.plus(encScore)
        }

        return score
    }

    private fun recursionSearch(x: Int, y: Int, teamID: Int, argFillEncampment: Array<Array<Int>>?): Array<Array<Int>>?{
        if(x == 0 || x == width-1 || y == 0 || y == height-1 || argFillEncampment == null){
            return null
        }

        // 探索済みにする
        argFillEncampment[y][x] = 1

        var fillEncampment = argFillEncampment
        val moveXList = listOf(x, x, x-1, x+1)
        val moveYList = listOf(y-1, y+1, y, y)

        for(i in 0 until 4){
            val _x = moveXList[i]
            val _y = moveYList[i]

            // 移動先がステージ内 and 探索先の場所が自分の陣地でない and すでに探索済みでなければ探索続行
            // nullが返ってきたらそのまま返す
            if(isWithInRange(_x, _y) && encampmentData[_y][_x] != teamID && fillEncampment!![_y][_x] == 0){
                fillEncampment = recursionSearch(_x, _y, teamID, fillEncampment)
                if(fillEncampment == null){ return null }
            }
        }

        return fillEncampment
    }

    private fun isWithInRange(x: Int, y: Int): Boolean{
        return (x in 0..(width - 1)) && (y in 0..(height-1))
    }

    fun conversion(): String{
        return DataConversion.conversion(scoreData, encampmentData, agents)
    }

    @Suppress("UNCHECKED_CAST")
    fun deconversion(target: String){
        val stageData = DataConversion.deconversion(target)

        width = stageData["width"] as Int
        height = stageData["height"] as Int
        encampmentData = stageData["encampmentData"] as Array<Array<Int>>
        agents = agentInit(stageData["agentPos"] as Map<String, Array<Int>>)
    }
}
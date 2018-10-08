class MegurimasuSimulator(agentInitPos: Map<String, Array<Int>>, val scoreData: Array<Array<Int>>){
    val width = scoreData[0].size
    val height = scoreData.size
    val agents = agentInit(agentInitPos)
    var encampmentData = arrayOf<Array<Int>>()

    inner class Agent(private val agentName: String, var x: Int, var y: Int) {
        fun action(type: Int): Boolean {
            if(!canAction(type)) return false

            val movedValues = takeActionPos(type)
            x = movedValues["x"]!!
            y = movedValues["y"]!!

            return true
        }

        fun canAction(type: Int): Boolean {
            if(type !in 0..8 && type !in 10..18) return false

            val xCopy = x + MovementValues.values[type%10]!!["x"]!!
            val yCopy = y + MovementValues.values[type%10]!!["y"]!!

            if((xCopy < 0 || width < xCopy) || (yCopy < 0 || height < yCopy)){ return false }
            val encampmant = encampmentData[yCopy][xCopy]
            if(encampmant != getTeamID(agentName) && encampmant != 0){ return false }

            return true
        }

        fun takeActionPos(type: Int): Map<String, Int>{
            if(type !in 0..8 && type !in 10..18){ return mapOf("x" to 0, "y" to 0) }
            if(!canAction(type)){ return mapOf("x" to x, "y" to y)}

            return mapOf(
                    "x" to x + MovementValues.values[type%10]!!["x"]!!,
                    "y" to y + MovementValues.values[type%10]!!["y"]!!
            )
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

        return agents.toMap()
    }

    private fun getTeamID(agentName: String): Int{
        return when(agentName){
            "A_1", "A_2" -> 1
            "B_1", "B_2" -> 2
            else -> 0
        }
    }

    fun action(behavior: Map<String, Int>){
        // 行動後の座標を取得する
        val takeActionPositions = mutableMapOf<String, Int>()
        actionSimulation(behavior).forEach { agentName, pos ->
            takeActionPositions[agentName] = pos["x"]!!*10 + pos["y"]!!
        }

        // 重複を記録する
        // 重複があればoverLapAgentsの値がtrueになる
        val overLapAgents = mutableMapOf<String, Boolean>()
        takeActionPositions.forEach { agentName, value ->
            overLapAgents[agentName] = takeActionPositions.count { it.value == value} >= 2
        }

        // 行動をとる座標が重複していないエージェントを動かす
        overLapAgents.forEach { agentName, isOverLap ->
            if(isOverLap || !agents.containsKey(agentName) || !behavior.containsKey(agentName)) {
                return@forEach
            }

            agents[agentName]!!.action(behavior[agentName]!!)
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

    fun calScore(): Map<String, Int>{
        return mapOf("A" to 0, "B" to 0)
    }
}

class MovementValues{
    companion object {
        val values = mapOf(
                8 to mapOf("x" to 0,  "y" to 0),
                0 to mapOf("x" to 0,  "y" to -1),
                1 to mapOf("x" to 1,  "y" to -1),
                2 to mapOf("x" to 1,  "y" to 0),
                3 to mapOf("x" to 1,  "y" to 1),
                4 to mapOf("x" to 0,  "y" to 1),
                5 to mapOf("x" to -1, "y" to 1),
                6 to mapOf("x" to -1, "y" to 0),
                7 to mapOf("x" to -1, "y" to -1)
        )
    }
}
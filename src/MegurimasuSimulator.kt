class MegurimasuSimulator(agentInitPos: Map<String, Array<Int>>, val scoreData: Array<Array<Int>>){
    val width = scoreData[0].size
    val height = scoreData.size
    val agents = agentInit(agentInitPos)
    var encampmentData = arrayOf<Array<Int>>()

    inner class Agent(private val agentName: String, var x: Int, var y: Int) {
        fun move(type: Int): Boolean {
            if(!canAction(type)) return false

            val movedValues = takeActionPos(type)
            x = movedValues["x"]!!
            y = movedValues["y"]!!

            return true
        }

        fun canAction(type: Int): Boolean {
            if(type !in 0..8 && type !in 10..18) return false

            val xCopy = takeActionPos(type)["x"]!!
            val yCopy = takeActionPos(type)["y"]!!

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

    fun move(behavior: Map<String, Int>): Boolean{
        return false
    }

    fun moveSimulation(behavior: Map<String, Int>): Boolean{
        return false
    }

    fun calScore(): Map<String, Int>{
        return mapOf("A" to 0, "B" to 0)
    }
}

class MovementValues{
    companion object {
        val values = mapOf(
                1 to mapOf("x" to 0,  "y" to 0),
                2 to mapOf("x" to 0,  "y" to -1),
                3 to mapOf("x" to 1,  "y" to -1),
                4 to mapOf("x" to 1,  "y" to 0),
                5 to mapOf("x" to 1,  "y" to 1),
                6 to mapOf("x" to 0,  "y" to 1),
                7 to mapOf("x" to -1, "y" to 1),
                8 to mapOf("x" to -1, "y" to 0),
                0 to mapOf("x" to -1, "y" to -1)
        )
    }
}
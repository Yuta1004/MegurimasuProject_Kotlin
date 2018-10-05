class MegurimasuSimulator(agentInitPos: Map<String, Array<Int>>, val scoreData: Array<Array<Int>>){
    val agents = agentInit(agentInitPos)
    var encampmentData = arrayOf<Array<Int>>()

    inner class Agent(argX: Int, argY: Int) {
        var x = argX
        var y = argY

        fun move(type: Int): Boolean {
            return false
        }

        fun canMove(type: Int): Boolean {

            return false
        }
    }

    init{
        
    }

    private fun agentInit(agentInitPos: Map<String, Array<Int>>): Map<String, Agent>{
        return mapOf("A_1" to Agent(0, 0))
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
                0 to mapOf("x" to 0, "y" to 0),
                1 to mapOf("x" to 0, "y" to -1),
                2 to mapOf("x" to 1, "y" to -1),
                3 to mapOf("x" to 1, "y" to 0),
                4 to mapOf("x" to 1, "y" to 1),
                5 to mapOf("x" to 0, "y" to 1),
                6 to mapOf("x" to -1, "y" to 1),
                7 to mapOf("x" to -1, "y" to 0),
                8 to mapOf("x" to -1, "y" to -1)
        )
    }
}
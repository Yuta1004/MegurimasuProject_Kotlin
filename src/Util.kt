import kotlin.math.sqrt

fun getAgentPosFromQR(): Map<String, Array<Int>>{
    return mapOf(
            "A_1" to arrayOf(0, 1),
            "A_2" to arrayOf(0, 2),
            "B_1" to arrayOf(0, 3),
            "B_2" to arrayOf(0, 4)
    )
}

fun getScoreDataFromQR(): Array<Array<Int>>{
    return Array(12) { _ -> Array(12) {random.nextInt(32)-16}}
}

fun getMovedPos(x: Int, y: Int, type: Int): Pair<Int, Int>{
    if(x%10 !in 0..8 || y%10 !in 0..8){ return Pair(0, 0) }

    return Pair(
            x + movementValues[type]!!["x"]!!,
            y + movementValues[type]!!["y"]!!
    )
}

fun getTeamID(agentName: String): Int{
    return when(agentName){
        "A_1", "A_2", "A" -> 1
        "B_1", "B_2", "B" -> 2
        else -> 0
    }
}

fun calDist(x: Int, y: Int, x_1: Int, y_1:Int): Double{
    return sqrt( ((x-x_1) * (x-x_1) + (y-y_1) * (y-y_1)).toDouble() )
}

val movementValues = mapOf(
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
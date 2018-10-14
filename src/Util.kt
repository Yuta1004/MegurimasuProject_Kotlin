import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.math.atan2

fun getActionPos(x: Int, y: Int, type: Int): Pair<Int, Int>{
    if(type%10 !in 0..8 && type%10 !in 10..18){ return Pair(0, 0) }

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

fun calDegree2Points(x: Double, y: Double, x_2: Double, y_2: Double): Double{
    var degree = atan2(y_2-y, x_2-x) * 180 / PI
    if(degree < 0){
        degree += 360
    }

    return degree
}

fun calDegree2Points(x: Int, y: Int, x_2: Int, y_2: Int): Double{
    return calDegree2Points(x.toDouble(), y.toDouble(), x_2.toDouble(), y_2.toDouble())
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
import java.util.Random;

val random = Random()

fun main(args: Array<String>){
    val agentPos = getAgentPosFromQR()
    val scoreData = getScoreDataFromQR()
    val megurimasu = MegurimasuSimulator(agentPos, scoreData)
}

fun strategyOfBruteForce(agentName: String, num: Int): List<Int>{
    return listOf()
}

fun strategyOfStalker(agentName: String, num: Int): List<Int>{
    return listOf()
}

fun strategyOfPrayToGod(agentName: String, num: Int): List<Int>{
    return listOf()
}

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
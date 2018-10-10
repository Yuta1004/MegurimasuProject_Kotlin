import java.util.Random;

val random = Random()

fun main(args: Array<String>){
    val agentPos = getAgentPosFromQR()
    val scoreData = getScoreDataFromQR()
    val megurimasu = MegurimasuSimulator(agentPos, scoreData)
}

fun strategyOfBruteForce(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>{
    return listOf()
}

fun strategyOfStalker(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>{
    return listOf()
}

fun strategyOfPrayToGod(megurimasu: MegurimasuSimulator, agentName: String, num: Int): List<Int>{
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
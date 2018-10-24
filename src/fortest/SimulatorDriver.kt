package fortest

import MegurimasuSimulator
import java.util.Random
import kotlin.system.measureTimeMillis

fun main(args: Array<String>){
    val agentInitPos = mapOf(
            "A_1" to arrayOf(0, 1),
            "A_2" to arrayOf(0, 2),
            "B_1" to arrayOf(0, 3),
            "B_2" to arrayOf(0, 4)
        )

    val randomGenerator = Random()
    val scoreData = Array(12) { _ -> Array(12) { randomGenerator.nextInt(32)-16 } }

    val megurimasu = MegurimasuSimulator(agentInitPos, scoreData)

    val timeAction = arrayListOf<Long>()
    val timeCalScore = arrayListOf<Long>()
    val timeConversion = arrayListOf<Long>()
    val timeDeConversion = arrayListOf<Long>()

    val loopCount = 100000
    for(cnt in 0 until loopCount) {
        val behavior = mapOf(
                "A_1" to randomGenerator.nextInt(8) + randomGenerator.nextInt(2)*10,
                "A_2" to randomGenerator.nextInt(8) + randomGenerator.nextInt(2)*10,
                "B_1" to randomGenerator.nextInt(8) + randomGenerator.nextInt(2)*10,
                "B_2" to randomGenerator.nextInt(8) + randomGenerator.nextInt(2)*10
        )

        megurimasu.action(behavior)
        megurimasu.calScore()

//        if(cnt < 10){ continue }
        var str = ""
        timeAction.add(measureTimeMillis { megurimasu.action(behavior) } )
        timeCalScore.add(measureTimeMillis { megurimasu.calScore() } )
        timeConversion.add(measureTimeMillis { str = megurimasu.conversion() })
        timeDeConversion.add(measureTimeMillis { megurimasu.deconversion(str) })

        if(cnt % 1000 == 0){
            println("step: $cnt")
            println(str)
            println("action():\t${timeAction.average()} ms")
            println("calSCore():\t${timeCalScore.average()} ms")
            println("timeConversion():\t${timeConversion.average()} ms")
            println("timeDeConversion():\t${timeDeConversion.average()} ms")
            println()
        }
    }

    val convedStr = megurimasu.conversion()
    println(convedStr)
    println("action():\t${timeAction.average()} ms")
    println("calSCore():\t${timeCalScore.average()} ms")
    println("timeConversion():\t${timeConversion.average()} ms")
    println("timeDeConversion():\t${timeDeConversion.average()} ms")
}

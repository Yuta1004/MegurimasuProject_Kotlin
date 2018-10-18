class QRParser(qrText: String){
    private val qrData = qrText.split(":")
    private val stageSizeInfo = qrData[0].split(" ")
    private val height = stageSizeInfo[0].toInt()

    fun getAgentPos(): Map<String, Array<Int>>{
        // わかりやすさを優先してループを使わない
        val agentA1Pos = qrData[height+1].split(" ")
        val agentA1PosY = agentA1Pos[0].toInt() - 1
        val agentA1PosX = agentA1Pos[1].toInt() - 1

        val agentA2Pos = qrData[height+2].split(" ")
        val agentA2PosY = agentA2Pos[0].toInt() - 1
        val agentA2PosX = agentA2Pos[1].toInt() - 1

        return mapOf(
                "A_1" to arrayOf(agentA1PosX, agentA1PosY),
                "A_2" to arrayOf(agentA2PosX, agentA2PosY),
                "B_1" to arrayOf(agentA1PosX, agentA2PosY),
                "B_2" to arrayOf(agentA2PosX, agentA1PosY)
        )
    }

    fun getScoreData(): Array<Array<Int>>{
        val scoreData = arrayListOf<Array<Int>>()

        qrData.forEachIndexed{ idx, line ->
            if(idx == 0 || height < idx){ return@forEachIndexed }

            val scoreLine = line
                    .split(" ")
                    .map { it -> it.toInt() }
                    .toTypedArray()
            scoreData.add(scoreLine)
        }

        return scoreData.toTypedArray()
    }
}

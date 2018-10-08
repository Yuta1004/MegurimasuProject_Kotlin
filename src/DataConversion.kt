class DataConversion {
    companion object {
        fun conversion(scoreData: Array<Array<Int>>, encampmentData: Array<Array<Int>>, agents: Map<String, MegurimasuSimulator.Agent>): String {
            var conversionStr = ""
            val width = scoreData[0].size
            val height = scoreData.size

            // ステージデータ(幅，高さ)
            conversionStr = "${width.toString(36)}:${height.toString(36)}:"

            // スコアデータ
            scoreData.forEach { array ->
                conversionStr += array
                        .map{ (it+16).toString(36) }
                        .reduce { s1, s2 -> s1 + s2 } + ":"
            }

            // 陣地データ
            encampmentData.forEach { array ->
                val binStr = array
                        .map{ String.format("%2s", it.toString(2)).replace(" ", "0") }
                        .reduce { s1, s2 -> s1 + s2 }
                conversionStr += Integer.parseInt(binStr, 2).toString(36) + ":"
            }

            // エージェントデータ
            conversionStr += agents
                    .map{ it.value.x.toString(36) + it.value.y.toString(36) + ":" }
                    .reduce { s1, s2 -> "$s1$s2" }

            return conversionStr
        }

        fun deconversion(target: String) {
            val splitTarget = target.split(":")

            // ステージ情報(幅，高さ)
            val width = numAtoB(splitTarget[0], 36, 10)
            val height = numAtoB(splitTarget[1], 36, 10)

            // スコアデータ
            // 32進数の文字それぞれを10進数に直して16を引く
            val scoreData = Array(height) { _ -> arrayListOf<Int>()}
            for(i in 0 until height){
                splitTarget[i+2].forEach { char ->
                    scoreData[i].add(numAtoB(char.toString(), 36, 10) - 16)
                }
            }

            // 陣地データ
            // 36進数を2進数に変換した後，2個ずつ数字を連結してそれを10進数に直す
            val encampmentData = Array(height) { _ -> arrayListOf<Int>()}
            for(i in 0 until height){
                var binStr = Integer.parseInt(splitTarget[i+2+height], 36).toString(2)
                binStr = String.format("%"+(height*2)+"s", binStr).replace(" ", "0")

                for(charIdx in 0 until width*2 step 2){
                    encampmentData[i].add("${binStr[charIdx]}${binStr[charIdx+1]}".toInt(2))
                }
            }

            // エージェントデータ
            // それぞれの対応桁を取り出す
            val agentPos = mutableMapOf<String, Array<Int>>()
            val agentNames = listOf("A_1", "A_2", "B_1", "B_2")
            for(i in 0 until 4){
                val agent = splitTarget[i+(height)*2+2]
                val agentX = numAtoB(agent[0].toString(), 36, 10)
                val agentY = numAtoB(agent[1].toString(), 36, 10)
                agentPos[agentNames[i]] = arrayOf(agentX, agentY)
            }
        }

        private fun numAtoB(numStr: String, A: Int, B:Int): Int{
            return Integer.parseInt(numStr, A).toString(B).toInt()
        }
    }
}
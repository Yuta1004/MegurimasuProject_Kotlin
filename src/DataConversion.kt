class DataConversion {
    companion object {
        fun conversion(scoreData: Array<Array<Int>>, encampmentData: Array<Array<Int>>, agents: Map<String, MegurimasuSimulator.Agent>): String {
            var conversionStr = ""
            val width = scoreData[0].size
            val height = scoreData.size

            // ステージデータ(幅，高さ)
            val stageInfoStr = "$width$height"
            conversionStr += "${Integer.toString(stageInfoStr.toInt(), 36)}:"

            // スコアデータ
            scoreData.forEach { array ->
                conversionStr += array
                        .map{ (it+16).toString(36) }
                        .reduce { s1, s2 -> s1 + s2 }
            }
            conversionStr += ":"

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

        fun deconversion() {

        }
    }
}
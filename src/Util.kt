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
package fortest

import java.net.ServerSocket

fun main(args: Array<String>) {
    val server = ServerSocket(6666)
    val socket = server.accept()

    while(true) {
        print("Please Input Send Text > ")
        val text = readLine()

        try {
            val writer = socket!!.getOutputStream()
            writer.write((text + "\n").toByteArray())
        } catch (e: Exception) {
            socket.close()
            server.close()
        }
    }
}
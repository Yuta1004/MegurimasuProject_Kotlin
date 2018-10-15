import com.sun.jdi.connect.spi.ClosedConnectionException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import kotlin.concurrent.thread

class TCPConnectionManager(private val hostAddress: String, private val hostPort: Int, private val receiver: (String) -> Unit){

    var socket: Socket? = null
    init { initSocket() }

    private fun initSocket(){
        try {
            socket = Socket(hostAddress, hostPort)
            println("Socket Open")
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // データ受信開始
    fun receiveStart(){
        thread { receiveData() }
    }

    private fun closeSocket(){
        if(socket == null){ return }

        socket!!.close()
        println("Socket Closed")
    }

    private fun receiveData(){
        if(socket == null){ return }

        // データ受信処理
        //  - 受信したデータはレシーバ関数へ
        //  - 切断時にはCloseConnectionExceptionを投げる
        try {
             val reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
             while (true) {
                 val text = reader.readLine() ?: throw ClosedConnectionException()
                 receiver(text)
             }
         } catch (e: ClosedConnectionException){
             closeSocket()
         } catch (e: Exception) {
             e.printStackTrace()
             closeSocket()
         }
    }
}
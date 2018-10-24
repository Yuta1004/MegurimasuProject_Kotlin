package manager

import com.sun.jdi.connect.spi.ClosedConnectionException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.util.logging.Handler
import kotlin.concurrent.thread

class TCPConnectionManager(private val hostAddress: String, private val hostPort: Int, private val receiver: (String) -> Unit){

    var socket: Socket? = null
    var connecting = false
    init { initSocket() }

    private fun initSocket(): Boolean{
        return try {
            socket = Socket(hostAddress, hostPort)
            receiver("open")
            connecting = true
            true
        }
        catch (e: Exception) { false }
    }

    // データ受信開始
    fun receiveStart(){
        thread {
            receiveData()
            closeSocket()
            connect()
        }
    }

    // データ送信
    fun sendData(text: String){
        try{
            val writer = socket!!.getOutputStream()
            writer.write(("$text\n").toByteArray())
        }catch(e: Exception){
            println("データ送信失敗")
        }
    }


    private fun connect(){
        while(!initSocket()){
            Thread.sleep(2000)
            receiver("reconnect")
        }

        connecting = true
        receiveStart()
    }

    private fun closeSocket(){
        if(socket == null){ return }

        socket!!.close()
        receiver("close")
        connecting = false
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
         } catch (e: Exception){
            return
         }
    }
}
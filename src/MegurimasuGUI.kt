import java.awt.*
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*

class MegurimasuGUI(var megurimasu: MegurimasuSimulator) : JFrame() {
    private var bufImage: Image? = null
    private var logTextArea: TextArea? = null

    init{
        background = Color.WHITE
        setBounds(100, 100, 1200, 800)
        title = "巡りマス"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true

        setUI()

        updateBoard(megurimasu)
    }

    // 盤面更新
    fun updateBoard(megurimasu: MegurimasuSimulator){
        this.megurimasu = megurimasu
        bufImage = createImage(1200, 650)
        drawBoard()
        revalidate()
        repaint()
    }

    // 最善手を表示する
    fun viewBestBehavior(bestBehavior: Map<String, Int>){
        val viewBestBehavior = ViewBestBehavior(bestBehavior)
        while(viewBestBehavior.isShowing){ Thread.sleep(10) }
    }

    // ログを記録する
    fun writeLog(text: String){
        if(logTextArea == null){ return }

        val nowTime = SimpleDateFormat("[ yyyy/MM/dd HH:mm:ss ] ").format(Date())
        logTextArea!!.append("\n" + nowTime + text)
    }

    // 画面更新時に呼ばれる
    override fun paint(g: Graphics){
        g.drawImage(bufImage, 0, 0, this)
    }

    // 画面更新時に呼ばれる
    override fun update(g: Graphics?) {
        if(g == null){ return }
        paint(g)
    }

    // UIセット
    private fun setUI(){
        val panel = Panel()
        logTextArea = TextArea(8, 140)
        panel.add(logTextArea)

        val contentPane = contentPane
        contentPane.add(panel, BorderLayout.SOUTH)
    }

    // 盤面描画
    private fun drawBoard() {
        if (bufImage == null) {
            return
        }

        val graphics = bufImage!!.graphics as Graphics2D

        // 盤面データ描画
        val scoreData = megurimasu.scoreData
        for (y: Int in 0 until scoreData.size) {
            for (x: Int in 0 until scoreData[0].size) {
                drawEncampment(graphics, x, y)
                drawScoreText(graphics, x, y)
            }
        }

        // スコア情報描画
        val score = megurimasu.calScore()
        val scoreStr = "自チーム ${String.format("%5d", score["A"])} vs ${String.format("%-5d", score["B"])} 相手チーム"
        val (drawX, drawY) = getDrawCenterPos(graphics, scoreStr, 830, 100)
        graphics.font = Font("Selif", 10, 30)
        graphics.drawString(scoreStr, drawX, drawY)
    }

    // 陣地描画
    private fun drawEncampment(g: Graphics2D, x: Int, y: Int){
        g.color = Color.BLACK
        g.stroke = BasicStroke(5f)
        g.drawRect(50 * x + 10, 50 * y + 30, 50, 50)
        g.color = getEncampmentColor(x, y)
        g.fillRect(50 * x + 10, 50 * y + 30, 50, 50)
    }

    // スコア描画
    private fun drawScoreText(g: Graphics2D, argX: Int, argY: Int){
        val (drawX, drawY) = getDrawCenterPos(g, megurimasu.scoreData[argY][argX].toString(), 50*argX+35, 50*argY+65)
        g.color = Color.BLACK
        g.font = Font("Selif", 10, 20)
        g.drawString(megurimasu.scoreData[argY][argX].toString(), drawX, drawY)
    }

    // センタリングするための座標を計算する
    private fun getDrawCenterPos(g: Graphics2D, text: String, x: Int, y: Int): Pair<Int, Int>{
        val fontMatrics = g.fontMetrics
        val rectText = fontMatrics.getStringBounds(text, g).bounds
        val drawX = x - rectText.width / 2
        val drawY = y - rectText.height + fontMatrics.maxAscent

        return Pair(drawX, drawY)
    }

    private fun getEncampmentColor(x: Int, y: Int): Color{
        val agentPos = megurimasu.agents.map { it.value.x * 100 + it.value.y }
        val encampmentData = megurimasu.encampmentData[y][x]

        // エージェントがいる座標だったら
        if(agentPos.contains(x * 100 + y)){
            return when(encampmentData){
                1 -> Color(255, 100, 100)
                2 -> Color(100, 100, 255)
                else -> Color.WHITE
            }
        }

        return when(encampmentData){
            1 -> Color(255, 200, 200)
            2 -> Color(200, 200, 255)
            else -> Color.WHITE
        }
    }

}
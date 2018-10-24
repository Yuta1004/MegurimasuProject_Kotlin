import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame

class ViewBestBehavior(private val bestBehavior: Map<String, Int>): JFrame(), KeyListener {
    var bufImage: Image? = null

    init{
        background = Color.WHITE
        setBounds(400, 100, 600, 600)
        title = "探索結果"
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true
        addKeyListener(this)

        viewBestBehavior(bestBehavior)
    }

    // 最善手表示
    private fun viewBestBehavior(bestBehavior: Map<String, Int>){
        bufImage = createImage(600, 600)

        val graphics = bufImage!!.graphics as Graphics2D

        // A_1
        val a1Text = "エージェント１"
        graphics.font = Font("Selif", 10, 25)
        val (a1TextX, a1TextY) = getDrawCenterPos(graphics, a1Text, 150, 100)
        graphics.drawString(a1Text, a1TextX, a1TextY)

        val a1Behavior = bestBehavior["A_1"].toString()
        graphics.font = Font("Selif", 10, 80)
        val (a1BehaviorX, a1BehaviorY) = getDrawCenterPos(graphics, a1Behavior, 150, 300)
        graphics.drawString(a1Behavior, a1BehaviorX, a1BehaviorY)

        // A_2
        val a2Text = "エージェント２"
        graphics.font = Font("Selif", 10, 25)
        val (a2TextX, a2TextY) = getDrawCenterPos(graphics, a1Text, 450, 100)
        graphics.drawString(a2Text, a2TextX, a2TextY)

        val a2Behavior = bestBehavior["A_2"].toString()
        graphics.font = Font("Selif", 10, 80)
        val (a2BehaviorX, a2BehaviorY) = getDrawCenterPos(graphics, a2Behavior, 450, 300)
        graphics.drawString(a2Behavior, a2BehaviorX, a2BehaviorY)

        // ENTERを押すとウィンドウ閉じれるよ！
        val pleaseEntermes = "ENTERを押してウィンドウを閉じてください"
        graphics.font = Font("Selif", 10, 20)
        val (pleaseEnterX, pleaseEnterY) = getDrawCenterPos(graphics, pleaseEntermes, 300, 450)
        graphics.drawString(pleaseEntermes, pleaseEnterX, pleaseEnterY)

        revalidate()
        repaint()
    }

    // キー入力
    override fun keyReleased(e: KeyEvent?) {}
    override fun keyTyped(e: KeyEvent?) {}
    override fun keyPressed(e: KeyEvent?) {
        if(e?.keyCode == KeyEvent.VK_ENTER){
            isVisible = false
            dispose()
        }
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

    // センタリングするための座標を計算する
    private fun getDrawCenterPos(g: Graphics2D, text: String, x: Int, y: Int): Pair<Int, Int>{
        val fontMatrics = g.fontMetrics
        val rectText = fontMatrics.getStringBounds(text, g).bounds
        val drawX = x - rectText.width / 2
        val drawY = y - rectText.height + fontMatrics.maxAscent

        return Pair(drawX, drawY)
    }
}
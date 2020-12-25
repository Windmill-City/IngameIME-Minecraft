package city.windmill.ingameime.client.gui.widget

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

class CompositionWidget(textRenderer: TextRenderer) : Widget(textRenderer) {
    var args: Pair<String, Int>? = null
    
    private val caretWidth = 3
    
    override val active get() = args != null
    override val width
        get() = super.width + textRenderer.getWidth(args?.first) + caretWidth
    override val height
        get() = super.height + textRenderer.fontHeight
    override val padding: Pair<Int, Int>
        get() = 1 to 1
    
    @Suppress("NAME_SHADOWING")
    override fun draw(matrices: MatrixStack?, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        val args = args
        args?.let {
            val text = args.first
            val caret = args.second
            
            super.draw(matrices, offsetX, offsetY, mouseX, mouseY, delta)
            
            val part1 = text.substring(0, caret)
            val part2 = text.substring(caret)
            
            var offsetX = offsetX + padding.first
            val offsetY = offsetY + padding.second
            offsetX = textRenderer.draw(matrices, part1, offsetX.toFloat(), offsetY.toFloat(), textColor)
            //Caret-blink 0.5s
            if ((System.currentTimeMillis() % 1000) > 500) {
                DrawableHelper.fill(
                    matrices,
                    offsetX + 1, //1 pixel width
                    offsetY,
                    offsetX + 2, //with 2 pixel margin
                    offsetY + textRenderer.fontHeight,
                    textColor
                )
            }
            offsetX += caretWidth
            textRenderer.draw(matrices, part2, offsetX.toFloat(), offsetY.toFloat(), textColor)
        }
    }
}
package city.windmill.ingameime.client.gui.widget

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack

class CandidateListWidget(textRenderer: TextRenderer) : Widget(textRenderer) {
    var candidates: Array<String>? = null
    
    private val drawItem = CandidateEntry(textRenderer)
    
    override val active get() = !candidates.isNullOrEmpty()
    override val width
        get() = super.width + candidates!!.sumBy { s -> drawItem.apply { this.text = s }.width }
    override val height
        get() = super.height + textRenderer.fontHeight
    override val padding: Pair<Int, Int>
        get() = 1 to 3
    
    @Suppress("NAME_SHADOWING")
    override fun draw(matrices: MatrixStack?, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        val candidates = candidates
        candidates?.let {
            super.draw(matrices, offsetX, offsetY, mouseX, mouseY, delta)
            
            var offsetX = offsetX + padding.first
            val offsetY = offsetY + padding.second
            var index = 1
            for (str in it) {
                drawItem.index = index
                drawItem.text = str
                drawItem.draw(matrices, offsetX, offsetY, mouseX, mouseY, delta)
                offsetX += drawItem.width
                index++
            }
        }
    }
    
    class CandidateEntry(textRenderer: TextRenderer) : Widget(textRenderer) {
        var text: String? = null
        var index = 0
        
        private val indexWidth = textRenderer.getWidth("00") + 5
        
        override val width
            get() = super.width + textRenderer.getWidth(text) + indexWidth
        override val height
            get() = super.height + textRenderer.fontHeight
        override val padding: Pair<Int, Int>
            get() = 2 to 3
        
        @Suppress("NAME_SHADOWING")
        override fun draw(matrices: MatrixStack?, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
            var offsetX = offsetX + padding.first
            drawCenteredString(matrices, textRenderer, index.toString(), offsetX + indexWidth / 2, offsetY, textColor)
            offsetX += indexWidth
            textRenderer.draw(matrices, text, offsetX.toFloat(), offsetY.toFloat(), textColor)
        }
        
        private fun drawCenteredString(
            matrices: MatrixStack?,
            textRenderer: TextRenderer,
            text: String?,
            centerX: Int,
            y: Int,
            color: Int
        ) {
            textRenderer.draw(
                matrices, text,
                (centerX - textRenderer.getWidth(text) / 2).toFloat(), y.toFloat(), color
            )
        }
        
    }
}
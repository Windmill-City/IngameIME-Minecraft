package city.windmill.ingameime.client.gui.widget

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font


class CandidateListWidget(font: Font) : Widget(font) {
    var candidates: Array<String>? = null
    
    private val drawItem = CandidateEntry(font)
    
    override val active get() = !candidates.isNullOrEmpty()
    override val width
        get() = super.width + candidates!!.sumBy { s -> drawItem.apply { this.text = s }.width }
    override val height
        get() = super.height + font.lineHeight
    override val padding: Pair<Int, Int>
        get() = 1 to 3
    
    @Suppress("NAME_SHADOWING")
    override fun draw(offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        candidates?.let {
            super.draw(offsetX, offsetY, mouseX, mouseY, delta)
            
            var offsetX = offsetX + padding.first
            val offsetY = offsetY + padding.second
            var index = 1
            for (str in it) {
                drawItem.index = index
                drawItem.text = str
                drawItem.draw(offsetX, offsetY, mouseX, mouseY, delta)
                offsetX += drawItem.width
                index++
            }
        }
    }
    
    class CandidateEntry(font: Font) : Widget(font) {
        var text: String? = null
        var index = 0
        
        private val indexWidth = font.width("00") + 5
        
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        override val width
            get() = super.width + font.width(text) + indexWidth
        override val height
            get() = super.height + font.lineHeight
        override val padding: Pair<Int, Int>
            get() = 2 to 3
        
        @Suppress("NAME_SHADOWING", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        override fun draw(offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
            var offsetX = offsetX + padding.first
            drawCenteredString(font, index.toString(), offsetX + indexWidth / 2, offsetY, textColor)
            offsetX += indexWidth
            font.draw(text, offsetX.toFloat(), offsetY.toFloat(), textColor)
        }
        
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        private fun drawCenteredString(
            font: Font,
            text: String?,
            centerX: Int,
            y: Int,
            color: Int
        ) {
            font.draw(
                text,
                (centerX - font.width(text) / 2).toFloat(), y.toFloat(), color
            )
        }
        
    }
}
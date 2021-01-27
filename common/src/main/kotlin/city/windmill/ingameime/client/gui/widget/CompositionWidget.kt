package city.windmill.ingameime.client.gui.widget

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font

class CompositionWidget(font: Font) : Widget(font) {
    var args: Pair<String, Int>? = null
    
    private val caretWidth = 3
    
    override val active get() = args != null
    
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override val width
        get() = super.width + font.width(args?.first) + caretWidth
    override val height
        get() = super.height + font.lineHeight
    override val padding: Pair<Int, Int>
        get() = 1 to 1
    
    @Suppress("NAME_SHADOWING")
    override fun draw(poseStack: PoseStack, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        args?.let {
            val text = it.first
            val caret = it.second
            
            super.draw(poseStack, offsetX, offsetY, mouseX, mouseY, delta)
            
            val part1 = text.substring(0, caret)
            val part2 = text.substring(caret)
            
            var offsetX = offsetX + padding.first
            val offsetY = offsetY + padding.second
            offsetX = font.draw(poseStack, part1, offsetX.toFloat(), offsetY.toFloat(), textColor)
            //Caret-blink 0.5s
            if ((System.currentTimeMillis() % 1000) > 500) {
                fill(
                    poseStack,
                    offsetX + 1, //1 pixel width
                    offsetY,
                    offsetX + 2, //with 2 pixel margin
                    offsetY + font.lineHeight,
                    textColor
                )
            }
            offsetX += caretWidth
            font.draw(poseStack, part2, offsetX.toFloat(), offsetY.toFloat(), textColor)
        }
    }
}
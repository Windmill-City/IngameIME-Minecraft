package city.windmill.ingameime.client.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.renderer.MultiBufferSource


abstract class Widget(val font: Font) {
    var offsetX = 0
    var offsetY = 0
    var textColor = 0xFF_00_00_00.toInt()
    var backgroundColor = 0xEB_EB_EB_EB.toInt()
    open val active get() = false
    open val width get() = padding.first * 2
    open val height get() = padding.second * 2
    open val padding = 0 to 0
    
    fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (active)
            draw(poseStack, offsetX, offsetY, mouseX, mouseY, delta)
    }
    
    open fun draw(poseStack: PoseStack, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        //Background
        fill(
            poseStack,
            offsetX,
            offsetY,
            offsetX + width,
            offsetY + height,
            backgroundColor
        )
    }
    
    fun moveTo(x: Int, y: Int) {
        offsetX = x
        offsetY = y
    }
    
    fun Font.draw(poseStack: PoseStack, string: String?, x: Float, y: Float, color: Int): Int {
        RenderSystem.enableAlphaTest()
        return if (string == null)
            0
        else {
            val bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().builder)
            val textWidth =
                drawInBatch(string, x, y, color, false, poseStack.last().pose(), bufferSource, false, 0, 15728880)
            bufferSource.endBatch()
            textWidth
        }
    }
    
    fun fill(poseStack: PoseStack, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        RenderSystem.enableDepthTest()
        GuiComponent.fill(poseStack.last().pose(), x1, y1, x2, y2, color)
        RenderSystem.disableDepthTest()
    }
}
package city.windmill.ingameime.client.gui.widget

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent


abstract class Widget(val font: Font) : net.minecraft.client.gui.components.Widget {
    var offsetX = 0
    var offsetY = 0
    var textColor = 0xFF_00_00_00.toInt()
    var backgroundColor = 0xEB_EB_EB_EB.toInt()
    open val active get() = false
    open val width get() = padding.first * 2
    open val height get() = padding.second * 2
    open val padding = 0 to 0
    
    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (active)
            draw(poseStack, offsetX, offsetY, mouseX, mouseY, delta)
    }
    
    open fun draw(poseStack: PoseStack, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        //Background
        GuiComponent.fill(
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
}
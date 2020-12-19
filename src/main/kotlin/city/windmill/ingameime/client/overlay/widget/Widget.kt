package city.windmill.ingameime.client.overlay.widget

import me.shedaniel.math.Color
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

abstract class Widget(val textRenderer: TextRenderer) : Drawable {
    var offsetX = 0
    var offsetY = 0
    var textColor = Color.ofRGB(0x00, 0x00, 0x00).color
    var backgroundColor = Color.ofRGB(0xFF, 0xFF, 0xFF).color
    open val active get() = false
    open val width get() = padding.first * 2
    open val height get() = padding.second * 2
    open val padding = 0 to 0
    
    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (active)
            draw(matrices, offsetX, offsetY, mouseX, mouseY, delta)
    }
    
    open fun draw(matrices: MatrixStack?, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        //Background
        DrawableHelper.fill(
            matrices,
            offsetX,
            offsetY,
            offsetX + width,
            offsetY + height,
            backgroundColor
        )
    }
    
    fun moveTo(x: Int, y:Int) {
        offsetX = x
        offsetY = y
    }
}
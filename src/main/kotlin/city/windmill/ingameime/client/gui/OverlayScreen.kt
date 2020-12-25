package city.windmill.ingameime.client.gui

import city.windmill.ingameime.client.gui.widget.AlphaModeWidget
import city.windmill.ingameime.client.gui.widget.CandidateListWidget
import city.windmill.ingameime.client.gui.widget.CompositionWidget
import city.windmill.ingameime.client.gui.widget.Widget
import city.windmill.ingameime.client.jni.ExternalBaseIME
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Drawable
import net.minecraft.client.util.math.MatrixStack

object OverlayScreen : Drawable {
    private val alphaModeWidget = AlphaModeWidget(MinecraftClient.getInstance().textRenderer)
    private val compositionWidget = CompositionWidget(MinecraftClient.getInstance().textRenderer)
    private val candidateListWidget = CandidateListWidget(MinecraftClient.getInstance().textRenderer)
    
    var caretPos: Pair<Int, Int> = 0 to 0
    
    var showAlphaMode
        get() = alphaModeWidget.active
        set(value) {
            alphaModeWidget.active = value
            alphaModeWidget.adjustPos()
        }
    
    var candidates
        get() = candidateListWidget.candidates
        set(value) {
            candidateListWidget.candidates = value
            candidateListWidget.adjustPos()
        }
    
    var composition
        get() = compositionWidget.args
        set(value) {
            compositionWidget.args = value
            compositionWidget.adjustPos()
            candidateListWidget.adjustPos()
        }
    
    val compositionExt
        get() = with(compositionWidget) {
            val scale = MinecraftClient.getInstance().window.scaleFactor
            intArrayOf(offsetX, offsetY, offsetX + width, offsetY + height).apply {
                forEachIndexed { index, i -> this[index] = i.times(scale).toInt() }
            }
        }
    
    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (ExternalBaseIME.State) {
            compositionWidget.render(matrices, mouseX, mouseY, delta)
            alphaModeWidget.render(matrices, mouseX, mouseY, delta)
            candidateListWidget.render(matrices, mouseX, mouseY, delta)
        }
    }
    
    private fun Widget.adjustPos() {
        if (!active) return
        with(MinecraftClient.getInstance().window) {
            moveTo(
                caretPos.first.coerceAtMost(scaledWidth - this@adjustPos.width),
                caretPos.second.coerceAtMost(scaledWidth - this@adjustPos.height)
            )
        }
    }
    
    private fun CandidateListWidget.adjustPos() {
        if (!active) return
        with(MinecraftClient.getInstance().window) {
            moveTo(caretPos.first.coerceAtMost((scaledWidth - this@adjustPos.width).coerceAtLeast(0)),
                (caretPos.second + compositionWidget.height).let {
                    if (it > scaledWidth - this@adjustPos.height)
                        caretPos.second - height //place the candidate above the composition
                    else it
                })
        }
    }
}
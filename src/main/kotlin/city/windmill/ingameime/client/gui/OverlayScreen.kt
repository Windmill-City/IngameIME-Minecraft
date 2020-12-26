package city.windmill.ingameime.client.gui

import city.windmill.ingameime.client.gui.widget.AlphaModeWidget
import city.windmill.ingameime.client.gui.widget.CandidateListWidget
import city.windmill.ingameime.client.gui.widget.CompositionWidget
import city.windmill.ingameime.client.gui.widget.Widget
import city.windmill.ingameime.client.jni.ExternalBaseIME
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Drawable
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Matrix4f

object OverlayScreen : Drawable {
    private val alphaModeWidget = AlphaModeWidget(MinecraftClient.getInstance().textRenderer)
    private val compositionWidget = CompositionWidget(MinecraftClient.getInstance().textRenderer)
    private val candidateListWidget = CandidateListWidget(MinecraftClient.getInstance().textRenderer)
    
    var caretPos: Pair<Int, Int> = 0 to 0
        set(value) {
            field = value
            compositionWidget.adjustPos()
        }
    
    var showAlphaMode
        get() = alphaModeWidget.active
        set(value) {
            alphaModeWidget.active = value
            alphaModeWidget.adjustPosByComposition()
        }
    
    var candidates
        get() = candidateListWidget.candidates
        set(value) {
            candidateListWidget.candidates = value
            candidateListWidget.adjustPosByComposition()
        }
    
    var composition
        get() = compositionWidget.args
        set(value) {
            compositionWidget.args = value
            compositionWidget.adjustPos()
            candidateListWidget.adjustPosByComposition()
        }
    
    val compositionExt
        get() = with(compositionWidget) {
            val scale = MinecraftClient.getInstance().window.scaleFactor
            intArrayOf(offsetX, offsetY, offsetX + width, offsetY + height).apply {
                forEachIndexed { index, i -> this[index] = i.times(scale).toInt() }
            }
        }
    
    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (ExternalBaseIME.State) {
            matrices.push()
            matrices.translate(0.0, 0.0, 500.0)
            compositionWidget.render(matrices, mouseX, mouseY, delta)
            alphaModeWidget.render(matrices, mouseX, mouseY, delta)
            candidateListWidget.render(matrices, mouseX, mouseY, delta)
            matrices.pop()
        }
    }
    
    private fun CompositionWidget.adjustPos() {
        with(MinecraftClient.getInstance().window) {
            moveTo(
                caretPos.first.coerceAtMost(scaledWidth - this@adjustPos.width),
                (caretPos.second - padding.second).coerceAtMost(scaledHeight - this@adjustPos.height + padding.second)
            )
        }
    }
    
    private fun Widget.adjustPosByComposition() {
        if (!active) return
        with(MinecraftClient.getInstance().window) {
            with(compositionWidget) {
                this@adjustPosByComposition.moveTo(
                    offsetX.coerceAtMost((scaledWidth - this@adjustPosByComposition.width).coerceAtLeast(0)),
                    (offsetY + height).let {
                        if (it > scaledHeight - this@adjustPosByComposition.height)
                            offsetY - this@adjustPosByComposition.height //place it above the composition
                        else it
                    })
            }
        }
    }
}
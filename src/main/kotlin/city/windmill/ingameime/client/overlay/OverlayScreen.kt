package city.windmill.ingameime.client.overlay

import city.windmill.ingameime.client.jni.ExternalBaseIME
import city.windmill.ingameime.client.overlay.widget.AlphaModeWidget
import city.windmill.ingameime.client.overlay.widget.CandidateListWidget
import city.windmill.ingameime.client.overlay.widget.CompositionWidget
import city.windmill.ingameime.client.overlay.widget.Widget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Drawable
import net.minecraft.client.util.math.MatrixStack
import kotlin.time.ExperimentalTime

@ExperimentalTime
object OverlayScreen : Drawable {
    private val alphaModeWidget = AlphaModeWidget(MinecraftClient.getInstance().textRenderer)
    private val compositionWidget = CompositionWidget(MinecraftClient.getInstance().textRenderer)
    private val candidateListWidget = CandidateListWidget(MinecraftClient.getInstance().textRenderer)
    
    var caretPos: Pair<Int, Int> = 0 to 0
    
    var showAlphaMode
        get() = alphaModeWidget.active
        set(value) {
            alphaModeWidget.active = value
        }
    var candidates
        get() = candidateListWidget.candidates
        set(value) {
            candidateListWidget.candidates = value
        }
    
    var composition
        get() = compositionWidget.args
        set(value) {
            compositionWidget.args = value
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
            compositionWidget.adjustPos()
            alphaModeWidget.adjustPos()
            candidateListWidget.adjustPos()
            compositionWidget.render(matrices, mouseX, mouseY, delta)
            alphaModeWidget.render(matrices, mouseX, mouseY, delta)
            candidateListWidget.render(matrices, mouseX, mouseY, delta)
        }
    }
    
    private fun Widget.adjustPos() {
        if (!active) return
        moveTo(caretPos.first, caretPos.second)
        val window = MinecraftClient.getInstance().window
        val maxX = window.scaledWidth - width
        val maxY = window.scaledHeight - height
        if (offsetX > maxX) offsetX = maxX
        if (offsetY > maxY) offsetY = maxY
    }
    
    private fun CandidateListWidget.adjustPos() {
        if (!active) return
        moveTo(caretPos.first, caretPos.second + compositionWidget.height)
        val window = MinecraftClient.getInstance().window
        val maxX = (window.scaledWidth - width).coerceAtLeast(0)
        val maxY = (window.scaledHeight - height).coerceAtLeast(0)
        if (offsetX > maxX) offsetX = maxX
        if (offsetY > maxY) offsetY = (compositionWidget.offsetY - height).coerceAtMost(maxY)
    }
}
package city.windmill.ingameime.client.gui

import city.windmill.ingameime.client.gui.widget.AlphaModeWidget
import city.windmill.ingameime.client.gui.widget.CandidateListWidget
import city.windmill.ingameime.client.gui.widget.CompositionWidget
import city.windmill.ingameime.client.gui.widget.Widget
import city.windmill.ingameime.client.jni.ExternalBaseIME
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import kotlin.ranges.*

object OverlayScreen : net.minecraft.client.gui.components.Widget {
    private val alphaModeWidget = AlphaModeWidget(Minecraft.getInstance().font)
    private val compositionWidget = CompositionWidget(Minecraft.getInstance().font)
    private val candidateListWidget = CandidateListWidget(Minecraft.getInstance().font)
    
    var caretPos: Pair<Int, Int> = 0 to 0
        set(value) {
            if (field == value) return
            field = value
            compositionWidget.adjustPos()
            alphaModeWidget.adjustPosByComposition()
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
            val scale = Minecraft.getInstance().window.guiScale
            intArrayOf(offsetX, offsetY, offsetX + width, offsetY + height).apply {
                forEachIndexed { index, i -> this[index] = i.times(scale).toInt() }
            }
        }
    
    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        if (ExternalBaseIME.State) {
            RenderSystem.enableDepthTest()
            RenderSystem.enableRescaleNormal()
            val poseStack = PoseStack()
            poseStack.translate(0.0, 0.0, 400.0)
            compositionWidget.render(poseStack, mouseX, mouseY, delta)
            alphaModeWidget.render(poseStack, mouseX, mouseY, delta)
            candidateListWidget.render(poseStack, mouseX, mouseY, delta)
            RenderSystem.disableDepthTest()
            RenderSystem.disableRescaleNormal()
        }
    }
    
    private fun CompositionWidget.adjustPos() {
        with(Minecraft.getInstance().window) {
            moveTo(
                caretPos.first.coerceAtMost(guiScaledWidth - this@adjustPos.width),
                (caretPos.second - padding.second).coerceAtMost(guiScaledHeight - this@adjustPos.height + padding.second)
            )
        }
    }
    
    private fun Widget.adjustPosByComposition() {
        if (!active) return
        with(Minecraft.getInstance().window) {
            with(compositionWidget) {
                this@adjustPosByComposition.moveTo(
                    offsetX.coerceAtMost((guiScaledWidth - this@adjustPosByComposition.width).coerceAtLeast(0)),
                    (offsetY + height).let {
                        if (it > guiScaledHeight - this@adjustPosByComposition.height)
                            offsetY - this@adjustPosByComposition.height //place it above the composition
                        else it
                    })
            }
        }
    }
}
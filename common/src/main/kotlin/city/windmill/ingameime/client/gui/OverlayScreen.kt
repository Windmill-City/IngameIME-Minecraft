package city.windmill.ingameime.client.gui

import city.windmill.ingameime.client.gui.widget.AlphaModeWidget
import city.windmill.ingameime.client.gui.widget.CandidateListWidget
import city.windmill.ingameime.client.gui.widget.CompositionWidget
import city.windmill.ingameime.client.gui.widget.Widget
import city.windmill.ingameime.client.jni.ExternalBaseIME
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft

object OverlayScreen : net.minecraft.client.gui.components.Widget {
    private val alphaModeWidget = AlphaModeWidget(Minecraft.getInstance().font)
    private val compositionWidget = CompositionWidget(Minecraft.getInstance().font)
    private val candidateListWidget = CandidateListWidget(Minecraft.getInstance().font)

    /**
     * The caret pos of the game window, for positioning the composition & candidate window
     */
    var caretPos: Pair<Int, Int> = 0 to 0
        set(value) {
            if (field == value) return
            field = value
            compositionWidget.adjustPos()
            alphaModeWidget.adjustPosByComposition()
        }

    /**
     * Show the alpha mode/normal mode indicator
     * will auto close after a few seconds
     */
    var showAlphaMode
        get() = alphaModeWidget.active
        set(value) {
            alphaModeWidget.active = value
            alphaModeWidget.adjustPosByComposition()
        }

    /**
     * Update candidates here, for fullscreen mode
     */
    var candidates
        get() = candidateListWidget.candidates
        set(value) {
            candidateListWidget.candidates = value
            candidateListWidget.adjustPosByComposition()
        }

    /**
     * Update composition data here
     * the String is the composition text
     * the caret is the position where the composition text is editing
     */
    var composition
        get() = compositionWidget.compositionData
        set(value) {
            compositionWidget.compositionData = value
            compositionWidget.adjustPos()
            candidateListWidget.adjustPosByComposition()
        }

    /**
     * Get composition ext here, for positioning input method's candidate window
     */
    val compositionExt
        get() = with(compositionWidget) {
            val scale = Minecraft.getInstance().window.guiScale
            intArrayOf(offsetX, offsetY, offsetX + width, offsetY + height).apply {
                forEachIndexed { index, i -> this[index] = i.times(scale).toInt() }
            }
        }

    /**
     * Check if we are composing
     */
    val composing
        get() = composition != null

    /**
     * Render the widget when input method is active
     */
    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (ExternalBaseIME.State) {
            poseStack.pushPose()
            poseStack.translate(0.0, 0.0, 500.0)
            compositionWidget.render(poseStack, mouseX, mouseY, delta)
            alphaModeWidget.render(poseStack, mouseX, mouseY, delta)
            candidateListWidget.render(poseStack, mouseX, mouseY, delta)
            poseStack.popPose()
        }
    }

    /**
     * Place the composition window beside the game window caret
     */
    private fun CompositionWidget.adjustPos() {
        with(Minecraft.getInstance().window) {
            moveTo(
                caretPos.first.coerceAtMost(guiScaledWidth - this@adjustPos.width),
                (caretPos.second - padding.second).coerceAtMost(guiScaledHeight - this@adjustPos.height + padding.second)
            )
        }
    }

    /**
     * Place the widget beside the composition window
     */
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
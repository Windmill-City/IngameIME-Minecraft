package city.windmill.ingameime.client.gui.widget

import city.windmill.ingameime.client.jni.ExternalBaseIME
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.gui.Font
import net.minecraft.client.resources.language.I18n
import java.lang.ref.WeakReference

class AlphaModeWidget(font: Font) : Widget(font) {
    private val text get() = I18n.get(if (ExternalBaseIME.AlphaMode) "alpha.ingameime.mode" else "native.ingameime.mode")
    private var hideDelay: WeakReference<Job>? = null
    
    override var active = false
        set(value) {
            hideDelay?.get()?.cancel()
            if (value) {
                hideDelay = WeakReference(GlobalScope.launch {
                    delay(3 * 1000)
                    field = false
                })
            }
            field = value
        }
    override val width
        get() = with(super.width + font.width(text)) {
            if (this < height) height else this
        }
    override val height
        get() = super.height + font.lineHeight
    override val padding: Pair<Int, Int>
        get() = 2 to 3
    
    @Suppress("NAME_SHADOWING")
    override fun draw(offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int, delta: Float) {
        super.draw(offsetX, offsetY, mouseX, mouseY, delta)
        val offsetX = offsetX + width / 2 - font.width(text) / 2
        val offsetY = offsetY + padding.second
        font.draw(text, offsetX.toFloat(), offsetY.toFloat(), textColor)
    }
}
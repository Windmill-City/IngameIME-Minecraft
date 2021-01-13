package city.windmill.ingameime.client.jni

import city.windmill.ingameime.client.IMEHandler
import city.windmill.ingameime.client.gui.OverlayScreen
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window

interface ICommitListener {
    fun onCommit()
}

object ExternalBaseIME {
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|ExternalBaseIME")!!
    var State: Boolean = false
        set(value) {
            LOGGER.trace("State $field -> $value")
            field = value
            nSetState(field)
            OverlayScreen.showAlphaMode = field
        }
    
    var FullScreen: Boolean = false
        set(value) {
            LOGGER.trace("FullScreen $field -> $value")
            field = value
            nSetFullScreen(field)
            if (State) {
                State = false
                State = true
            }
        }
    
    var HandleComposition: Boolean = false
        set(value) {
            LOGGER.trace("HandleComposition $field -> $value")
            field = value
            nSetHandleComposition(field)
        }
    
    var AlphaMode: Boolean = false
        private set(value) {
            LOGGER.trace("AlphaMode $field -> $value")
            field = value
        }
    
    init {
        val x86 = if (Minecraft.getInstance().is64Bit) "" else "-x86"
        val resourceNative = ResourceLocation("ingameime", "natives/jni$x86.dll")
        NativeLoader.load(Minecraft.getInstance().resourceManager.getResource(resourceNative))
        LOGGER.trace("Initialing window")
        nInitialize(glfwGetWin32Window(Minecraft.getInstance().window.window))
        HandleComposition = true
    }
    
    //region Natives
    private external fun nInitialize(handle: Long)
    
    @Suppress("unused")
    private external fun nUninitialize()
    private external fun nSetState(state: Boolean)
    private external fun nSetFullScreen(fullscreen: Boolean)
    private external fun nSetHandleComposition(handle: Boolean)
    //endregion
    
    //region CallFrom JNI
    @Suppress("unused")
    private fun onCandidateList(candidates: Array<String>?) {
        OverlayScreen.candidates = candidates
    }
    
    @Suppress("unused")
    private fun onComposition(str: String?, caret: Int, state: CompositionState) {
        when (state) {
            CompositionState.Commit -> {
                IMEHandler.IMEState.onCommit()
                OverlayScreen.composition = null
                str!!.onEach { ch ->
                    Minecraft.getInstance().keyboardHandler
                        .charTyped(Minecraft.getInstance().window.window, ch.toInt(), 0)
                }
            }
            CompositionState.Start,
            CompositionState.End,
            CompositionState.Update -> {
                OverlayScreen.composition = if (str.isNullOrEmpty()) null else str to caret
            }
        }
        OverlayScreen.showAlphaMode = false
    }
    
    @Suppress("unused")
    private fun onGetCompExt(): IntArray {
        return OverlayScreen.compositionExt
    }
    
    @Suppress("unused")
    private fun onAlphaMode(isAlphaMode: Boolean) {
        AlphaMode = isAlphaMode
        OverlayScreen.showAlphaMode = true
    }
    //endregion
    
    private enum class CompositionState {
        Start,
        Update,
        End,
        Commit,
    }
}
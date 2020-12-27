package city.windmill.ingameime.client.jni

import city.windmill.ingameime.client.IMEHandler
import city.windmill.ingameime.client.gui.OverlayScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
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
        val resourceNative = Identifier("ingameime", "natives/jni.dll")
        NativeLoader.load(MinecraftClient.getInstance().resourceManager.getResource(resourceNative))
        LOGGER.trace("Initialing window")
        nInitialize(glfwGetWin32Window(MinecraftClient.getInstance().window.handle))
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
                    MinecraftClient.getInstance().keyboard
                        .onChar(MinecraftClient.getInstance().window.handle, ch.toInt(), 0)
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
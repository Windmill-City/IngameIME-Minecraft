package city.windmill.ingameime.client.jni

import city.windmill.ingameime.client.overlay.OverlayScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window
import kotlin.time.ExperimentalTime

@ExperimentalTime
object ExternalBaseIME {
    init {
        val resourceNative = Identifier("ingameime", "natives/jni.dll")
        NativeLoader.load(MinecraftClient.getInstance().resourceManager.getResource(resourceNative))
        nInitialize(glfwGetWin32Window(MinecraftClient.getInstance().window.handle))
    }
    
    var State: Boolean = false
        set(value) {
            field = value
            nSetState(field)
            OverlayScreen.showAlphaMode = field
        }
    
    var FullScreen: Boolean = false
        set(value) {
            field = value
            nSetFullScreen(field)
            if (State) {
                State = false
                State = true
            }
        }
    
    var HandleComposition: Boolean = false
        set(value) {
            field = value
            nSetHandleComposition(field)
        }
    
    var AlphaMode: Boolean = false
    
    //region Natives
    private external fun nInitialize(handle: Long)
    
    @Suppress("unused")
    external fun nUninitialize()
    private external fun nSetState(state: Boolean)
    private external fun nSetFullScreen(fullscreen: Boolean)
    private external fun nSetHandleComposition(handle: Boolean)
    //endregion
    
    //region CallFrom JNI
    @Suppress("unused")
    fun onCandidateList(candidates: Array<String>?) {
        OverlayScreen.candidates = candidates
    }
    
    @Suppress("unused")
    fun onComposition(str: String?, caret: Int, state: CompositionState) {
        when (state) {
            CompositionState.Commit -> {
                str!!.onEach { ch ->
                    MinecraftClient.getInstance().keyboard
                        .onChar(MinecraftClient.getInstance().window.handle, ch.toInt(), 0)
                }
            }
            CompositionState.Start, CompositionState.End, CompositionState.Update -> {
                OverlayScreen.composition = if (str.isNullOrEmpty()) null else str to caret
            }
        }
        OverlayScreen.showAlphaMode = false
    }
    
    @Suppress("unused")
    fun onGetCompExt(): IntArray {
        return OverlayScreen.compositionExt
    }
    
    @Suppress("unused")
    fun onAlphaMode(isAlphaMode: Boolean) {
        AlphaMode = isAlphaMode
        OverlayScreen.showAlphaMode = true
    }
    //endregion
    
    enum class CompositionState {
        Start,
        Update,
        End,
        Commit,
    }
}
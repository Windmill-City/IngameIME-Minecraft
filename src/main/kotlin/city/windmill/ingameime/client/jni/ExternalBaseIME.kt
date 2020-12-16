package city.windmill.ingameime.client.jni

object ExternalBaseIME {
    init {
        System.loadLibrary("IngameIME-JNI")
    }
    
    var State: Boolean = false
        set(value) {
            field = value
            SetState(field)
        }
    
    var FullScreen: Boolean = false
        set(value) {
            field = value
            SetFullScreen(field)
        }
    
    var HandleComposition: Boolean = false
        set(value) {
            field = value
            SetHandleComposition(field)
        }
    
    //region Natives
    external fun Initialize(handle: Long)
    external fun Uninitialize()
    external fun SetState(state: Boolean)
    external fun SetFullScreen(fullscreen: Boolean)
    external fun SetHandleComposition(handle: Boolean)
    //endregion
    
    //region CallFrom JNI
    @Suppress("unused")
    fun onCandidateList(cand: Array<String>) {}
    
    @Suppress("unused")
    fun onComposition(str: String?, caret: Int, state: CompositionState) {
        when (state) {
            CompositionState.Commit -> {
            }
            CompositionState.Update -> {
            }
            CompositionState.Start, CompositionState.End -> {
            }
        }
    }
    
    @Suppress("unused")
    fun onGetCompExt(): IntArray {
        return IntArray(4)
    }
    //endregion
    
    enum class CompositionState {
        Start,
        Update,
        End,
        Commit,
    }
}
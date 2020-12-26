import KeyHandler.CombinationKeyState.CombinationKeyAction.*
import city.windmill.ingameime.client.jni.ExternalBaseIME
import org.apache.logging.log4j.LogManager

object IMEHandler {
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|IMEHandler")!!
    
    interface ICombinationKeyActionListener {
        fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction)
    }
    
    interface ICommitListener {
        fun onCommit()
    }
    
    enum class IMEState {
        DISABLED {
            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState {
                return when (action) {
                    CLICKED -> {
                        ExternalBaseIME.State = true
                        TEMPORARY
                    }
                    DOUBLE_CLICKED -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                    LONG_PRESS -> this
                }
            }
            
            override fun onCommit(): IMEState {
                return this //do nothing
            }
        },
        TEMPORARY {
            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState {
                return when (action) {
                    CLICKED -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    DOUBLE_CLICKED -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                    LONG_PRESS -> this
                }
            }
            
            override fun onCommit(): IMEState {
                ExternalBaseIME.State = false
                return DISABLED
            }
        },
        ENABLED {
            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState {
                return when (action) {
                    CLICKED -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    DOUBLE_CLICKED -> {
                        ExternalBaseIME.State = true
                        this
                    }
                    LONG_PRESS -> this
                }
            }
            
            override fun onCommit(): IMEState {
                return this //do nothing
            }
        };
        
        companion object : ICombinationKeyActionListener, ICommitListener {
            private var imeState = DISABLED
                set(value) {
                    LOGGER.debug("IMEState $field -> $value")
                    field = value
                }
            
            override fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction) {
                imeState = imeState.onAction(action)
            }
            
            override fun onCommit() {
                imeState = imeState.onCommit()
            }
        }
        
        abstract fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState
        abstract fun onCommit(): IMEState
    }
}
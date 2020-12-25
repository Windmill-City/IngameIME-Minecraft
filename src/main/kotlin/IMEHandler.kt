import KeyHandler.HotKeyState.HotKeyAction.*
import city.windmill.ingameime.client.jni.ExternalBaseIME
import org.apache.logging.log4j.LogManager

interface IHotKeyActionListener {
    fun onAction(action: KeyHandler.HotKeyState.HotKeyAction): IMEHandler.IMEState
}

interface IIMECommitListener {
    fun onCommit(): IMEHandler.IMEState
}

object IMEHandler : IHotKeyActionListener, IIMECommitListener {
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|IMEHandler")!!
    private var imeState = IMEState.DISABLED
        set(value) {
            LOGGER.info("IMEState $field -> $value")
            field = value
        }
    
    enum class IMEState : IHotKeyActionListener, IIMECommitListener {
        DISABLED {
            override fun onAction(action: KeyHandler.HotKeyState.HotKeyAction): IMEState {
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
            override fun onAction(action: KeyHandler.HotKeyState.HotKeyAction): IMEState {
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
            override fun onAction(action: KeyHandler.HotKeyState.HotKeyAction): IMEState {
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
    }
    
    override fun onAction(action: KeyHandler.HotKeyState.HotKeyAction): IMEState {
        imeState = imeState.onAction(action)
        return imeState
    }
    
    override fun onCommit(): IMEState {
        imeState = imeState.onCommit()
        return imeState
    }
}
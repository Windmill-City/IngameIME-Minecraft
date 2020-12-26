package city.windmill.ingameime.client

import city.windmill.ingameime.client.KeyHandler.CombinationKeyState.CombinationKeyAction.*
import city.windmill.ingameime.client.jni.ExternalBaseIME
import city.windmill.ingameime.client.jni.ICommitListener
import org.apache.logging.log4j.LogManager

object IMEHandler {
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|IMEHandler")!!
    
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
            
            override fun onScreenState(state: ScreenHandler.ScreenState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.NULL_SCREEN,
                    ScreenHandler.ScreenState.SCREEN_OPEN -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    ScreenHandler.ScreenState.SCREEN_DUMMY_EDIT -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                }
            }
            
            override fun onTextFieldState(state: ScreenHandler.ScreenState.EditState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.EditState.NULL_EDIT -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    ScreenHandler.ScreenState.EditState.EDIT_OPEN -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                }
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
            
            override fun onScreenState(state: ScreenHandler.ScreenState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.NULL_SCREEN,
                    ScreenHandler.ScreenState.SCREEN_OPEN -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    ScreenHandler.ScreenState.SCREEN_DUMMY_EDIT -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                }
            }
            
            override fun onTextFieldState(state: ScreenHandler.ScreenState.EditState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.EditState.NULL_EDIT -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    ScreenHandler.ScreenState.EditState.EDIT_OPEN -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                }
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
                        ENABLED
                    }
                    LONG_PRESS -> this
                }
            }
            
            override fun onCommit(): IMEState {
                return this //do nothing
            }
            
            override fun onScreenState(state: ScreenHandler.ScreenState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.NULL_SCREEN,
                    ScreenHandler.ScreenState.SCREEN_OPEN -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    ScreenHandler.ScreenState.SCREEN_DUMMY_EDIT -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                }
            }
            
            override fun onTextFieldState(state: ScreenHandler.ScreenState.EditState): IMEState {
                return when (state) {
                    ScreenHandler.ScreenState.EditState.NULL_EDIT -> {
                        ExternalBaseIME.State = false
                        DISABLED
                    }
                    ScreenHandler.ScreenState.EditState.EDIT_OPEN -> {
                        ExternalBaseIME.State = true
                        ENABLED
                    }
                }
            }
        };
        
        companion object : ICombinationKeyActionListener, ICommitListener, IScreenStateListener,
            IEditStateListener {
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
            
            override fun onScreenState(state: ScreenHandler.ScreenState) {
                imeState = imeState.onScreenState(state)
            }
            
            override fun onEditState(state: ScreenHandler.ScreenState.EditState) {
                imeState = imeState.onTextFieldState(state)
            }
        }
        
        abstract fun onAction(action: KeyHandler.CombinationKeyState.CombinationKeyAction): IMEState
        abstract fun onCommit(): IMEState
        abstract fun onScreenState(state: ScreenHandler.ScreenState): IMEState
        abstract fun onTextFieldState(state: ScreenHandler.ScreenState.EditState): IMEState
    }
}
package city.windmill.ingameime.client

import city.windmill.ingameime.client.gui.OverlayScreen
import net.minecraft.client.gui.screen.Screen
import org.apache.logging.log4j.LogManager

interface IScreenStateListener {
    fun onScreenState(state: ScreenHandler.ScreenState)
}

interface ITextFieldStateListener {
    fun onTextFieldState(state: ScreenHandler.TextFieldState)
}

object ScreenHandler {
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|ScreenHandler")!!
    
    enum class ScreenState {
        NULL_SCREEN {
            override fun onScreenChange(oldScreen: Screen?, newScreen: Screen?): ScreenState {
                newScreen?.let {
                    currentScreen = newScreen
                    return SCREEN_OPEN
                }
                return this
            }
        },
        SCREEN_OPEN {
            override fun onScreenChange(oldScreen: Screen?, newScreen: Screen?): ScreenState {
                newScreen?.let {
                    currentScreen = newScreen
                    return this
                }
                return NULL_SCREEN
            }
        },
        SCREEN_DUMMY_TEXT_FIELD {
            override fun onScreenChange(oldScreen: Screen?, newScreen: Screen?): ScreenState {
                newScreen?.let {
                    currentScreen = newScreen
                    return this
                }
                return NULL_SCREEN
            }
        };
        
        companion object {
            private var screenState = NULL_SCREEN
                set(value) {
                    LOGGER.info("ScreenState $field -> $value")
                    field = value
                    IMEHandler.IMEState.onScreenState(field)
                }
            private var currentScreen: Screen? = null
            
            fun onScreenChange(oldScreen: Screen?, newScreen: Screen?) {
                LOGGER.info("$oldScreen -> $newScreen")
                screenState = screenState.onScreenChange(oldScreen, newScreen)
            }
        }
        
        abstract fun onScreenChange(oldScreen: Screen?, newScreen: Screen?): ScreenState
        
    }
    
    enum class TextFieldState {
        NULL_TEXTFIELD {
            override fun onTextFieldOpen(textField: Any, caretPos: Pair<Int, Int>): TextFieldState {
                currentTextField = textField
                OverlayScreen.caretPos = caretPos
                return TEXTFIELD_OPEN
            }
            
            override fun onTextFieldCaret(textField: Any, xPos: Int): TextFieldState {
                return this //do nothing
            }
            
            override fun onTextFieldClose(textField: Any): TextFieldState {
                return this //do nothing
            }
        },
        TEXTFIELD_OPEN {
            override fun onTextFieldOpen(textField: Any, caretPos: Pair<Int, Int>): TextFieldState {
                currentTextField = textField
                OverlayScreen.caretPos = caretPos
                return this
            }
            
            override fun onTextFieldCaret(textField: Any, xPos: Int): TextFieldState {
                if (textField == currentTextField) {
                    OverlayScreen.caretPos = xPos to OverlayScreen.caretPos.second
                }
                return this
            }
            
            override fun onTextFieldClose(textField: Any): TextFieldState {
                if (textField == currentTextField) {
                    currentTextField = null
                    return NULL_TEXTFIELD
                }
                return this
            }
        };
        
        companion object : IScreenStateListener {
            private var textFieldState = NULL_TEXTFIELD
                set(value) {
                    LOGGER.info("TextFieldState $textFieldState -> $value")
                    field = value
                    IMEHandler.IMEState.onTextFieldState(field)
                }
            
            private var currentTextField: Any? = null
            
            fun onTextFieldOpen(textField: Any, caretPos: Pair<Int, Int>) {
                if (textField != currentTextField)
                    textFieldState = textFieldState.onTextFieldOpen(textField, caretPos)
            }
            
            fun onTextFieldCaret(textField: Any, xPos: Int) {
                textFieldState = textFieldState.onTextFieldCaret(textField, xPos)
            }
            
            fun onTextFieldClose(textField: Any) {
                textFieldState = textFieldState.onTextFieldClose(textField)
            }
            
            override fun onScreenState(state: ScreenState) {
                when (state) {
                    ScreenState.NULL_SCREEN -> {
                        currentTextField = null
                        textFieldState = NULL_TEXTFIELD
                    }
                    ScreenState.SCREEN_OPEN,
                    ScreenState.SCREEN_DUMMY_TEXT_FIELD -> {
                        //do nothing
                    }
                }
            }
        }
        
        abstract fun onTextFieldOpen(textField: Any, caretPos: Pair<Int, Int>): TextFieldState
        abstract fun onTextFieldCaret(textField: Any, xPos: Int): TextFieldState
        abstract fun onTextFieldClose(textField: Any): TextFieldState
    }
}
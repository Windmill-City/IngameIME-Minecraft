package city.windmill.ingameime.client

import city.windmill.ingameime.client.gui.OverlayScreen
import net.minecraft.client.gui.screen.Screen
import org.apache.logging.log4j.LogManager

interface IScreenStateListener {
    fun onScreenState(state: ScreenHandler.ScreenState)
}

interface IEditStateListener {
    fun onEditState(state: ScreenHandler.ScreenState.EditState)
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
                OverlayScreen.caretPos = 0 to 0
                return NULL_SCREEN
            }
        },
        SCREEN_DUMMY_EDIT {
            override fun onScreenChange(oldScreen: Screen?, newScreen: Screen?): ScreenState {
                newScreen?.let {
                    currentScreen = newScreen
                    return this
                }
                OverlayScreen.caretPos = 0 to 0
                return NULL_SCREEN
            }
        };
        
        companion object {
            private var screenState = NULL_SCREEN
                set(value) {
                    LOGGER.trace("ScreenState $field -> $value")
                    field = value
                    IMEHandler.IMEState.onScreenState(field)
                    EditState.onScreenState(field)
                }
            private var currentScreen: Screen? = null
            
            fun onScreenChange(oldScreen: Screen?, newScreen: Screen?) {
                LOGGER.trace("$oldScreen -> $newScreen")
                screenState = screenState.onScreenChange(oldScreen, newScreen)
            }
        }
        
        abstract fun onScreenChange(oldScreen: Screen?, newScreen: Screen?): ScreenState
        
        enum class EditState {
            NULL_EDIT {
                override fun onEditOpen(edit: Any, caretPos: Pair<Int, Int>): EditState {
                    currentEdit = edit
                    OverlayScreen.caretPos = caretPos
                    return EDIT_OPEN
                }
                
                override fun onEditCaret(edit: Any, caretPos: Pair<Int, Int>): EditState {
                    return this //do nothing
                }
                
                override fun onEditClose(edit: Any): EditState {
                    return this //do nothing
                }
            },
            EDIT_OPEN {
                override fun onEditOpen(edit: Any, caretPos: Pair<Int, Int>): EditState {
                    currentEdit = edit
                    OverlayScreen.caretPos = caretPos
                    return EDIT_OPEN
                }
                
                override fun onEditCaret(edit: Any, caretPos: Pair<Int, Int>): EditState {
                    OverlayScreen.caretPos = caretPos
                    return EDIT_OPEN
                }
                
                override fun onEditClose(edit: Any): EditState {
                    currentEdit = null
                    return NULL_EDIT
                }
            };
            
            companion object : IScreenStateListener {
                private var editState = NULL_EDIT
                    set(value) {
                        LOGGER.trace("EditState $editState -> $value")
                        field = value
                        IMEHandler.IMEState.onEditState(field)
                    }
                
                private var currentEdit: Any? = null
                
                fun onEditOpen(edit: Any, caretPos: Pair<Int, Int>) {
                    if (edit != currentEdit)
                        editState = editState.onEditOpen(edit, caretPos)
                }
                
                fun onEditCaret(edit: Any, caretPos: Pair<Int, Int>) {
                    if (edit == currentEdit && OverlayScreen.caretPos != caretPos)
                        editState = editState.onEditCaret(edit, caretPos)
                }
                
                fun onEditClose(edit: Any) {
                    if (edit == currentEdit)
                        editState = editState.onEditClose(edit)
                }
                
                override fun onScreenState(state: ScreenState) {
                    when (state) {
                        NULL_SCREEN -> {
                            currentEdit = null
                            editState = NULL_EDIT
                        }
                        SCREEN_OPEN,
                        SCREEN_DUMMY_EDIT -> {
                            //do nothing
                        }
                    }
                }
            }
            
            abstract fun onEditOpen(edit: Any, caretPos: Pair<Int, Int>): EditState
            abstract fun onEditCaret(edit: Any, caretPos: Pair<Int, Int>): EditState
            abstract fun onEditClose(edit: Any): EditState
        }
    }
}
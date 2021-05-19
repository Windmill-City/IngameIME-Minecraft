package city.windmill.ingameime.client

import city.windmill.ingameime.client.gui.OverlayScreen
import net.minecraft.client.gui.screens.Screen
import org.apache.logging.log4j.LogManager

fun interface IScreenStateListener {
    fun onScreenState(state: ScreenHandler.ScreenState)
}

fun interface IEditStateListener {
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
                    screenState = NULL_SCREEN //ScreenChange close old one
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
                    screenState = NULL_SCREEN //ScreenChange close old one
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
                    iScreenStateListener.onScreenState(field)
                    EditState.onScreenState(field)
                }
            var currentScreen: Screen? = null
            var iScreenStateListener: IScreenStateListener = IMEHandler.IMEState

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
                    if (edit != currentEdit) {
                        editState = NULL_EDIT //Edit change, close old edit
                        currentEdit = edit
                        OverlayScreen.caretPos = caretPos
                    }
                    return EDIT_OPEN
                }
                
                override fun onEditCaret(edit: Any, caretPos: Pair<Int, Int>): EditState {
                    if (edit == currentEdit)
                        OverlayScreen.caretPos = caretPos
                    return EDIT_OPEN
                }
                
                override fun onEditClose(edit: Any): EditState {
                    if (edit == currentEdit) {
                        currentEdit = null
                        return NULL_EDIT
                    }
                    return EDIT_OPEN
                }
            };
            
            companion object : IScreenStateListener {
                private var editState = NULL_EDIT
                    set(value) {
                        if (field == value) return
                        LOGGER.trace("EditState $editState -> $value")
                        field = value
                        iEditstateListener.onEditState(field)
                    }

                var currentEdit: Any? = null
                var iEditstateListener: IEditStateListener = IMEHandler.IMEState

                fun onEditOpen(edit: Any, caretPos: Pair<Int, Int>) {
                    editState = editState.onEditOpen(edit, caretPos)
                }

                fun onEditCaret(edit: Any, caretPos: Pair<Int, Int>) {
                    editState = editState.onEditCaret(edit, caretPos)
                }

                fun onEditClose(edit: Any) {
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
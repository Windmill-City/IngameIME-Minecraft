package city.windmill.ingameime.client

import city.windmill.ingameime.client.ScreenEvents.EditCaret
import city.windmill.ingameime.client.ScreenEvents.EditClose
import city.windmill.ingameime.client.ScreenEvents.EditOpen
import city.windmill.ingameime.client.ScreenEvents.ScreenChanged
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.screen.Screen

@Environment(EnvType.CLIENT)
object ScreenEvents {
    val SCREEN_CHANGED: Event<ScreenChanged> =
        EventFactory.createArrayBacked(ScreenChanged::class.java) { callbacks: Array<ScreenChanged> ->
            ScreenChanged { oldScreen, newScreen ->
                for (callback in callbacks) {
                    callback.onScreenChanged(oldScreen, newScreen)
                }
            }
        }
    
    val EDIT_OPEN: Event<EditOpen> =
        EventFactory.createArrayBacked(EditOpen::class.java) { callbacks ->
            EditOpen { edit, caretPos ->
                for (callback in callbacks) {
                    callback.onEditOpen(edit, caretPos)
                }
            }
        }
    
    val EDIT_CARET: Event<EditCaret> =
        EventFactory.createArrayBacked(EditCaret::class.java) { callbacks ->
            EditCaret { edit, caretPos ->
                for (callback in callbacks) {
                    callback.onEditCaret(edit, caretPos)
                }
            }
        }
    
    val EDIT_CLOSE: Event<EditClose> =
        EventFactory.createArrayBacked(EditClose::class.java) { callbacks ->
            EditClose { edit ->
                for (callback in callbacks) {
                    callback.onEditClose(edit)
                }
            }
        }
    
    fun interface ScreenChanged {
        fun onScreenChanged(oldScreen: Screen?, newScreen: Screen?)
    }
    
    fun interface EditOpen {
        fun onEditOpen(edit: Any, caretPos: Pair<Int, Int>)
    }
    
    fun interface EditCaret {
        fun onEditCaret(edit: Any, caretPos: Pair<Int, Int>)
    }
    
    fun interface EditClose {
        fun onEditClose(edit: Any)
    }
}
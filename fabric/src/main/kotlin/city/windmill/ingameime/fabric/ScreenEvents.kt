package city.windmill.ingameime.fabric

import city.windmill.ingameime.fabric.ScreenEvents.EditCaret
import city.windmill.ingameime.fabric.ScreenEvents.EditClose
import city.windmill.ingameime.fabric.ScreenEvents.EditOpen
import city.windmill.ingameime.fabric.ScreenEvents.MouseMove
import city.windmill.ingameime.fabric.ScreenEvents.ScreenChanged
import city.windmill.ingameime.fabric.ScreenEvents.WindowSizeChanged
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.screens.Screen

@Environment(EnvType.CLIENT)
object ScreenEvents {
    val SCREEN_MOUSE_MOVE: Event<MouseMove> =
        EventFactory.createArrayBacked(MouseMove::class.java) { callbacks ->
            MouseMove { prevX, prevY, curX, curY ->
                callbacks.forEach { it.onMouseMove(prevX, prevY, curX, curY) }
            }
        }

    val WINDOW_SIZE_CHANGED: Event<WindowSizeChanged> =
        EventFactory.createArrayBacked(WindowSizeChanged::class.java) { callbacks ->
            WindowSizeChanged { sizeX, sizeY -> callbacks.forEach { it.onWindowSizeChanged(sizeX, sizeY) } }
        }

    val SCREEN_CHANGED: Event<ScreenChanged> =
        EventFactory.createArrayBacked(ScreenChanged::class.java) { callbacks ->
            ScreenChanged { oldScreen, newScreen -> callbacks.forEach { it.onScreenChanged(oldScreen, newScreen) } }
        }

    val EDIT_OPEN: Event<EditOpen> =
        EventFactory.createArrayBacked(EditOpen::class.java) { callbacks ->
            EditOpen { edit, caretPos -> callbacks.forEach { it.onEditOpen(edit, caretPos) } }
        }

    val EDIT_CARET: Event<EditCaret> =
        EventFactory.createArrayBacked(EditCaret::class.java) { callbacks ->
            EditCaret { edit, caretPos -> callbacks.forEach { it.onEditCaret(edit, caretPos) } }
        }

    val EDIT_CLOSE: Event<EditClose> =
        EventFactory.createArrayBacked(EditClose::class.java) { callbacks ->
            EditClose { edit -> callbacks.forEach { it.onEditClose(edit) } }
        }

    fun interface MouseMove {
        fun onMouseMove(prevX: Int, prevY: Int, curX: Int, curY: Int)
    }

    fun interface WindowSizeChanged {
        fun onWindowSizeChanged(sizeX: Int, sizeY: Int)
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
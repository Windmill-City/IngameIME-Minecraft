package city.windmill.ingameime.client

import city.windmill.ingameime.client.ScreenEvents.ScreenChanged
import city.windmill.ingameime.client.ScreenEvents.TextFieldSelectionChanged
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
    
    val TEXT_FIELD_SEL_CHANGED =
        EventFactory.createArrayBacked(TextFieldSelectionChanged::class.java) { callbacks ->
            TextFieldSelectionChanged { textfield, selected ->
                for (callback in callbacks) {
                    callback.onSelectionChanged(textfield, selected)
                }
            }
        }
    
    fun interface ScreenChanged {
        fun onScreenChanged(oldScreen: Screen?, newScreen: Screen?)
    }
    
    fun interface TextFieldSelectionChanged {
        fun onSelectionChanged(textfield: Any, selected: Boolean)
    }
}
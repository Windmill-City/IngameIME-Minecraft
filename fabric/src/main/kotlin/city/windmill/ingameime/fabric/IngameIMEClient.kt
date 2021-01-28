package city.windmill.ingameime.fabric

import city.windmill.ingameime.client.KeyHandler
import city.windmill.ingameime.client.ScreenHandler
import city.windmill.ingameime.client.gui.OverlayScreen
import city.windmill.ingameime.client.jni.ExternalBaseIME
import city.windmill.ingameime.fabric.ScreenEvents.EDIT_CARET
import city.windmill.ingameime.fabric.ScreenEvents.EDIT_CLOSE
import city.windmill.ingameime.fabric.ScreenEvents.EDIT_OPEN
import city.windmill.ingameime.fabric.ScreenEvents.SCREEN_CHANGED
import city.windmill.ingameime.fabric.ScreenEvents.SCREEN_SIZE_CHANGED
import me.shedaniel.cloth.callbacks.client.ScreenKeyPressedCallback
import me.shedaniel.cloth.callbacks.client.ScreenKeyReleasedCallback
import me.shedaniel.cloth.callbacks.client.ScreenRenderCallback
import me.shedaniel.cloth.hooks.ClothClientHooks
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionResult
import org.apache.logging.log4j.LogManager

@Environment(EnvType.CLIENT)
object IngameIMEClient : ClientModInitializer {
    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    val LOGGER = LogManager.getFormatterLogger("IngameIME")!!
    override fun onInitializeClient() {
        if (Util.getPlatform() == Util.OS.WINDOWS) {
            LOGGER.info("it is Windows OS! Loading mod...")
            ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted {
                ClothClientHooks.SCREEN_LATE_RENDER.register(ScreenRenderCallback.Post { _, _, mouseX, mouseY, delta ->
                    OverlayScreen.render(mouseX, mouseY, delta)
                })
                ClothClientHooks.SCREEN_KEY_PRESSED.register(ScreenKeyPressedCallback { _, _, keyCode, scanCode, modifiers ->
                    if (KeyHandler.KeyState.onKeyDown(keyCode, scanCode, modifiers))
                        InteractionResult.SUCCESS
                    else
                        InteractionResult.PASS
                })
                ClothClientHooks.SCREEN_KEY_RELEASED.register(ScreenKeyReleasedCallback { _, _, keyCode, scanCode, modifiers ->
                    if (KeyHandler.KeyState.onKeyUp(keyCode, scanCode, modifiers))
                        InteractionResult.SUCCESS
                    else
                        InteractionResult.PASS
                })
                SCREEN_SIZE_CHANGED.register(ScreenEvents.ScreenSizeChanged { _, _ ->
                    ExternalBaseIME.FullScreen = Minecraft.getInstance().window.isFullscreen
                })
                with(ScreenHandler.ScreenState) {
                    SCREEN_CHANGED.register(ScreenEvents.ScreenChanged(::onScreenChange))
                }
                with(ScreenHandler.ScreenState.EditState) {
                    EDIT_OPEN.register(ScreenEvents.EditOpen(::onEditOpen))
                    EDIT_CARET.register(ScreenEvents.EditCaret(::onEditCaret))
                    EDIT_CLOSE.register(ScreenEvents.EditClose(::onEditClose))
                }
            })
            KeyBindingHelper.registerKeyBinding(KeyHandler.toogleKey)
        } else
            LOGGER.warn("This mod cant work in ${Util.getPlatform()} !")
    }
}
package city.windmill.ingameime.fabric

import city.windmill.ingameime.client.KeyHandler
import city.windmill.ingameime.client.ScreenHandler
import city.windmill.ingameime.client.gui.OverlayScreen
import city.windmill.ingameime.client.jni.ExternalBaseIME
import city.windmill.ingameime.fabric.ScreenEvents.EDIT_CARET
import city.windmill.ingameime.fabric.ScreenEvents.EDIT_CLOSE
import city.windmill.ingameime.fabric.ScreenEvents.EDIT_OPEN
import city.windmill.ingameime.fabric.ScreenEvents.SCREEN_CHANGED
import ladysnake.satin.api.event.ResolutionChangeCallback
import me.shedaniel.cloth.api.client.events.v0.ClothClientHooks
import me.shedaniel.cloth.api.client.events.v0.ScreenKeyPressedCallback
import me.shedaniel.cloth.api.client.events.v0.ScreenKeyReleasedCallback
import me.shedaniel.cloth.api.client.events.v0.ScreenRenderCallback
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
class IngameIMEClient : ClientModInitializer {
    val LOGGER = LogManager.getFormatterLogger("IngameIME")
    override fun onInitializeClient() {
        if (Util.getPlatform() == Util.OS.WINDOWS) {
            LOGGER.info("it is Windows OS! Loading mod")
            ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted {
                ClothClientHooks.SCREEN_LATE_RENDER.register(ScreenRenderCallback.Post { matrixStack, _, _, mouseX, mouseY, delta ->
                    OverlayScreen.render(matrixStack, mouseX, mouseY, delta)
                })
                ClothClientHooks.SCREEN_KEY_PRESSED.register(ScreenKeyPressedCallback { _, _, keyCode, scanCode, modifier ->
                    if (KeyHandler.KeyState.onKeyDown(keyCode, scanCode, modifier))
                        InteractionResult.CONSUME
                    else
                        InteractionResult.PASS
                })
                ClothClientHooks.SCREEN_KEY_RELEASED.register(ScreenKeyReleasedCallback { _, _, keyCode, scanCode, modifier ->
                    if (KeyHandler.KeyState.onKeyUp(keyCode, scanCode, modifier))
                        InteractionResult.CONSUME
                    else
                        InteractionResult.PASS
                })
                ResolutionChangeCallback.EVENT.register(ResolutionChangeCallback { _, _ ->
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
                ExternalBaseIME.FullScreen = Minecraft.getInstance().window.isFullscreen
            })
            KeyBindingHelper.registerKeyBinding(KeyHandler.toogleKey)
        } else
            LOGGER.warn("This mod cant work in ${Util.getPlatform()}")
    }
}
package city.windmill.ingameime.client

import city.windmill.ingameime.client.gui.OverlayScreen
import city.windmill.ingameime.client.jni.ExternalBaseIME
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
import net.minecraft.client.MinecraftClient
import net.minecraft.util.ActionResult
import net.minecraft.util.Util

@Environment(EnvType.CLIENT)
class IngameIMEClient : ClientModInitializer {
    override fun onInitializeClient() {
        if (Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS) {
            ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted {
                ClothClientHooks.SCREEN_LATE_RENDER.register(ScreenRenderCallback.Post { matrixStack, _, _, mouseX, mouseY, delta ->
                    OverlayScreen.render(matrixStack, mouseX, mouseY, delta)
                })
                ClothClientHooks.SCREEN_KEY_PRESSED.register(ScreenKeyPressedCallback { _, _, keyCode, scanCode, modifier ->
                    if (KeyHandler.KeyState.onKeyDown(keyCode, scanCode, modifier))
                        ActionResult.CONSUME
                    else
                        ActionResult.PASS
                })
                ClothClientHooks.SCREEN_KEY_RELEASED.register(ScreenKeyReleasedCallback { _, _, keyCode, scanCode, modifier ->
                    if (KeyHandler.KeyState.onKeyUp(keyCode, scanCode, modifier))
                        ActionResult.CONSUME
                    else
                        ActionResult.PASS
                })
                ResolutionChangeCallback.EVENT.register(ResolutionChangeCallback { _, _ ->
                    ExternalBaseIME.FullScreen = MinecraftClient.getInstance().window.isFullscreen
                })
                ExternalBaseIME.FullScreen = MinecraftClient.getInstance().window.isFullscreen
            })
            KeyBindingHelper.registerKeyBinding(KeyHandler.hotKey)
        }
    }
}
package city.windmill.ingameime.client

import city.windmill.ingameime.client.ScreenEvents.TEXT_FIELD_SEL_CHANGED
import city.windmill.ingameime.client.gui.OverlayScreen
import city.windmill.ingameime.client.jni.ExternalBaseIME
import ladysnake.satin.api.event.ResolutionChangeCallback
import me.shedaniel.cloth.api.client.events.v0.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.TextFieldWidget
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
                ClothClientHooks.SCREEN_INIT_POST.register(ScreenInitCallback.Post { _, screen, _ ->
                    screen.focused?.let {
                        if (it is TextFieldWidget)
                            TEXT_FIELD_SEL_CHANGED.invoker().onSelectionChanged(it, true)
                    }
                })
                ResolutionChangeCallback.EVENT.register(ResolutionChangeCallback { _, _ ->
                    ExternalBaseIME.FullScreen = MinecraftClient.getInstance().window.isFullscreen
                })
                ScreenEvents.SCREEN_CHANGED.register(ScreenEvents.ScreenChanged { oldScreen, newScreen ->
                    ScreenHandler.ScreenState.onScreenChange(oldScreen, newScreen)
                })
                TEXT_FIELD_SEL_CHANGED.register(ScreenEvents.TextFieldSelectionChanged { textfield, selected ->
                    when (textfield) {
                        is TextFieldWidget -> {
                            if (selected)
                                ScreenHandler.TextFieldState.onTextFieldOpen(
                                    textfield,
                                    textfield.x to textfield.y + (textfield.height - 8) / 2
                                )
                            else
                                ScreenHandler.TextFieldState.onTextFieldClose(textfield)
                        }
                        else -> {
                            if (selected)
                                ScreenHandler.TextFieldState.onTextFieldOpen(textfield, 0 to 0)
                            else
                                ScreenHandler.TextFieldState.onTextFieldClose(textfield)
                        }
                    }
                })
                ExternalBaseIME.FullScreen = MinecraftClient.getInstance().window.isFullscreen
            })
            KeyBindingHelper.registerKeyBinding(KeyHandler.hotKey)
        }
    }
}
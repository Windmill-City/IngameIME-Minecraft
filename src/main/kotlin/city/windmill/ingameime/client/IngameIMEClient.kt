package city.windmill.ingameime.client

import city.windmill.ingameime.client.jni.ExternalBaseIME
import city.windmill.ingameime.client.overlay.OverlayScreen
import me.shedaniel.cloth.api.client.events.v0.ClothClientHooks
import me.shedaniel.cloth.api.client.events.v0.ScreenRenderCallback
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Util
import org.lwjgl.glfw.GLFW
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Environment(EnvType.CLIENT)
class IngameIMEClient : ClientModInitializer {
    private var hotKey: KeyBinding? = null
    private var keyDown = false
    
    private val KeyBinding.isKeyDown: Boolean
        get() {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, this.boundKey.code)
        }
    
    override fun onInitializeClient() {
        if (Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS) {
            ClothClientHooks.SCREEN_LATE_RENDER.register(ScreenRenderCallback.Post(fun(
                matrices: MatrixStack,
                _: MinecraftClient,
                _: Screen,
                mouseX: Int,
                mouseY: Int,
                delta: Float
            ) {
                OverlayScreen.render(matrices, mouseX, mouseY, delta)
            }))
            hotKey = KeyBindingHelper.registerKeyBinding(
                KeyBinding(
                    "key.ingameime.hotkey",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_HOME,
                    "category.ingameime.keybinding"
                )
            )
            ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
                if (hotKey!!.isKeyDown && !keyDown) {
                    ExternalBaseIME.State = ExternalBaseIME.State.not()
                    ExternalBaseIME.HandleComposition = true
                    keyDown = true
                }else if (!hotKey!!.isKeyDown) keyDown = false
                if(MinecraftClient.getInstance().currentScreen == null) ExternalBaseIME.State = false
                with(MinecraftClient.getInstance().window.isFullscreen){
                    if (this != ExternalBaseIME.FullScreen)
                        ExternalBaseIME.FullScreen = this
                }
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, GLFW.GLFW_KEY_PAGE_UP))
                    OverlayScreen.caretPos = with(OverlayScreen.caretPos){
                        first + 10 to second + 10
                    }
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, GLFW.GLFW_KEY_PAGE_DOWN))
                    OverlayScreen.caretPos = with(OverlayScreen.caretPos){
                        first - 10 to second - 10
                    }
            })
            
        }
    }
}
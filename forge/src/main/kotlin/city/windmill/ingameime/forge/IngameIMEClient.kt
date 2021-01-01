package city.windmill.ingameime.forge

import city.windmill.ingameime.client.KeyHandler
import city.windmill.ingameime.client.ScreenHandler
import city.windmill.ingameime.client.gui.OverlayScreen
import city.windmill.ingameime.client.jni.ExternalBaseIME
import net.minecraft.client.Minecraft
import net.minecraft.util.Util
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("ingameime")
object IngameIMEClient {
    private val LOGGER = LogManager.getLogger()
    val INGAMEIME_BUS = MOD_BUS
    
    init {
        if (Util.getPlatform() == Util.OS.WINDOWS) {
            LOGGER.info("it is Windows OS! Loading mod")
            with(INGAMEIME_BUS) {
                addListener(::onClientSetup)
                addListener(::enqueueIMC)
            }
        }
        LOGGER.warn("This mod cant work in ${Util.getPlatform()}")
    }
    
    private fun onClientSetup(event: FMLClientSetupEvent) {
        ClientRegistry.registerKeyBinding(KeyHandler.toogleKey)
    }
    
    private fun enqueueIMC(event: InterModEnqueueEvent) {
        with(FORGE_BUS) {
            addListener<GuiScreenEvent.DrawScreenEvent.Post> {
                OverlayScreen.render(it.matrixStack, it.mouseX, it.mouseY, it.renderPartialTicks)
            }
            addListener<GuiScreenEvent.KeyboardKeyPressedEvent.Pre> {
                it.isCanceled = KeyHandler.KeyState.onKeyDown(it.keyCode, it.scanCode, it.modifiers)
            }
            addListener<GuiScreenEvent.KeyboardKeyReleasedEvent.Pre> {
                it.isCanceled = KeyHandler.KeyState.onKeyUp(it.keyCode, it.scanCode, it.modifiers)
            }
        }
        with(INGAMEIME_BUS) {
            addListener<ScreenEvents.WindowSizeChanged> {
                ExternalBaseIME.FullScreen = Minecraft.getInstance().window.isFullscreen
            }
            addListener<ScreenEvents.ScreenChanged> {
                ScreenHandler.ScreenState.onScreenChange(it.oldScreen, it.newScreen)
            }
            addListener<ScreenEvents.EditOpen> {
                ScreenHandler.ScreenState.EditState.onEditOpen(it.edit, it.caretPos)
            }
            addListener<ScreenEvents.EditCaret> {
                ScreenHandler.ScreenState.EditState.onEditCaret(it.edit, it.caretPos)
            }
            addListener<ScreenEvents.EditClose> {
                ScreenHandler.ScreenState.EditState.onEditClose(it.edit)
            }
        }
    }
}
package city.windmill.ingameime.forge

import city.windmill.ingameime.client.IMEHandler
import city.windmill.ingameime.client.KeyHandler
import city.windmill.ingameime.client.ScreenHandler
import city.windmill.ingameime.client.gui.OverlayScreen
import city.windmill.ingameime.client.jni.ExternalBaseIME
import cpw.mods.modlauncher.Launcher
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.*
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.ExtensionPoint
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.network.FMLNetworkConstants
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist
import java.util.function.BiFunction
import java.util.function.BiPredicate
import java.util.function.Supplier


@Mod("ingameime")
object IngameIMEClient {
    private val LOGGER = LogManager.getLogger()
    val INGAMEIME_BUS = MOD_BUS

    /**
     * Track mouse move
     */
    private var prevX = 0
    private var prevY = 0

    init {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        LOADING_CONTEXT.registerExtensionPoint(
            ExtensionPoint.DISPLAYTEST
        ) {
            org.apache.commons.lang3.tuple.Pair.of(
                Supplier { FMLNetworkConstants.IGNORESERVERONLY },
                BiPredicate { _, _ -> true })
        }
        LOADING_CONTEXT.registerExtensionPoint(
            ExtensionPoint.CONFIGGUIFACTORY
        ) {
            BiFunction { _, parent ->
                return@BiFunction ConfigHandlerImpl.createConfigScreen().setParentScreen(parent).build()
            }
        }

        runForDist({
            if (Util.getPlatform() == Util.OS.WINDOWS) {
                LOGGER.info("it is Windows OS! Loading mod...")

                with(INGAMEIME_BUS) {
                    addListener(::onClientSetup)
                    addListener(::enqueueIMC)
                }
            } else
                LOGGER.warn("This mod cant work in ${Util.getPlatform()} !")
        }) { LOGGER.warn("This mod cant work in a DelicateServer!") }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onClientSetup(event: FMLClientSetupEvent) {
        ClientRegistry.registerKeyBinding(KeyHandler.toggleKey)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun enqueueIMC(event: InterModEnqueueEvent) {
        with(FORGE_BUS) {
            addListener<GuiScreenEvent.DrawScreenEvent.Post> {
                //Track mouse move here
                if (prevX != it.mouseX || prevY != it.mouseY) {
                    INGAMEIME_BUS.post(ScreenEvents.MouseMove(prevX, prevY, it.mouseX, it.mouseY))

                    prevX = it.mouseX
                    prevY = it.mouseY
                }

                OverlayScreen.render(it.mouseX, it.mouseY, it.renderPartialTicks)
            }
            addListener<GuiScreenEvent.KeyboardKeyPressedEvent.Pre> {
                it.isCanceled = KeyHandler.KeyState.onKeyDown(it.keyCode, it.scanCode, it.modifiers)
            }
            addListener<GuiScreenEvent.KeyboardKeyReleasedEvent.Pre> {
                it.isCanceled = KeyHandler.KeyState.onKeyUp(it.keyCode, it.scanCode, it.modifiers)
            }
            addListener<PlayerEvent.PlayerLoggedInEvent> {
                if (!Launcher.INSTANCE.environment().findLaunchPlugin("mixin").isPresent) {
                    it.player.sendMessage(
                        TranslatableComponent("message.nomixin",
                            ComponentUtils.wrapInSquareBrackets(
                                TextComponent("MixinBootStrap").apply {
                                    style.clickEvent = ClickEvent(
                                        ClickEvent.Action.OPEN_URL,
                                        "https://www.curseforge.com/minecraft/mc-mods/mixinbootstrap"
                                    )
                                    style.hoverEvent = HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        TranslatableComponent("tooltip.openlink")
                                    )
                                    style.isUnderlined = true
                                    style.isBold = false
                                    style.color = ChatFormatting.AQUA
                                }
                            ).apply { style.isBold = true }
                        )
                    )
                }
            }
        }
        with(INGAMEIME_BUS) {
            addListener<ScreenEvents.MouseMove> {
                IMEHandler.IMEState.onMouseMove()
            }
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
        ConfigHandlerImpl.initialConfig()
        //Ensure native dll are loaded, or crash the game
        LOGGER.info("Current IME State:${ExternalBaseIME.State}")
    }
}
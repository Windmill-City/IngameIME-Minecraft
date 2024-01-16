package city.windmill.ingameime;

import city.windmill.ingameime.gui.OverlayScreen;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;

import static city.windmill.ingameime.IngameIME_Forge.LOG;
import static org.lwjgl.input.Keyboard.KEY_HOME;

public class ClientProxy extends CommonProxy implements IMEventHandler {
    public static ClientProxy INSTANCE = null;
    public static OverlayScreen Screen = new OverlayScreen();
    public static KeyBinding KeyBind = new KeyBinding("ingameime.key.desc", KEY_HOME, "IngameIME");
    public static city.windmill.ingameime.IMEventHandler IMEventHandler = IMStates.Disabled;
    private static boolean IsKeyDown = false;

    @SubscribeEvent
    public void onRenderScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        ClientProxy.Screen.draw();

        if (Keyboard.isKeyDown(ClientProxy.KeyBind.getKeyCode())) {
            IsKeyDown = true;
        } else if (IsKeyDown) {
            IsKeyDown = false;
            onToggleKey();
        }

        if (Config.TurnOffOnMouseMove.getBoolean())
            if (IMEventHandler == IMStates.OpenedManual && (Mouse.getDX() > 0 || Mouse.getDY() > 0)) {
                onMouseMove();
            }
    }

    public void preInit(FMLPreInitializationEvent event) {
        INSTANCE = this;
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        ClientRegistry.registerKeyBinding(KeyBind);
        Internal.loadLibrary();
        Internal.createInputCtx();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public IMStates onScreenClose() {
        IMEventHandler = IMEventHandler.onScreenClose();
        return null;
    }

    @Override
    public IMStates onControlFocus(@Nonnull Object control, boolean focused) {
        IMEventHandler = IMEventHandler.onControlFocus(control, focused);
        return null;
    }

    @Override
    public IMStates onScreenOpen(Object screen) {
        IMEventHandler = IMEventHandler.onScreenOpen(screen);
        return null;
    }

    @Override
    public IMStates onToggleKey() {
        IMEventHandler = IMEventHandler.onToggleKey();
        return null;
    }

    @Override
    public IMStates onMouseMove() {
        IMEventHandler = IMEventHandler.onMouseMove();
        return null;
    }
}

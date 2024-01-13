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

import static city.windmill.ingameime.IngameIME_Forge.LOG;
import static org.lwjgl.input.Keyboard.KEY_HOME;

public class ClientProxy extends CommonProxy {
    public static OverlayScreen Screen = new OverlayScreen();
    public static KeyBinding KeyBind = new KeyBinding("ingameime.key.desc", KEY_HOME, "IngameIME");
    public static boolean IsToggledManually = false;
    private static boolean IsKeyDown = false;

    @SubscribeEvent
    public void onRenderScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        ClientProxy.Screen.draw();

        if (Keyboard.isKeyDown(ClientProxy.KeyBind.getKeyCode())) {
            IsKeyDown = true;
        } else if (IsKeyDown) {
            IsKeyDown = false;
            ClientProxy.IsToggledManually = true;
            IngameIMEContext.toggleInputMethod();
            LOG.info("Toggled by keybinding");
        }

        if (Config.TurnOffOnMouseMove.getBoolean())
            if (ClientProxy.IsToggledManually && (Mouse.getDX() > 0 || Mouse.getDY() > 0)) {
                IngameIMEContext.setActivated(false);
                LOG.info("Turned off by mouse move");
            }
    }

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        ClientRegistry.registerKeyBinding(KeyBind);
        IngameIMEContext.loadLibrary();
        IngameIMEContext.createInputCtx();
        MinecraftForge.EVENT_BUS.register(this);
    }
}

package city.windmill.ingameime;

import city.windmill.ingameime.gui.OverlayScreen;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ingameime.InputContext;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import static org.lwjgl.input.Keyboard.KEY_HOME;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.7.10]", acceptableRemoteVersions = "*")
public class IngameIME_Forge {
    public static final Logger LOG = LogManager.getLogger(Tags.MODNAME);
    public static boolean LIBRARY_LOADED = false;
    public static InputContext InputCtx = null;
    public static OverlayScreen Screen = new OverlayScreen();
    public static KeyBinding KeyBind = new KeyBinding("Toggle Input Method", KEY_HOME, "IngameIME");
    @SidedProxy(clientSide = "city.windmill.ingameime.ClientProxy")
    public static ClientProxy proxy;
    private boolean IsKeyDown = false;

    public static boolean getActivated() {
        if (InputCtx != null) {
            return InputCtx.getActivated();
        }
        return false;
    }

    public static void setActivated(boolean activated) {
        if (InputCtx != null) {
            InputCtx.setActivated(activated);
            LOG.info("InputMethod activated: {}", activated);
        }
    }

    @SubscribeEvent
    public void onRenderScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        Screen.draw();

        if (Keyboard.isKeyDown(KeyBind.getKeyCode())) {
            IsKeyDown = true;
        } else if (IsKeyDown) {
            IsKeyDown = false;
            setActivated(!getActivated());
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}

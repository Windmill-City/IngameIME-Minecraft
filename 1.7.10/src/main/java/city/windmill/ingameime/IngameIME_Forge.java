package city.windmill.ingameime;

import city.windmill.ingameime.gui.OverlayScreen;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ingameime.InputContext;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.7.10]")
public class IngameIME_Forge {

    public static final Logger LOG = LogManager.getLogger(Tags.MODNAME);
    public static boolean LIBRARY_LOADED = false;
    public static InputContext InputCtx = null;
    public static OverlayScreen Screen = new OverlayScreen();

    @SidedProxy(clientSide = "city.windmill.ingameime.ClientProxy")
    public static ClientProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
        ClientProxy.destroyInputCtx();
        ClientProxy.createInputCtx();
    }

    @SubscribeEvent
    public void onRenderScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        Screen.draw();
    }
}

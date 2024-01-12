package city.windmill.ingameime;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Tags.MODID,
        version = Tags.VERSION,
        name = Tags.MODNAME,
        acceptedMinecraftVersions = "[1.7.10]",
        acceptableRemoteVersions = "*",
        dependencies = "required-after:unimixins;after:NotEnoughItems"
)
public class IngameIME_Forge {
    public static final Logger LOG = LogManager.getLogger(Tags.MODNAME);
    @SidedProxy(clientSide = "city.windmill.ingameime.ClientProxy", serverSide = "city.windmill.ingameime.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
}

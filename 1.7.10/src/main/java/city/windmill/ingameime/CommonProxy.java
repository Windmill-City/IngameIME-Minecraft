package city.windmill.ingameime;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        IngameIME_Forge.LOG.info("This mod is a Client side only mod, skip loading...");
    }
}

package city.windmill.ingameime.mixins;

import city.windmill.ingameime.ClientProxy;
import city.windmill.ingameime.Config;
import city.windmill.ingameime.IngameIME_Forge;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "toggleFullscreen", at = @At(value = "HEAD"))
    void preToggleFullscreen(CallbackInfo ci) {
        if (IngameIME_Forge.InputCtx != null) {
            IngameIME_Forge.InputCtx.delete();
            IngameIME_Forge.LOG.info("InputContext has destroyed!");
        }
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "RETURN"))
    void postToggleFullscreen(CallbackInfo ci) {
        if (Minecraft.getMinecraft().isFullScreen()) {
            Config.UiLess_Windows.set(true);
        }
        ClientProxy.createInputCtx();
    }
}

package city.windmill.ingameime.mixins;

import city.windmill.ingameime.ClientProxy;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "toggleFullscreen", at = @At(value = "HEAD"))
    void preToggleFullscreen(CallbackInfo ci) {
        ClientProxy.destroyInputCtx();
    }


    @Inject(method = "toggleFullscreen", at = @At(value = "RETURN"))
    void postToggleFullscreen(CallbackInfo ci) {
        ClientProxy.createInputCtx();
    }
}

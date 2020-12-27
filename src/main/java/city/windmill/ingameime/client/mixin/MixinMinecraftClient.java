package city.windmill.ingameime.client.mixin;

import city.windmill.ingameime.client.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Shadow
    public Screen currentScreen;

    @Inject(method = "openScreen", at = @At("HEAD"))
    public void onScreenChange(Screen screenIn, CallbackInfo info) {
        ScreenEvents.INSTANCE.getSCREEN_CHANGED().invoker().onScreenChanged(currentScreen, screenIn);
    }
}

package city.windmill.ingameime.forge.mixin;

import city.windmill.ingameime.forge.IngameIMEClient;
import city.windmill.ingameime.forge.ScreenEvents;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
class MixinMinecraft {
    @Shadow
    public Screen screen;
    @Final
    @Shadow
    private MainWindow window;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onScreenChange(Screen screenIn, CallbackInfo info) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.ScreenChanged(screen, screenIn));
    }

    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void onScreenSizeChanged(CallbackInfo info){
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.WindowSizeChanged(window.getWidth(), window.getHeight()));
    }
}

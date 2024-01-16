package city.windmill.ingameime.mixins;

import city.windmill.ingameime.ClientProxy;
import city.windmill.ingameime.Internal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "toggleFullscreen", at = @At(value = "HEAD"))
    void preToggleFullscreen(CallbackInfo ci) {
        Internal.destroyInputCtx();
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "RETURN"))
    void postToggleFullscreen(CallbackInfo ci) {
        Internal.createInputCtx();
    }

    @Inject(method = "displayGuiScreen", at = @At(value = "RETURN"))
    void postDisplayScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
        // Reset pos when screen changes
        ClientProxy.Screen.setCaretPos(0, 0);
        // Disable input method when not screen
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen == null)
            ClientProxy.INSTANCE.onScreenClose();
        else
            ClientProxy.INSTANCE.onScreenOpen(currentScreen);
    }
}

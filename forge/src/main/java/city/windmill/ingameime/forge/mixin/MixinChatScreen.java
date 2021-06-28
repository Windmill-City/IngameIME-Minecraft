package city.windmill.ingameime.forge.mixin;

import city.windmill.ingameime.client.ConfigHandler;
import city.windmill.ingameime.client.IMEHandler;
import city.windmill.ingameime.client.ScreenHandler;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @Shadow
    protected EditBox input;

    @Inject(method = "moveInHistory", at = @At("RETURN"))
    private void onHistoryMove(int i, CallbackInfo ci) {
        if (input.getValue().startsWith("/") && ConfigHandler.INSTANCE.getDisableIMEInCommandMode()) {
            IMEHandler.IMEState.Companion.onEditState(ScreenHandler.ScreenState.EditState.NULL_EDIT);
        } else IMEHandler.IMEState.Companion.onEditState(ScreenHandler.ScreenState.EditState.EDIT_OPEN);
    }
}

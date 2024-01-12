package city.windmill.ingameime.mixins;

import city.windmill.ingameime.IngameIME_Forge;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiTextField.class)
public class MixinGuiTextField {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
            method = "drawTextBox",
            at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPEQ, ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    void onDrawCaret(CallbackInfo ci, int i, int j, int k, String s, boolean flag, boolean flag1, int l, int i1, int j1, boolean flag2, int k1) {
        IngameIME_Forge.Screen.setCaretPos(k1, i1);
    }

    @Inject(method = "setFocused", at = @At(value = "HEAD"))
    void onSetFocus(boolean focused, CallbackInfo ci) {
        IngameIME_Forge.setActivated(focused);
    }
}

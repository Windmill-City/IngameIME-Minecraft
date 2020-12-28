package city.windmill.ingameime.forge.mixin;

import city.windmill.ingameime.forge.IngameIMEClient;
import city.windmill.ingameime.forge.ScreenEvents;
import com.mojang.blaze3d.matrix.MatrixStack;
import kotlin.Pair;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TextFieldWidget.class)
abstract class MixinTextFieldWidget extends Widget {
    @Shadow
    private boolean bordered;

    public MixinTextFieldWidget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_, ITextComponent p_i232254_5_) {
        super(p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, p_i232254_5_);
    }

    @Inject(method = {"setFocus", "onFocusedChanged"}, at = @At("HEAD"))
    private void onSelected(boolean selected, CallbackInfo info) {
        int caretX = bordered ? x + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        if (selected)
            IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>(caretX, caretY)));
        else
            IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditClose(this));
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I",
            shift = At.Shift.BEFORE,
            ordinal = 0))
    private void onFocused(double double_1, double double_2, int int_1, CallbackInfoReturnable<Boolean> cir) {
        int caretX = bordered ? x + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>(caretX, caretY)));
    }

    @Inject(method = "renderButton",
            at = @At(value = "INVOKE", target = "java/lang/String.isEmpty()Z", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret(MatrixStack matrices, int arg1, int arg2, float arg3, CallbackInfo ci, int l, int m, int n, String string, boolean bl, boolean bl2, int o, int p, int q, boolean bl3, int r) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>(r, p)));
    }
}

//@Mixin(TextFieldWidget.class)
//abstract class MixinTextFieldWidget {
//    @Shadow(remap = false)
//    private boolean hasBorder;
//    @Shadow(remap = false)
//    private Rectangle bounds;
//
//    @Inject(method = "setFocused", at = @At("HEAD"), remap = false)
//    private void onSelected(boolean selected, CallbackInfo info) {
//        int caretX = hasBorder ? bounds.x + 4 : bounds.x;
//        int caretY = hasBorder ? bounds.y + (bounds.height - 8) / 2 : bounds.y;
//        if (selected)
//            ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(caretX, caretY));
//        else
//            ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
//    }
//
//    @Inject(method = "render",
//            at = @At(value = "INVOKE", target = "java/lang/String.isEmpty()Z", ordinal = 1),
//            locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
//    private void onCaret(PoseStack poseStack, int arg1, int arg2, float arg3, CallbackInfo ci, int l, int m, int n, String string, boolean bl, boolean bl2, int o, int p, int q, boolean bl3, int r) {
//        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(r, p));
//    }
//}

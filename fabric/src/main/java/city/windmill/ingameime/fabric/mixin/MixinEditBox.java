package city.windmill.ingameime.fabric.mixin;

import city.windmill.ingameime.fabric.ScreenEvents;
import kotlin.Pair;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EditBox.class)
abstract class MixinEditBox extends AbstractWidget {
    @Shadow
    private boolean bordered;

    @Shadow
    private boolean isEditable;

    public MixinEditBox(int i, int j, String string) {
        super(i, j, string);
    }

    @Inject(method = {"setFocus", "onFocusedChanged"}, at = @At("HEAD"))
    private void onSelected(boolean selected, CallbackInfo info) {
        int caretX = bordered ? x + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        if (selected && isEditable)
            ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(caretX, caretY));
        else
            ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }

    @Inject(method = "setEditable", at = @At("HEAD"))
    private void onEditableChange(boolean bl, CallbackInfo ci) {
        int caretX = bordered ? x + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        if (!bl) ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
        else if (isFocused())
            ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(caretX, caretY));
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE",
            target = "net/minecraft/util/Mth.floor(D)I",
            shift = At.Shift.BEFORE,
            ordinal = 0))
    private void onFocused(double double_1, double double_2, int int_1, CallbackInfoReturnable<Boolean> cir) {
        int caretX = bordered ? x + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        if (isFocused() && isEditable)
            ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(caretX, caretY));
        else
            ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }

    @Inject(method = "renderButton",
            at = @At(value = "INVOKE", target = "java/lang/String.isEmpty()Z", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret(int arg0, int arg1, float arg2, CallbackInfo ci, int k, int l, int m, String string, boolean bl, boolean bl2, int n, int o, int p, boolean bl3, int q) {
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(p, o));
    }
}

@Mixin(value = TextFieldWidget.class, remap = false)
abstract class MixinTextFieldWidget {
    @Shadow(remap = false)
    private boolean hasBorder;
    @Shadow(remap = false)
    private Rectangle bounds;

    @Inject(method = "setFocused", at = @At("HEAD"), remap = false)
    private void onSelected(boolean selected, CallbackInfo info) {
        int caretX = hasBorder ? bounds.x + 4 : bounds.x;
        int caretY = hasBorder ? bounds.y + (bounds.height - 8) / 2 : bounds.y;
        if (selected)
            ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(caretX, caretY));
        else
            ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }

    @Inject(method = {"render"},
            at = @At(value = "INVOKE", target = "java/lang/String.isEmpty()Z", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret(int arg0, int arg1, float arg2, CallbackInfo ci, int k, int l, int m, String string, boolean bl, boolean bl2, int n, int o, int p, boolean bl3, int q) {
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(q, o));
    }
}

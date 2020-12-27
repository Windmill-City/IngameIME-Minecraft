package city.windmill.ingameime.client.mixin;

import city.windmill.ingameime.client.ScreenEvents;
import kotlin.Pair;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TextFieldWidget.class)
abstract class MixinTextFieldWidget extends AbstractButtonWidget implements Drawable, Element {
    @Shadow
    private boolean focused;

    public MixinTextFieldWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(method = {"setSelected", "onFocusedChanged"}, at = @At("HEAD"))
    public void onSelected(boolean selected, CallbackInfo info) {
        int caretX = focused ? x + 4 : x;
        int caretY = focused ? y + (height - 8) / 2 : y;
        if (selected)
            ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(caretX, caretY));
        else
            ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }

    @Inject(method = "renderButton",
            at = @At(value = "INVOKE", target = "java/lang/String.isEmpty()Z", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onCaret(MatrixStack matrices, int arg1, int arg2, float arg3, CallbackInfo ci, int j, int k, int l, String string, boolean bl, boolean bl2, int m, int n, int o, boolean bl3, int p) {
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(p, n));
    }
}

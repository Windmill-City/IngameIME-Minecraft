package city.windmill.ingameime.client.mixin;

import city.windmill.ingameime.client.ScreenEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import kotlin.Pair;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EditBox.class)
abstract class MixinTextFieldWidget extends AbstractWidget implements Widget, GuiEventListener {
    @Shadow
    private boolean bordered;

    public MixinTextFieldWidget(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(method = {"setFocus", "onFocusedChanged"}, at = @At("HEAD"))
    public void onSelected(boolean selected, CallbackInfo info) {
        int caretX = bordered ? x + 4 : x;
        int caretY = bordered ? y + (height - 8) / 2 : y;
        if (selected)
            ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(caretX, caretY));
        else
            ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }

    @Inject(method = "renderButton",
            at = @At(value = "INVOKE", target = "java/lang/String.isEmpty()Z", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onCaret(PoseStack poseStack, int arg1, int arg2, float arg3, CallbackInfo ci, int j, int k, int l, String string, boolean bl, boolean bl2, int m, int n, int o, boolean bl3, int p) {
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(p, n));
    }
}

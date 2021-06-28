package city.windmill.ingameime.forge.mixin;

import city.windmill.ingameime.forge.IngameIMEClient;
import city.windmill.ingameime.forge.ScreenEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({BookEditScreen.class, SignEditScreen.class})
class MixinEditScreen {
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>(0, 0)));
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemove(CallbackInfo info) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditClose(this));
    }
}

@Mixin(BookEditScreen.class)
abstract class MixinBookEditScreen {
    @Inject(method = "convertLocalToScreen",
            at = @At("TAIL"))
    private void onCaret_Book(BookEditScreen.Pos2i pos2i, CallbackInfoReturnable<BookEditScreen.Pos2i> cir) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>(cir.getReturnValue().x, cir.getReturnValue().y)));
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret_Book(PoseStack poseStack, int i, int j, float f, CallbackInfo ci, int k, FormattedCharSequence formattedCharSequence, int m, int n) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>(
                k + 36 + (114 + n) / 2
                        - Minecraft.getInstance().font.width("_"),
                50
        )));
    }
}

@Mixin(SignEditScreen.class)
abstract class MixinEditSignScreen extends Screen {

    protected MixinEditSignScreen(Component p_i51108_1_) {
        super(p_i51108_1_);
    }

    @Inject(method = "render",
            at = {
                    @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZIIZ)I",
                            ordinal = 1),
                    @At(value = "INVOKE",
                            target = "net/minecraft/client/gui/screens/inventory/SignEditScreen.fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V",
                            ordinal = 0)},
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret_Sign(PoseStack arg, int i, int j, float f, CallbackInfo ci, float g, BlockState lv, boolean bl, boolean bl2, float h, MultiBufferSource.BufferSource lv2, float k, int l, int m, int n, int o, Matrix4f lv5, int p, String string, float q, int r, int s) {
        //s(23)->x,o(17)->y
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>((int) lv5.m03 + s, (int) lv5.m13 + o)));
    }

    @Surrogate
    private void onCaret_Sign(PoseStack arg, int i, int j, float f, CallbackInfo ci, float g, BlockState lv, boolean bl, boolean bl2, float h, MultiBufferSource.BufferSource lv2, float k, int l, int m, int n, int o, Matrix4f lv5, int t, String string2, int u, int v) {
        //v(22)->x,o(17)->y
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>((int) lv5.m03 + v, (int) lv5.m13 + o)));
    }
}

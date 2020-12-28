package city.windmill.ingameime.forge.mixin;

import city.windmill.ingameime.forge.IngameIMEClient;
import city.windmill.ingameime.forge.ScreenEvents;
import com.mojang.blaze3d.matrix.MatrixStack;
import kotlin.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({EditBookScreen.class, EditSignScreen.class})
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

@Mixin(EditBookScreen.class)
abstract class MixinEditBookScreen {
    @Shadow
    protected abstract EditBookScreen.Point convertLocalToScreen(EditBookScreen.Point position);

    @Inject(method = "renderCursor",
            at = @At("HEAD"))
    private void onCaret_Book(MatrixStack matrices, EditBookScreen.Point pos2i, boolean bl, CallbackInfo ci) {
        pos2i = convertLocalToScreen(pos2i);
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>(pos2i.x, pos2i.y)));
    }
}

@Mixin(EditSignScreen.class)
abstract class MixinEditSignScreen extends Screen {

    protected MixinEditSignScreen(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    @Inject(method = "render",
            at = {
                    @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/FontRenderer;drawInBatch(Ljava/lang/String;FFIZLnet/minecraft/util/math/vector/Matrix4f;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ZIIZ)I",
                            ordinal = 1),
                    @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/AbstractGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V",
                            ordinal = 0)},
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret_Sign(MatrixStack matrices, int i, int j, float f, CallbackInfo ci, float g, BlockState blockState, boolean bl, boolean bl2, float h, IRenderTypeBuffer.Impl bufferSource, float k, int l, int m, int n, int o, Matrix4f matrix4f, int t, String string2, int u, int v) {
        //v->x,o->y
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>((int) matrix4f.m03 + v, (int) matrix4f.m13 + o)));
    }
}

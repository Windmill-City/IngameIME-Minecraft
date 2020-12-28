package city.windmill.ingameime.fabric.mixin;

import city.windmill.ingameime.fabric.ScreenEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import kotlin.Pair;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({BookEditScreen.class, SignEditScreen.class})
class MixinEditScreen {
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(0, 0));
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemove(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }
}

@Mixin(BookEditScreen.class)
abstract class MixinBookEditScreen {
    @Shadow
    protected abstract BookEditScreen.Pos2i convertLocalToScreen(BookEditScreen.Pos2i position);

    @Inject(method = "renderCursor",
            at = @At("HEAD"))
    private void onCaret_Book(PoseStack poseStack, BookEditScreen.Pos2i pos2i, boolean bl, CallbackInfo ci) {
        pos2i = convertLocalToScreen(pos2i);
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(pos2i.x, pos2i.y));
    }
}

@Mixin(SignEditScreen.class)
abstract class MixinSignEditScreen extends Screen {
    private MixinSignEditScreen(Component component) {
        super(component);
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
    private void onCaret_Sign(PoseStack poseStack, int i, int j, float f, CallbackInfo ci, float g, BlockState blockState, boolean bl, boolean bl2, float h, MultiBufferSource.BufferSource bufferSource, float k, int l, int m, int n, int o, Matrix4f matrix4f, int t, String string2, int u, int v) {
        //v->x,o->y
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>((int) matrix4f.m03 + v, (int) matrix4f.m13 + o));
    }
}

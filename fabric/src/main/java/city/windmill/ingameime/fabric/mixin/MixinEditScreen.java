package city.windmill.ingameime.fabric.mixin;

import city.windmill.ingameime.fabric.ScreenEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
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
    @Inject(method = "convertLocalToScreen",
            at = @At("TAIL"))
    private void onCaret_Book(BookEditScreen.Pos2i pos2i, CallbackInfo ci) {
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(pos2i.x, pos2i.y));
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;draw(Ljava/lang/String;FFI)I",
                    ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret_Book(int i, int j, float f, CallbackInfo ci, int k, int l, String string, String string2, int m, int n) {
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(
                k + 36 + (114 + n) / 2
                        - Minecraft.getInstance().font.width("_"),
                50
        ));
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
                            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I",
                            ordinal = 1),
                    @At(value = "INVOKE",
                            target = "net/minecraft/client/gui/screens/inventory/SignEditScreen.fill(Lcom/mojang/math/Matrix4f;IIIII)V",
                            ordinal = 0)
            },
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret_Sign(int i, int j, float f, CallbackInfo ci, PoseStack poseStack, float g, BlockState blockState, boolean bl, boolean bl2, float h, MultiBufferSource.BufferSource bufferSource, float k, int l, String[] strings, Matrix4f matrix4f, int n, int o, int p, int q, int v, String string2, int w, int x) {
        //x(25/24)->x,q(20)->y
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>((int) matrix4f.m03 + x, (int) matrix4f.m13 + q));
    }
}

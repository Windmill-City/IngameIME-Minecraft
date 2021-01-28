package city.windmill.ingameime.fabric.mixin;

import city.windmill.ingameime.fabric.ScreenEvents;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.math.Matrix4f;
import kotlin.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;

@Mixin(BookEditScreen.class)
abstract class MixinBookEditScreen {
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(0, 0));
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemove(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }

    @Inject(method = "convertLocalToScreen",
            at = @At("TAIL"))
    private void onCaret_Book(BookEditScreen.Pos2i pos2i, CallbackInfo ci) {
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(pos2i.x, pos2i.y));
    }
}

@Mixin(SignEditScreen.class)
abstract class MixinSignEditScreen {
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_OPEN().invoker()
                .onEditOpen(BlockEntityRenderDispatcher.instance.
                        getRenderer(SignBlockEntity.class), new Pair<>(0, 0));
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemove(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker()
                .onEditClose(BlockEntityRenderDispatcher.instance.
                        getRenderer(SignBlockEntity.class));
    }
}

@Mixin(SignRenderer.class)
abstract class MixinSignRenderer {
    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/world/level/block/entity/SignBlockEntity.isShowCursor()Z",
                    ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret_Sign(SignBlockEntity signBlockEntity, double d, double e, double f, float arg4, int i, CallbackInfo ci, Font font, float j, int k, int l, String string, int m, int n, int o, int p) {
        Matrix4f matrix4f = GlStateManager.getMatrix4f(GL_MODELVIEW_MATRIX);
        //o(19)->x,p(20)->y
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>((int) matrix4f.values[12] + o, (int) matrix4f.values[13] + p));
    }
}

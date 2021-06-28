package city.windmill.ingameime.forge.mixin;

import city.windmill.ingameime.forge.IngameIMEClient;
import city.windmill.ingameime.forge.ScreenEvents;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.math.Matrix4f;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
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
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditOpen(this, new Pair<>(0, 0)));
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemove(CallbackInfo info) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditClose(this));
    }

    @Inject(method = "convertLocalToScreen",
            at = @At("TAIL"))
    private void onCaret_Book(BookEditScreen.Pos2i pos2i, CallbackInfo ci) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>(pos2i.x, pos2i.y)));
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;draw(Ljava/lang/String;FFI)I",
                    ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCaret_Book(int i, int j, float f, CallbackInfo ci, int k, int l, String string, String string2, int m, int n) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>(
                k + 36 + (114 + n) / 2
                        - Minecraft.getInstance().font.width("_"),
                50
        )));
    }
}

@Mixin(SignEditScreen.class)
abstract class MixinSignEditScreen {
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents
                .EditOpen(BlockEntityRenderDispatcher.instance.
                getRenderer(SignBlockEntity.class), new Pair<>(0, 0)));
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void onRemove(CallbackInfo info) {
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents
                .EditClose(BlockEntityRenderDispatcher.instance.
                getRenderer(SignBlockEntity.class)));
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
        IngameIMEClient.INSTANCE.getINGAMEIME_BUS().post(new ScreenEvents.EditCaret(this, new Pair<>((int) matrix4f.values[12] + o, (int) matrix4f.values[13] + p)));
    }
}

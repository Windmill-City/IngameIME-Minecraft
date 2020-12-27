package city.windmill.ingameime.client.mixin;

import city.windmill.ingameime.client.ScreenEvents;
import kotlin.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({BookEditScreen.class, SignEditScreen.class})
public class MixinEditScreen {
    @Inject(method = "init", at = @At("TAIL"))
    public void onInit(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_OPEN().invoker().onEditOpen(this, new Pair<>(0, 0));
    }

    @Inject(method = "removed", at = @At("TAIL"))
    public void onRemove(CallbackInfo info) {
        ScreenEvents.INSTANCE.getEDIT_CLOSE().invoker().onEditClose(this);
    }
}

@Mixin(BookEditScreen.class)
abstract class MixinBookEditScreen {
    @Shadow
    protected abstract BookEditScreen.Position method_27590(BookEditScreen.Position position);

    @Inject(method = "method_27581",
            at = @At("HEAD"))
    public void onCaret_Book(MatrixStack matrixStack, BookEditScreen.Position position, boolean bl, CallbackInfo info) {
        position = method_27590(position);
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>(position.x, position.y));
    }
}

@Mixin(SignEditScreen.class)
abstract class MixinSignEditScreen extends Screen {
    protected MixinSignEditScreen(Text title) {
        super(title);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/font/TextRenderer.draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I",
                    ordinal = 1,
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onCaret_Sign(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, float h, int i, int j, int k, int l, Matrix4f matrix4f, int m, String string, int o, int p) {
        //p->x,l->y
        ScreenEvents.INSTANCE.getEDIT_CARET().invoker().onEditCaret(this, new Pair<>((int) matrix4f.a03 + p, (int) matrix4f.a13 + l));
    }
}

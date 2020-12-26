package city.windmill.ingameime.client.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public class MixinTextFieldWidget {
    @Inject(method = "setSelected", at = @At("HEAD"))
    public void onFocus(boolean selected, CallbackInfo info){
    }
}

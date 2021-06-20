package city.windmill.ingameime.fabric.mixin;

import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KeyboardHandler.class)
public interface MixinKeyboardHandler {
    @Invoker
    void invokeCharTyped(long l, int i, int j);
}

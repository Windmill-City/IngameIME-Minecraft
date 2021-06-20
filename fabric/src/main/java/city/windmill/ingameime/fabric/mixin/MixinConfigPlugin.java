package city.windmill.ingameime.fabric.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    private final Logger logger = Logger.getLogger("IngameIME|MixinConfigPlugin");

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return FabricLoader.getInstance().isModLoaded("optifabric") ?
                Collections.singletonList("MixinKeyboardHandler")
                : Collections.emptyList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (mixinClassName.equals(MixinKeyboardHandler.class.getName())) {
            targetClass.methods.forEach((methodNode) -> {
                if (methodNode.name.equals("charTyped") || methodNode.name.equals("method_1457")) {
                    methodNode.access &= ~Opcodes.ACC_PRIVATE;
                    methodNode.access |= Opcodes.ACC_PUBLIC;
                    logger.info("Patched charTyped");
                }
            });
        }
    }
}

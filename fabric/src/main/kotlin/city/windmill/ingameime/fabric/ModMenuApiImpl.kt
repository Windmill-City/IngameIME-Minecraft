package city.windmill.ingameime.fabric

import io.github.prospector.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screens.Screen
import java.util.function.Function

class ModMenuApiImpl : ModMenuApi {
    override fun getModId(): String {
        return "ingameime"
    }

    override fun getConfigScreenFactory(): Function<Screen, out Screen> {
        return Function { parent -> ConfigHandlerImpl.createConfigScreen().setParentScreen(parent).build() }
    }
}
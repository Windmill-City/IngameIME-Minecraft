package city.windmill.ingameime.fabric

import city.windmill.ingameime.client.ConfigHandler
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

class ModMenuApiImpl : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent -> ConfigHandler.createConfigScreen().setParentScreen(parent).build() }
    }
}
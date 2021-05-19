package city.windmill.ingameime.fabric

import city.windmill.ingameime.client.ConfigHandler
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.resources.language.I18n
import java.util.*

object ConfigHandlerImpl : ConfigHandler() {
    override fun createConfigScreen(): ConfigBuilder {
        return ConfigBuilder.create()
            .setTitle(I18n.get("config.title"))
            .setSavingRunnable { saveConfig() }.apply {
                getOrCreateCategory(I18n.get("config.category.chat")).apply {
                    addEntry(
                        entryBuilder()
                            .startBooleanToggle(
                                I18n.get("desc.disableIMEInCommandMode"),
                                disableIMEInCommandMode
                            )
                            .setDefaultValue(true)
                            .setTooltip(I18n.get("tooltip.disableIMEInCommandMode"))
                            .setSaveConsumer { result -> disableIMEInCommandMode = result }
                            .build()
                    )
                    addEntry(
                        entryBuilder()
                            .startBooleanToggle(
                                I18n.get("desc.autoReplaceSlashChar"),
                                autoReplaceSlashChar
                            )
                            .setDefaultValue(true)
                            .setTooltip(I18n.get("tooltip.autoReplaceSlashChar"))
                            .setSaveConsumer { result -> autoReplaceSlashChar = result }
                            .build()
                    )
                    addEntry(
                        entryBuilder().startStrList(
                            I18n.get("desc.slashChars"),
                            slashCharArray.map { it.toString() }
                        )
                            .setDefaultValue(mutableListOf("、"))
                            .setTooltip(I18n.get("tooltip.slashChars"))
                            .setCellErrorSupplier { str ->
                                if (str.length > 1)
                                    return@setCellErrorSupplier Optional.of(I18n.get("desc.error.slashChars"))
                                return@setCellErrorSupplier Optional.empty()
                            }
                            .setSaveConsumer { result ->
                                slashCharArray = result
                                    .filterNot { it.isBlank() }
                                    .map { it[0] }
                                    .toSet()
                                    .toCharArray()
                            }
                            .build()
                    )
                }
            }
    }
}
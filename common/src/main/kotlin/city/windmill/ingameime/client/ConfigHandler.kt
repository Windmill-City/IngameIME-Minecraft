package city.windmill.ingameime.client

import city.windmill.ingameime.client.jni.ExternalBaseIME
import city.windmill.ingameime.client.jni.ICommitListener
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.ChatScreen
import org.apache.logging.log4j.LogManager
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.outputStream
import kotlin.io.path.reader

abstract class ConfigHandler {
    var disableIMEInCommandMode = false
        set(value) {
            if (field != value)
                if (value) {
                    //Disable -> Enable
                    ScreenHandler.ScreenState.EditState.apply {
                        iEditstateListener = IEditStateListener { state ->
                            if (state == ScreenHandler.ScreenState.EditState.EDIT_OPEN
                                && ScreenHandler.ScreenState.currentScreen is ChatScreen
                                && (ScreenHandler.ScreenState.currentScreen as ChatScreen).initial == "/"
                            ) {
                                //Disable IME in Command Mode
                                IMEHandler.IMEState.onEditState(ScreenHandler.ScreenState.EditState.NULL_EDIT)
                                return@IEditStateListener
                            }
                            IMEHandler.IMEState.onEditState(state)
                        }
                    }
                } else {
                    //Enable -> Disable
                    ScreenHandler.ScreenState.EditState.apply {
                        iEditstateListener = IMEHandler.IMEState
                    }
                }
            field = value
        }
    var autoReplaceSlashChar = false
        set(value) {
            if (field != value)
                if (value) {
                    ExternalBaseIME.iCommitListener = ICommitListener { commit ->
                        var result = commit
                        if (ScreenHandler.ScreenState.currentScreen is ChatScreen
                            && ScreenHandler.ScreenState.EditState.currentEdit is EditBox
                            && (ScreenHandler.ScreenState.EditState.currentEdit as EditBox).cursorPosition == 0
                            && commit.length > 0 && slashCharArray.contains(commit[0])
                        ) {
                            //Change to command mode, replace the char /
                            result = "/${commit.substring(1)}"
                            //Disable IME in command mode
                            if (disableIMEInCommandMode)
                                IMEHandler.IMEState.onEditState(ScreenHandler.ScreenState.EditState.NULL_EDIT)
                        }
                        return@ICommitListener IMEHandler.IMEState.onCommit(result)
                    }
                } else {
                    ExternalBaseIME.iCommitListener = IMEHandler.IMEState
                }
            field = value
        }
    var slashCharArray = charArrayOf('、')

    private val config = Paths.get(
        Minecraft.getInstance().gameDirectory.toString(),
        "config", "ingameime.json"
    )
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|Config")!!

    fun initialConfig() {
        readConfig()
    }

    fun loadDefaultConfig() {
        disableIMEInCommandMode = true
        autoReplaceSlashChar = true
        slashCharArray = charArrayOf('、')
    }

    @OptIn(ExperimentalPathApi::class)
    fun readConfig() {
        try {
            JsonParser().parse(JsonReader(config.reader())).apply {
                disableIMEInCommandMode = (this as JsonObject).get("disableIMEInCommandMode").asBoolean
                autoReplaceSlashChar = this.get("autoReplaceSlashChar").asBoolean
                slashCharArray = this.get("slashChars").asJsonArray.map { it.asCharacter }.toCharArray()
            }
        } catch (e: Exception) {
            LOGGER.warn("Failed to read config:", e)
            LOGGER.warn("Loading Default config")
            loadDefaultConfig()
        }
        saveConfig()
    }

    @OptIn(ExperimentalPathApi::class)
    fun saveConfig() {
        config.outputStream(
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        ).bufferedWriter().apply {
            write(
                GsonBuilder().setPrettyPrinting().create().toJson(
                    JsonObject().apply {
                        addProperty("disableIMEInCommandMode", disableIMEInCommandMode)
                        addProperty("autoReplaceSlashChar", autoReplaceSlashChar)
                        add("slashChars", JsonArray().apply { slashCharArray.onEach(::add) })
                    }
                )
            )
            flush()
            close()
        }
    }

    abstract fun createConfigScreen(): Any
}
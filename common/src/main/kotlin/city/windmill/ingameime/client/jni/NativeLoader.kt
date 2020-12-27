package city.windmill.ingameime.client.jni

import net.minecraft.server.packs.resources.Resource
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object NativeLoader {
    private val LOGGER = LogManager.getFormatterLogger("IngameIME|NativeLoader")!!
    
    /**
     * Loads library from minecraft Resource
     */
    fun load(lib: Resource) {
        LOGGER.trace("Try load native from ${lib.location}")
        val tempFile = Files.createTempFile("IngameIME-Native", null).apply {
            LOGGER.trace("Copying Native to $this")
            Files.copy(lib.inputStream, this, StandardCopyOption.REPLACE_EXISTING)
        }
        System.load(tempFile.toAbsolutePath().toString())
    }
}
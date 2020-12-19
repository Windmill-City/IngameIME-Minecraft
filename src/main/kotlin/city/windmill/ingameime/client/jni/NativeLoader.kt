package city.windmill.ingameime.client.jni

import net.minecraft.resource.Resource
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object NativeLoader {
    /**
     * Loads library from minecraft Resource
     */
    fun load(lib: Resource) {
        val tempFile = Files.createTempFile("IngameIME-Native", null).apply {
            Files.copy(lib.inputStream, this, StandardCopyOption.REPLACE_EXISTING)
        }
        System.load(tempFile.toAbsolutePath().toString())
    }
}
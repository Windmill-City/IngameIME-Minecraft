package city.windmill.ingameime;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Arrays;

public class Config {
    public static Property API_Windows = null;
    public static Property UiLess_Windows = null;
    public static Property TurnOffOnMouseMove = null;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        API_Windows = configuration.get("API",
                "Windows",
                "TextServiceFramework",
                "Config the API to use in Windows platform (TextServiceFramework, Imm32)"
        );
        API_Windows.setValidValues(new String[]{"TextServiceFramework", "Imm32"});
        if (Arrays.stream(API_Windows.getValidValues()).noneMatch(it -> it.equals(API_Windows.getString())))
            API_Windows.set(API_Windows.getDefault());
        API_Windows.setRequiresMcRestart(true);

        UiLess_Windows = configuration.get("UiLess",
                "Windows",
                true,
                "Config if render CandidateList in game");
        UiLess_Windows.setRequiresMcRestart(true);

        TurnOffOnMouseMove = configuration.get("General",
                "TurnOffOnMouseMove",
                true,
                "Turn off InputMethod when mouse move");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}

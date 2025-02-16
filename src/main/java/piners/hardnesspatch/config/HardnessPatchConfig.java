package piners.hardnesspatch.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import java.util.HashMap;
import java.util.Map;

@Config(name = "hardnesspatch-client")
public class HardnessPatchConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public Map<String, Float> customHardnessMap = new HashMap<>();

    // Remove the synchronizeFromServer method
}
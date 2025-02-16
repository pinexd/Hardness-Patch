package piners.hardnesspatch.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import piners.hardnesspatch.HardnessPatch;
import java.util.HashMap;
import java.util.Map;

@Config(name = "hardnesspatch")
public class HardnessPatchConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public Map<String, Float> customHardnessMap = new HashMap<>();

    public static void synchronizeFromServer(Map<String, Float> serverConfig) {
        HardnessPatch.customHardnessMap.clear();
        serverConfig.forEach((key, value) -> {
            Block block = Registries.BLOCK.get(Identifier.tryParse(key));
            if (block != Blocks.AIR) {
                HardnessPatch.customHardnessMap.put(block, value);
            }
        });
    }
}
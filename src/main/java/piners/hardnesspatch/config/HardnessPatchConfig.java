package piners.hardnesspatch.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public static void synchronizeConfig() {
        HardnessPatch.customHardnessMap.clear();
        getConfig().customHardnessMap.forEach((key, value) -> {
            Block block = Registries.BLOCK.get(Identifier.tryParse(key));
            if (block != null && block != Blocks.AIR) {
                HardnessPatch.customHardnessMap.put(block, value);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void initialize() {
        AutoConfig.register(HardnessPatchConfig.class, JanksonConfigSerializer::new);
    }

    public static HardnessPatchConfig getConfig() {
        return AutoConfig.getConfigHolder(HardnessPatchConfig.class).getConfig();
    }

    public static void saveConfig() {
        AutoConfig.getConfigHolder(HardnessPatchConfig.class).save();
    }
}
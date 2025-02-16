package piners.hardnesspatch.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import piners.hardnesspatch.HardnessPatch;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILENAME = "hardnesspatch.json";

    public static void loadConfig() {
        File file = getConfigFile();
        if (!file.exists()) {
            saveConfig(new HardnessPatchConfig());
        } else {
            try (FileReader reader = new FileReader(file)) {
                HardnessPatchConfig config = GSON.fromJson(reader, HardnessPatchConfig.class);
                HardnessPatch.customHardnessMap.clear();
                for (Map.Entry<String, Float> entry : config.customHardnessMap.entrySet()) {
                    HardnessPatch.customHardnessMap.put(Registries.BLOCK.get(Identifier.tryParse(entry.getKey())), entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig(HardnessPatchConfig config) {
        File file = getConfigFile();
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getConfigFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), FILENAME);
    }
}
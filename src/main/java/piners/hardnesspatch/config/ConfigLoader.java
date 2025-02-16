package piners.hardnesspatch.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import piners.hardnesspatch.HardnessPatch;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfigLoader {
    private static final Logger LOGGER = LogManager.getLogger("HardnessPatch");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILENAME = "hardnesspatch-server.json";
    private static ScheduledExecutorService executor;

    public static void loadConfig() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
            return;
        }

        File file = getConfigFile();
        HardnessPatchConfig config = new HardnessPatchConfig();

        try {
            if (!file.exists()) {
                LOGGER.info("Creating default server config file");
                config.customHardnessMap.put("minecraft:stone", 1.5f);
                saveConfig(config);
            } else {
                try (FileReader reader = new FileReader(file)) {
                    config = GSON.fromJson(reader, HardnessPatchConfig.class);
                    LOGGER.info("Loaded server config with {} entries", config.customHardnessMap.size());
                }
            }

            HardnessPatch.customHardnessMap.clear();
            for (Map.Entry<String, Float> entry : config.customHardnessMap.entrySet()) {
                Identifier id = Identifier.tryParse(entry.getKey());
                if (id != null && Registries.BLOCK.containsId(id)) {
                    HardnessPatch.customHardnessMap.put(Registries.BLOCK.get(id), entry.getValue());
                } else {
                    LOGGER.warn("Invalid block ID in server config: {}", entry.getKey());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load server config", e);
        }
    }

    public static void saveConfig(HardnessPatchConfig config) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
            return;
        }

        try (FileWriter writer = new FileWriter(getConfigFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save server config", e);
        }
    }

    private static File getConfigFile() {
        Path configPath = FabricLoader.getInstance().getGameDir();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            configPath = configPath.resolve("config");
        } else {
            configPath = configPath.resolve("saves").resolve("Server Configs");
        }
        return configPath.resolve(FILENAME).toFile();
    }

    public static void startFileWatcher() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
            return;
        }

        File configFile = getConfigFile();
        long[] lastModified = {configFile.lastModified()};

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                if (configFile.lastModified() > lastModified[0]) {
                    LOGGER.info("Server config changed, reloading...");
                    loadConfig();
                    lastModified[0] = configFile.lastModified();
                    HardnessPatch.broadcastConfigUpdate();
                }
            } catch (Exception e) {
                LOGGER.error("Config watcher error", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static class HardnessPatchConfig {
        public Map<String, Float> customHardnessMap = new HashMap<>();
    }
}
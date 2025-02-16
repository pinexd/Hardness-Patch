package piners.hardnesspatch;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import piners.hardnesspatch.config.HardnessPatchConfig;
import piners.hardnesspatch.network.NetworkHandler;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class HardnessPatchClient implements ClientModInitializer {
    private static final Map<String, Float> serverConfig = new HashMap<>();
    private static boolean usingServerConfig = false;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(HardnessPatchConfig.class, me.shedaniel.autoconfig.serializer.JanksonConfigSerializer::new);
        NetworkHandler.Client.initialize();
    }

    public static void setServerConfig(Map<String, Float> config) {
        serverConfig.clear();
        serverConfig.putAll(config);
        usingServerConfig = true;
    }

    public static void resetToClientConfig() {
        usingServerConfig = false;
        serverConfig.clear();
    }

    public static float getAdjustedHardness(Block block) {
        if (usingServerConfig && MinecraftClient.getInstance().world != null) {
            Identifier blockId = Registries.BLOCK.getId(block);
            return serverConfig.getOrDefault(blockId.toString(), block.getHardness());
        }
        return AutoConfig.getConfigHolder(HardnessPatchConfig.class).getConfig()
                .customHardnessMap.getOrDefault(blockIdToString(block), block.getHardness());
    }

    private static String blockIdToString(Block block) {
        return Registries.BLOCK.getId(block).toString();
    }
}
package piners.hardnesspatch;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import piners.hardnesspatch.config.ConfigLoader;
import piners.hardnesspatch.network.NetworkHandler;

import java.util.HashMap;
import java.util.Map;

public class HardnessPatch implements ModInitializer {
    public static final Map<Block, Float> customHardnessMap = new HashMap<>();
    public static void broadcastConfigUpdate() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            Map<String, Float> config = getServerConfig();
            // Correct method for Fabric Loader 0.16.10
            MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
            server.getPlayerManager().getPlayerList().forEach(player ->
                    NetworkHandler.sendConfigToPlayer(config, player)
            );
        }
    }

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            ConfigLoader.loadConfig();
            ConfigLoader.startFileWatcher();
        }

        PayloadTypeRegistry.playS2C().register(
                NetworkHandler.ID,
                NetworkHandler.CODEC
        );

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                    NetworkHandler.sendConfigToPlayer(getServerConfig(), handler.player)
            );
        }
    }

    private static Map<String, Float> getServerConfig() {
        Map<String, Float> config = new HashMap<>();
        customHardnessMap.forEach((block, value) ->
                config.put(Registries.BLOCK.getId(block).toString(), value)
        );
        return config;
    }
}
package piners.hardnesspatch;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import piners.hardnesspatch.config.HardnessPatchConfig;
import piners.hardnesspatch.network.NetworkHandler;

public class HardnessPatchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register config and network
        AutoConfig.register(HardnessPatchConfig.class, JanksonConfigSerializer::new);
        NetworkHandler.Client.initialize();
    }
}
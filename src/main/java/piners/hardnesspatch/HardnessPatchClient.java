package piners.hardnesspatch;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import piners.hardnesspatch.config.HardnessPatchConfig;

@Environment(EnvType.CLIENT)
public class HardnessPatchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Client-side initialization if needed
    }
}
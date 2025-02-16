package piners.hardnesspatch;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.Block;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import piners.hardnesspatch.config.HardnessPatchConfig;

import java.util.HashMap;
import java.util.Map;

public class HardnessPatch implements ModInitializer {
    public static final String MOD_ID = "hardnesspatch";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Map<Block, Float> customHardnessMap = new HashMap<>();

    @Override
    public void onInitialize() {
        HardnessPatchConfig.initialize();
        HardnessPatchConfig.synchronizeConfig();

        ServerWorldEvents.LOAD.register((server, world) -> {
            HardnessPatchConfig.synchronizeConfig();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("reloadHardnessConfig")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        HardnessPatchConfig.synchronizeConfig();
                        context.getSource().sendFeedback(() ->
                                Text.literal("Hardness config reloaded"), false);
                        return Command.SINGLE_SUCCESS;
                    }));
        });
    }
}
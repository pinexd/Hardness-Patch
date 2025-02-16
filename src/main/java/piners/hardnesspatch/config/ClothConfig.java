package piners.hardnesspatch.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.*;

public class ClothConfig {
    public final Screen screen;

    public ClothConfig(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.hardnesspatch.title"))
                .setSavingRunnable(() -> {
                    AutoConfig.getConfigHolder(HardnessPatchConfig.class).save();
                    HardnessPatchConfig.synchronizeFromServer(AutoConfig.getConfigHolder(HardnessPatchConfig.class).getConfig().customHardnessMap);
                });

        ConfigEntryBuilder entry = builder.entryBuilder();
        HardnessPatchConfig config = AutoConfig.getConfigHolder(HardnessPatchConfig.class).getConfig();

        ConfigCategory category = builder.getOrCreateCategory(Text.translatable("config.hardnesspatch.category.main"));

        // Add informational messages
        category.addEntry(entry.startTextDescription(
                Text.translatable("config.hardnesspatch.messageimmediate")
                        .formatted(Formatting.ITALIC)
                        .formatted(Formatting.GREEN)
        ).build());

        category.addEntry(entry.startTextDescription(
                Text.translatable("config.hardnesspatch.messageclient")
                        .formatted(Formatting.ITALIC)
                        .formatted(Formatting.RED)
        ).build());

        // Block ID list with direct removal handling
        category.addEntry(entry.startStrList(
                        Text.translatable("config.hardnesspatch.entries"),
                        new ArrayList<>(config.customHardnessMap.keySet())
                )
                .setAddButtonTooltip(Text.translatable("config.hardnesspatch.add_entry"))
                .setRemoveButtonTooltip(Text.translatable("config.hardnesspatch.broken"))
                .setDefaultValue(List.of("minecraft:stone"))
                .setSaveConsumer(newList -> {
                    Map<String, Float> newMap = new HashMap<>();
                    for (String key : newList) {
                        newMap.put(key, config.customHardnessMap.getOrDefault(key, 1.5f));
                    }
                    config.customHardnessMap.clear();
                    config.customHardnessMap.putAll(newMap);
                })
                .build());

        // Hardness values with proper dropdowns
        config.customHardnessMap.forEach((blockId, hardness) -> {
            category.addEntry(entry.startSubCategory(
                            Text.literal(blockId),
                            createHardnessEntry(blockId, hardness, config, entry))
                    .setExpanded(false)
                    .build());
        });

        this.screen = builder.build();
    }

    private List<AbstractConfigListEntry> createHardnessEntry(String blockId, float hardness,
                                                              HardnessPatchConfig config,
                                                              ConfigEntryBuilder entry) {
        List<AbstractConfigListEntry> entryElements = new ArrayList<>();

        // Hardness field
        entryElements.add(entry.startFloatField(
                        Text.translatable("config.hardnesspatch.hardness"),
                        hardness)
                .setDefaultValue(1.5f)
                .setSaveConsumer(newValue -> config.customHardnessMap.put(blockId, newValue))
                .build());

        // Remove button
        entryElements.add(entry.startBooleanToggle(
                        Text.translatable("config.hardnesspatch.remove_entry"), false)
                .setSaveConsumer(toggled -> {
                    if (toggled) {
                        config.customHardnessMap.remove(blockId);
                    }
                })
                .setTooltip(Text.translatable("config.hardnesspatch.remove_tooltip"))
                .build());

        return entryElements;
    }

    public Screen getScreen() {
        return screen;
    }
}
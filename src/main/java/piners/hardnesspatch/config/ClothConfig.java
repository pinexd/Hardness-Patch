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
                .setSavingRunnable(() ->
                        AutoConfig.getConfigHolder(HardnessPatchConfig.class).save()
                );

        ConfigEntryBuilder entry = builder.entryBuilder();
        HardnessPatchConfig config = AutoConfig.getConfigHolder(HardnessPatchConfig.class).getConfig();

        ConfigCategory category = builder.getOrCreateCategory(Text.translatable("config.hardnesspatch.category.main"));
        category.addEntry(entry.startTextDescription(
                Text.translatable("config.hardnesspatch.messageimmediate")
                        .formatted(Formatting.ITALIC, Formatting.GREEN)
        ).build());

        // Informational message
        category.addEntry(entry.startTextDescription(
                Text.translatable("config.hardnesspatch.messageclient")
                        .formatted(Formatting.ITALIC, Formatting.GOLD)
        ).build());

        // Block list entry
        category.addEntry(entry.startStrList(
                        Text.translatable("config.hardnesspatch.entries"),
                        new ArrayList<>(config.customHardnessMap.keySet())
                )
                .setAddButtonTooltip(Text.translatable("config.hardnesspatch.add_entry"))
                .setRemoveButtonTooltip(Text.translatable("config.hardnesspatch.broken"))
                .setDefaultValue(List.of("minecraft:stone"))
                .setSaveConsumer(newList -> updateConfigMap(config, newList))
                .build());

        // Add hardness entries
        config.customHardnessMap.forEach((blockId, hardness) ->
                category.addEntry(createBlockEntry(blockId, hardness, config, entry))
        );

        this.screen = builder.build();
    }

    private void updateConfigMap(HardnessPatchConfig config, List<String> newList) {
        Map<String, Float> newMap = new HashMap<>();
        for (String key : newList) {
            newMap.put(key, config.customHardnessMap.getOrDefault(key, 1.5f));
        }
        config.customHardnessMap.clear();
        config.customHardnessMap.putAll(newMap);
    }

    private AbstractConfigListEntry<?> createBlockEntry(String blockId, float hardness,
                                                        HardnessPatchConfig config,
                                                        ConfigEntryBuilder entry) {
        return entry.startSubCategory(
                Text.literal(blockId),
                Arrays.asList(
                        entry.startFloatField(Text.translatable("config.hardnesspatch.hardness"), hardness)
                                .setDefaultValue(1.5f)
                                .setSaveConsumer(newValue -> config.customHardnessMap.put(blockId, newValue))
                                .build(),
                        entry.startBooleanToggle(Text.translatable("config.hardnesspatch.remove_entry"), false)
                                .setSaveConsumer(toggled -> { if (toggled) config.customHardnessMap.remove(blockId); })
                                .setTooltip(Text.translatable("config.hardnesspatch.remove_tooltip"))
                                .build()
                )
        ).setExpanded(false).build();
    }

    public Screen getScreen() {
        return screen;
    }
}
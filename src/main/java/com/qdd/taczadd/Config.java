package com.qdd.taczadd;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Taczadd.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

//    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
//            .comment("Whether to log the dirt block on common setup")
//            .define("logDirtBlock", true);
//
//    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
//            .comment("A magic number")
//            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
//
    public static final ForgeConfigSpec.ConfigValue<String> PASSWORD = BUILDER
            .comment("服务器启动校验密码（不填或不匹配将关闭服务器）")
            .define("password", "");

    public static final ForgeConfigSpec.BooleanValue SKILL_KNOCKBACK_ENABLED = BUILDER
            .comment("技能击退/击飞开关（关闭后：冰龙等技能不会把怪打飞）")
            .define("skillKnockbackEnabled", false);

    public static final ForgeConfigSpec.BooleanValue SKILL_CAN_HURT_PLAYERS = BUILDER
            .comment("技能是否可以对玩家造成伤害（关闭后：技能仅对怪物生效）")
            .define("skillCanHurtPlayers", true);
//
//    // a list of strings that are treated as resource locations for items
//    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
//            .comment("A list of items to log on common setup.")
//            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    public static final String CONFIG_FILE_NAME = "taczadd-common.toml";
    public static final ForgeConfigSpec SPEC = BUILDER.build();

//    public static boolean logDirtBlock;
//    public static int magicNumber;
    public static String password;
    public static boolean skillKnockbackEnabled;
    public static boolean skillCanHurtPlayers;
//    public static Set<Item> items;

//    private static boolean validateItemName(final Object obj)
//    {
//        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
//    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event)
    {
        if (event.getConfig() == null || event.getConfig().getSpec() != SPEC) {
            return;
        }
//        logDirtBlock = LOG_DIRT_BLOCK.get();
//        magicNumber = MAGIC_NUMBER.get();
        bake();
//
//        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream()
//                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
//                .collect(Collectors.toSet());
    }

    public static void bake() {
        password = PASSWORD.get();
        skillKnockbackEnabled = SKILL_KNOCKBACK_ENABLED.get();
        skillCanHurtPlayers = SKILL_CAN_HURT_PLAYERS.get();
    }

    public static void reloadFromDisk(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());
            if (!Files.exists(configFile)) {
                Files.createFile(configFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile)
                .sync()
                .autosave()
                .build();
        fileConfig.load();
        SPEC.setConfig(fileConfig);
        bake();
        fileConfig.close();
    }
}

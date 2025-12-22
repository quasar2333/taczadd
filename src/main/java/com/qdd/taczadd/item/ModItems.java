package com.qdd.taczadd.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import com.qdd.taczadd.Taczadd;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Taczadd.MODID);

    public static final RegistryObject<Item> Tyrants=ITEMS.register("tyrants_gem",
            () -> new TyrantsGam(new Item.Properties().stacksTo(1),80,180));

    public static final RegistryObject<Item> Tyrants1=ITEMS.register("tyrants_gem_1",
            () -> new TyrantsGam(new Item.Properties().stacksTo(1),30,70));

    public static final RegistryObject<Item> Tyrants2=ITEMS.register("tyrants_gem_2",
            () -> new TyrantsGam(new Item.Properties().stacksTo(1),50,120));

    public static final RegistryObject<Item> GOFWP=ITEMS.register("godofwarprism_gem",
            () -> new GodOfWarPrismGam(new Item.Properties().stacksTo(1),160,300));

    public static final RegistryObject<Item> GOFWP1=ITEMS.register("godofwarprism_gem_1",
            () -> new GodOfWarPrismGam(new Item.Properties().stacksTo(1),50,100));

    public static final RegistryObject<Item> GOFWP2=ITEMS.register("godofwarprism_gem_2",
            () -> new GodOfWarPrismGam(new Item.Properties().stacksTo(1),120,200));

    public static final RegistryObject<Item> Collapse=ITEMS.register("collapse_gem",
            () -> new CollapseGam(new Item.Properties().stacksTo(1),250,400));

    public static final RegistryObject<Item> Collapse1=ITEMS.register("collapse_gem_1",
            () -> new CollapseGam(new Item.Properties().stacksTo(1),100,180));

    public static final RegistryObject<Item> Collapse2=ITEMS.register("collapse_gem_2",
            () -> new CollapseGam(new Item.Properties().stacksTo(1),180,250));

    public static final RegistryObject<Item> FuriousFragments=ITEMS.register("furiousfragments_gem",
            () -> new FuriousFragmentsGam(new Item.Properties().stacksTo(1),170,300));

    public static final RegistryObject<Item> FuriousFragments1=ITEMS.register("furiousfragments_gem_1",
            () -> new FuriousFragmentsGam(new Item.Properties().stacksTo(1),80,150));

    public static final RegistryObject<Item> FuriousFragments2=ITEMS.register("furiousfragments_gem_2",
            () -> new FuriousFragmentsGam(new Item.Properties().stacksTo(1),120,230));

    public static final RegistryObject<Item> PrecisionCore=ITEMS.register("precisioncore_gem",
            () -> new PrecisionCoreGam(new Item.Properties().stacksTo(1),120,200));

    public static final RegistryObject<Item> PrecisionCore1=ITEMS.register("precisioncore_gem_1",
            () -> new PrecisionCoreGam(new Item.Properties().stacksTo(1),20,80));

    public static final RegistryObject<Item> PrecisionCore2=ITEMS.register("precisioncore_gem_2",
            () -> new PrecisionCoreGam(new Item.Properties().stacksTo(1),70,150));

    public static final RegistryObject<Item> BattlefieldPhysician=ITEMS.register("battlefield_physician_gem",
            () -> new BattlefieldPhysicianGam(new Item.Properties().stacksTo(1)));

//    public static final RegistryObject<Item> BloodContractMagazine=ITEMS.register("blood_contract_magazine_gem",
//            () -> new BloodContractMagazineGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FieldCommander=ITEMS.register("field_commander_gem",
            () -> new FieldCommanderGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> AccurateShooter=ITEMS.register("accurate_shooter_gem",
            () -> new AccurateShooterGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CriticalHit=ITEMS.register("critical_hit_gem",
            () -> new CriticalHitGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> InfiniteFirepower=ITEMS.register("infinite_firepower_gem",
            () -> new InfiniteFirepowerGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FirepowerSuppression=ITEMS.register("firepower_suppression_gem",
            () -> new FirepowerSuppressionGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GlassCannon=ITEMS.register("glass_cannon_gem",
            () -> new GlassCannonGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RoyalGift=ITEMS.register("royal_gift_gem",
            () -> new RoyalGiftGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GloriousEmblem=ITEMS.register("glorious_emblem_gem",
            () -> new GloriousEmblemGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HeavyWarrior=ITEMS.register("heavy_warrior_gem",
            () -> new HeavyWarriorGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ThornCurse=ITEMS.register("thorn_curse_gem",
            () -> new ThornCurseGam(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> LowGun=ITEMS.register("low_gun_crystal",
            () -> new ReinforcedCrystal(new Item.Properties(), ReinforcedCrystal.Type.Gun, ReinforcedCrystal.Rank.Low));

    public static final RegistryObject<Item> MidGun=ITEMS.register("mid_gun_crystal",
            () -> new ReinforcedCrystal(new Item.Properties(), ReinforcedCrystal.Type.Gun, ReinforcedCrystal.Rank.Mid));

    public static final RegistryObject<Item> LowArmor=ITEMS.register("low_armor_crystal",
            ()-> new ReinforcedCrystal(new Item.Properties(), ReinforcedCrystal.Type.Armor, ReinforcedCrystal.Rank.Low));

    public static final RegistryObject<Item> MidArmor=ITEMS.register("mid_armor_crystal",
            () -> new ReinforcedCrystal(new Item.Properties(), ReinforcedCrystal.Type.Armor, ReinforcedCrystal.Rank.Mid));

    public static final RegistryObject<Item> BreakGam=ITEMS.register("break_gem",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> IdentifyGam=ITEMS.register("identify_gem",
            () -> new Item(new Item.Properties()));

    // 通用枪械进阶模块
    public static final RegistryObject<Item> GeneralUpgradeModule = ITEMS.register("general_upgrade_module",
            () -> new Item(new Item.Properties()));

    // 通用装备进阶模块（占位物品，无实际功能）
    public static final RegistryObject<Item> GeneralEquipmentUpgradeModule = ITEMS.register("general_equipment_upgrade_module",
            () -> new Item(new Item.Properties()));

    // 枪械进阶核心物品
    public static final RegistryObject<Item> AugPirateCore=ITEMS.register("aug_pirate_core",
            () -> new com.qdd.taczadd.item.upgrade.AugPirateCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> M4EnderDragonCore=ITEMS.register("m4_enderdragon_core",
            () -> new com.qdd.taczadd.item.upgrade.M4EnderDragonCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> Ak117DeepSeaCore=ITEMS.register("ak117_deepsea_core",
            () -> new com.qdd.taczadd.item.upgrade.Ak117DeepSeaCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> Ak117VenomCore=ITEMS.register("ak117_venom_core",
            () -> new com.qdd.taczadd.item.upgrade.Ak117VenomCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> M4DarkPivotCore=ITEMS.register("m4_darkpivot_core",
            () -> new com.qdd.taczadd.item.upgrade.M4DarkPivotCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BarrettCoordinateCore=ITEMS.register("barrett_coordinate_core",
            () -> new com.qdd.taczadd.item.upgrade.BarrettCoordinateCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> M4ZmzyCore=ITEMS.register("m4_zmzy_core",
            () -> new com.qdd.taczadd.item.upgrade.M4ZmzyCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> Sg914K30Core=ITEMS.register("sg914_k30_core",
            () -> new com.qdd.taczadd.item.upgrade.Sg914K30Core(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GWI=ITEMS.register("greatsworditem",
            () -> new GWItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(0)));

    public static final RegistryObject<Item> ArmorInducer=ITEMS.register("armor_inducer",
            () -> new ArmorInducer(new Item.Properties()));
}

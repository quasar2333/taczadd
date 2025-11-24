package com.qdd.taczadd.gui;

import net.minecraft.world.inventory.MenuType;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.gui.menu.GamSettingMenu;
import com.qdd.taczadd.gui.menu.ReinforcedMenu;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuType {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Taczadd.MODID);

    public static final RegistryObject<MenuType<GamSettingMenu>> GAM_SETTING_MENU =
            MENUS.register("gem_polishing_menu",()-> IForgeMenuType.create( GamSettingMenu::new));

    public static final RegistryObject<MenuType<ReinforcedMenu>> Reinforced_MENU =
            MENUS.register("reinforced_menu",()-> IForgeMenuType.create( ReinforcedMenu::new));

    public static final RegistryObject<MenuType<com.qdd.taczadd.gui.menu.UpgradeMenu>> UPGRADE_MENU =
            MENUS.register("upgrade_menu",()-> IForgeMenuType.create( com.qdd.taczadd.gui.menu.UpgradeMenu::new));

    public static final RegistryObject<MenuType<com.qdd.taczadd.gui.menu.ActivationMenu>> ACTIVATION_MENU =
            MENUS.register("activation_menu",()-> IForgeMenuType.create( com.qdd.taczadd.gui.menu.ActivationMenu::new));

    public static final RegistryObject<MenuType<com.qdd.taczadd.gui.menu.InducerMenu>> INDUCER_MENU =
            MENUS.register("inducer_menu",()-> IForgeMenuType.create( com.qdd.taczadd.gui.menu.InducerMenu::new));
}

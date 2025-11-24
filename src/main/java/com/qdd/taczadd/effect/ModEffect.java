package com.qdd.taczadd.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import com.qdd.taczadd.Taczadd;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffect {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Taczadd.MODID);

    public static final RegistryObject<MobEffect> AccurateShooterE = EFFECTS.register("accurate_shooter",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000));

    public static final RegistryObject<MobEffect> CriticalHitE = EFFECTS.register("critical_hit",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000));

    public static final RegistryObject<MobEffect> InfiniteFirepowerE = EFFECTS.register("infinite_firepower",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000));
}

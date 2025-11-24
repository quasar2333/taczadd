package com.qdd.taczadd.item.Attributes;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import com.qdd.taczadd.Taczadd;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Taczadd.MODID);

    public static final RegistryObject<Attribute> CTA = ATTRIBUTES.register("cta",
            () ->  (new RangedAttribute("attribute.name.taczadd.cta", 1D, 0.0D, 2.0D)).setSyncable(true));

    public static final RegistryObject<Attribute> armorIgnore = ATTRIBUTES.register("armorignore",
            () ->  (new RangedAttribute("attribute.name.taczadd.armorignore", 0D, 0.0D, 1.0D)).setSyncable(true));

    public static final RegistryObject<Attribute> GunDamage = ATTRIBUTES.register("gundamage",
            () ->  (new RangedAttribute("attribute.name.taczadd.gundamage", 1D, 0.0D, 2.0D)).setSyncable(true));

    public static final RegistryObject<Attribute> DamageReduction = ATTRIBUTES.register("damagereduction",
            () ->  (new RangedAttribute("attribute.name.taczadd.damagereduction", 0D, 0D, 100D)).setSyncable(true));
}

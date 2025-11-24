package com.qdd.taczadd.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import com.qdd.taczadd.Taczadd;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            Taczadd.MODID);
    public static void register(IEventBus modEventBus){
        ENTITIES.register(modEventBus);
    }
    public static final RegistryObject<EntityType<IceCraterSmallEntity>> ICS_Entity = registerEntity("ice_crater_small", IceCraterSmallEntity::new,
            4f, 2f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<EndDragonGun>> ED_Entity = registerEntity("enddragon", EndDragonGun::new,
            3.5f, 0.5f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<Zmzy30Entity>> Zmzy30_Entity = registerEntity("zmzy30", Zmzy30Entity::new,
            1f, 1f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<Zmzy5Entity>> Zmzy5_Entity = registerEntity("zmzy5", Zmzy5Entity::new,
            1f, 1f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<JxdfaEntity>> Jxdfa_Entity = registerEntity("jxdfa", JxdfaEntity::new,
            5f, 5f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<JxdfbEntity>> Jxdfb_Entity = registerEntity("jxdfb", JxdfbEntity::new,
            1f, 1f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<BigDragonEntity>> BD_Entity = registerEntity("bigdragon", BigDragonEntity::new,
            5f, 5f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<WaterWaveEntity>> WW_Entity = registerEntity("waterwave", WaterWaveEntity::new,
            1f, 1f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<WatrtWavecEntity>> WWC_Entity = registerEntity("waterwavec", WatrtWavecEntity::new,
            4f, 2f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<GreatswordEntity>> GW_Entity = registerEntity("greatsword", GreatswordEntity::new,
            5f, 5f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<SGSwordEntity>> SGS_Entity = registerEntity("sg_sword", SGSwordEntity::new,
            3f, 5f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<K30WaveEntity>> K30W_Entity = registerEntity("k30_wave", K30WaveEntity::new,
            1f, 1f, 0xDD0000, 0xD8FFF7);
    public static final RegistryObject<EntityType<PozhanzheSkillEntity>> PozhanzheSkill_Entity = registerEntity("pozhanzhe_skill", PozhanzheSkillEntity::new,
            2f, 3f, 0xDD0000, 0xD8FFF7);



    public static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> entity, float width, float height, int primaryEggColor, int secondaryEggColor) {
        return ENTITIES.register(name,
                () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
    }

}

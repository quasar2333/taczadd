package com.qdd.taczadd.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import com.qdd.taczadd.Taczadd;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Taczadd.MODID);

    // existing sounds
    public static final RegistryObject<SoundEvent> SKILL1 = SOUND_EVENTS.register(
            "lx",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "lx"))
    );
    public static final RegistryObject<SoundEvent> SKILL2 = SOUND_EVENTS.register(
            "sl",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "sl"))
    );
    public static final RegistryObject<SoundEvent> SKILL3 = SOUND_EVENTS.register(
            "jian",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "jian"))
    );

    // reinforcement sounds
    public static final RegistryObject<SoundEvent> REINFORCE_SUCCESS = SOUND_EVENTS.register(
            "reinforce_success",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "reinforce_success"))
    );
    public static final RegistryObject<SoundEvent> REINFORCE_FAIL = SOUND_EVENTS.register(
            "reinforce_fail",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "reinforce_fail"))
    );

    // skill sounds
    public static final RegistryObject<SoundEvent> SG914_SKILL = SOUND_EVENTS.register(
            "sg914_skill",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "sg914_skill"))
    );
    public static final RegistryObject<SoundEvent> POZHANZHE_SKILL = SOUND_EVENTS.register(
            "pozhanzhe_skill",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "pozhanzhe_skill"))
    );

    // inducer sounds
    public static final RegistryObject<SoundEvent> INDUCER_SUCCESS = SOUND_EVENTS.register(
            "inducer_success",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "inducer_success"))
    );
    public static final RegistryObject<SoundEvent> INDUCER_FAIL = SOUND_EVENTS.register(
            "inducer_fail",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Taczadd.MODID, "inducer_fail"))
    );
}

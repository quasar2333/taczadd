package com.qdd.taczadd.event;

import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.client.gui.overlay.DamageScoreBoard;
import com.qdd.taczadd.client.gui.overlay.SkillOverlay;
import com.qdd.taczadd.client.render.*;
import com.qdd.taczadd.entity.ModEntities;
import com.qdd.taczadd.handler.KeyBindingHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import com.qdd.taczadd.item.Attributes.ModAttributes;

public class ModEvent {
    @Mod.EventBusSubscriber(modid = Taczadd.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvent{
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.ICS_Entity.get(), ICSRender::new);
            event.registerEntityRenderer(ModEntities.ED_Entity.get(), EDRender::new);
            event.registerEntityRenderer(ModEntities.Zmzy30_Entity.get(), Zmzy30Render::new);
            event.registerEntityRenderer(ModEntities.Zmzy5_Entity.get(), Zmzy5Render::new);
            event.registerEntityRenderer(ModEntities.Jxdfa_Entity.get(), JxdfaRender::new);
            event.registerEntityRenderer(ModEntities.Jxdfb_Entity.get(), JxdfbRender::new);
            event.registerEntityRenderer(ModEntities.BD_Entity.get(),BDRender::new);
            event.registerEntityRenderer(ModEntities.WW_Entity.get(), WWRender::new);
            event.registerEntityRenderer(ModEntities.WWC_Entity.get(), WWCRender::new);
            event.registerEntityRenderer(ModEntities.GW_Entity.get(), GWRender::new);
            event.registerEntityRenderer(ModEntities.SGS_Entity.get(), SGSwordRender::new);
            event.registerEntityRenderer(ModEntities.K30W_Entity.get(), K30WaveRender::new);
            event.registerEntityRenderer(ModEntities.PozhanzheSkill_Entity.get(), PozhanzheSkillRender::new);

        }
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
            event.registerBelowAll("skill", new SkillOverlay());
            event.registerBelowAll("damage_score_board", new DamageScoreBoard());
        }
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(KeyBindingHandler.GamSetting);
//            event.register(KeyBindingHandler.count40);
//            event.register(KeyBindingHandler.count60);
        }

    }

    @Mod.EventBusSubscriber(modid = Taczadd.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModCommonEvent {
        @SubscribeEvent
        public static void onAddPlayerAttributes(final EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, ModAttributes.GunDamage.get());
            event.add(EntityType.PLAYER, ModAttributes.CTA.get());
            event.add(EntityType.PLAYER, ModAttributes.armorIgnore.get());
            event.add(EntityType.PLAYER, ModAttributes.DamageReduction.get());
        }
    }
}

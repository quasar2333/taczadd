package com.qdd.taczadd.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.cap.GamCap;
import com.qdd.taczadd.cap.PlayerCapProvider;
import com.qdd.taczadd.command.DPSCommand;
import com.qdd.taczadd.command.TaczaddCommand;
import com.qdd.taczadd.effect.ModEffect;
import com.qdd.taczadd.handler.*;
import com.qdd.taczadd.item.Attributes.ModAttributes;
import com.qdd.taczadd.item.GamItem;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"removal"})
@Mod.EventBusSubscriber
public class GameEvent {
    private static final UUID HealthModifierUUID = UUID.fromString("f7f7f7f7-f7f7-f7f7-f7f7-f7f7f7f7f7f7");

    @SubscribeEvent
    public static void onShootEven(GunShootEvent event){
        ItemStack itemStack=event.getGunItemStack();
        if (itemStack.getItem() instanceof AbstractGunItem gunItem) {
            // 增强鲁棒性：确保 GemEffects 存在
            CompoundTag gemEffects = itemStack.getOrCreateTag().getCompound("GemEffects");
            if (gemEffects.isEmpty() && !GamHandler.getGams(itemStack).isEmpty()) {
                try {
                    GamHandler.applygam(itemStack);
                    gemEffects = itemStack.getOrCreateTag().getCompound("GemEffects");
                } catch (Exception ignored) {}
            }
            if(gemEffects.getFloat("accurate_shooter")>event.getShooter().getRandom().nextFloat()){
                event.getShooter().addEffect(new MobEffectInstance(ModEffect.AccurateShooterE.get(),100));
            }
            if (gemEffects.getFloat("critical_hit")>event.getShooter().getRandom().nextFloat()){
                event.getShooter().addEffect(new MobEffectInstance(ModEffect.CriticalHitE.get(),20));
            }
            if (gemEffects.getFloat("infinite_firepower")>event.getShooter().getRandom().nextFloat()){
                event.getShooter().addEffect(new MobEffectInstance(ModEffect.InfiniteFirepowerE.get(),120));
            }
            ResourceLocation gunId = gunItem.getGunId(itemStack);
            long lastCd = itemStack.getOrCreateTag().getLong("cd");
            long gameTime = event.getShooter().level().getGameTime();
            int cdDuration = (int) GunSkill.Skill.getOrDefault(gunId.getPath(),Map.of("cd",-1)).getOrDefault("cd",-1);
            boolean should = (gameTime - lastCd > cdDuration) || (gameTime < lastCd);
            AmmocCount ac=new AmmocCount(itemStack);
            ac.add(1,should);
        }
    }
    @SubscribeEvent
    public static void entityAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(Taczadd.MODID,"playerdata"),new PlayerCapProvider());
        }
    }
    @SubscribeEvent
    public static void itemAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        Item item = event.getObject().getItem();
        if (item instanceof ArmorItem) {
            event.addCapability(new ResourceLocation(Taczadd.MODID, "gam"), new GamCap());
        } else if (item instanceof AbstractGunItem) {
            event.addCapability(new ResourceLocation(Taczadd.MODID, "gam"), new GamCap());
        }
    }


    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // 玩家死亡时保留数据
            event.getOriginal().getCapability(PlayerCapProvider.PLAYER_DATA).ifPresent(oldData -> {
                event.getEntity().getCapability(PlayerCapProvider.PLAYER_DATA).ifPresent(newData -> {
                    newData.deserializeNBT(oldData.serializeNBT());
                });
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event){
        if (event.getItemStack().getItem() instanceof ArmorItem || event.getItemStack().getItem() instanceof AbstractGunItem){
            ItemStack stack=event.getItemStack();
            // Append reinforcement level to the first line (item name) as "+<level>"
            int reinforced = stack.getOrCreateTag().getInt("reinforced");
            if (reinforced > 0 && !event.getToolTip().isEmpty()) {
                Component first = event.getToolTip().get(0);
                event.getToolTip().set(0, Component.empty().append(first).append(Component.literal(" +" + reinforced)));
            }
            if (event.getEntity() != null && net.minecraft.client.gui.screens.Screen.hasShiftDown()){
                event.getToolTip().addAll(GamHandler.getGamInfo(stack));
            }else{
                for (Map.Entry<Item,Long> e : GamHandler.getGamNum(stack).entrySet()){
                    GamItem gam =(GamItem)e.getKey();
                    int color=0xFFFFFF;
                    if (gam.getMaxCount()==1||e.getValue()>=4){
                        float glow = 0.5f + 0.5f * Mth.sin((float)(System.currentTimeMillis() % 2000) / 2000 * Mth.TWO_PI);
                        color= Mth.hsvToRgb(0.11f, 0.9f, 0.7f + 0.3f * glow);
                    } else if (e.getValue()>=2) {
                        color=0x55FF55;
                    }
                    event.getToolTip().add(Component.literal("✧"+gam.getGamname()+":"+e.getValue()+"/"+gam.getMaxCount()).withStyle(Style.EMPTY.withColor(color)));
                }
            }
            // Hide base damage tooltip line (requested)
            // if (stack.getOrCreateTag().getDouble("damagebase")>0){
            //     event.getToolTip().add(Component.translatable("tooltip.taczadd.damagebase",stack.getOrCreateTag().getDouble("damagebase")).withStyle(ChatFormatting.DARK_GREEN));
            // }
            // --- Real-time multiplier breakdown in tooltip for guns (split Equip vs Set) ---
            if (stack.getItem() instanceof AbstractGunItem) {
                Player viewer = event.getEntity();
                if (viewer == null) viewer = net.minecraft.client.Minecraft.getInstance().player; // client-side fallback
                // 增强鲁棒性：如果 GemEffects 不存在或为空，尝试重新计算
                CompoundTag gemEffects = stack.getOrCreateTag().getCompound("GemEffects");
                if (gemEffects.isEmpty() && !GamHandler.getGams(stack).isEmpty()) {
                    try {
                        GamHandler.applygam(stack);
                        gemEffects = stack.getOrCreateTag().getCompound("GemEffects");
                    } catch (Exception ignored) {}
                }
                float gem = gemEffects.getFloat("damageModifier");
                float gunReinf = stack.getOrCreateTag().getFloat("damagebase");
                float skill = stack.getOrCreateTag().getFloat("multiple");
                float equipPart = 0.0f;
                float setPart = 0.0f;
                if (viewer != null) {
                    AttributeInstance inst = viewer.getAttribute(ModAttributes.GunDamage.get());
                    if (inst != null) {
                        double total = inst.getValue();
                        // prefer UUID modifier; fallback to direct set detection if missing
                        AttributeModifier setMod = inst.getModifier(SET_GUNDAMAGE_UUID);
                        if (setMod != null) {
                            setPart = (float) setMod.getAmount();
                        } else {
                            if (isWearingFullSet(viewer, "defender")) setPart = 0.30f;
                            else if (isWearingFullSet(viewer, "attacker")) setPart = 0.20f;
                            else if (isWearingFullSet(viewer, "armored_chemical")) setPart = 0.15f;
                            else if (isWearingFullSet(viewer, "chemical_protective")) setPart = 0.10f;
                        }
                        equipPart = (float) (total - 1.0 - setPart);
                        if (equipPart < 0) equipPart = 0.0f;
                    }
                }
                float baseSum = Math.max(0f, gem + gunReinf + equipPart + setPart);
                float totalMult = (1.0f + baseSum) * (1.0f + skill) - 1.0f;
                event.getToolTip().add(Component.translatable("tooltip.taczadd.mult.header").withStyle(ChatFormatting.YELLOW));
                event.getToolTip().add(Component.translatable("tooltip.taczadd.mult.gem", String.format("%.1f", gem * 100f)));
                event.getToolTip().add(Component.translatable("tooltip.taczadd.mult.gunreinf", String.format("%.1f", gunReinf * 100f)));
                event.getToolTip().add(Component.translatable("tooltip.taczadd.mult.equip", String.format("%.1f", equipPart * 100f)));
                event.getToolTip().add(Component.translatable("tooltip.taczadd.mult.set", String.format("%.1f", setPart * 100f)));
                // Hide Skill line in multipliers (requested)
                // event.getToolTip().add(Component.translatable("tooltip.taczadd.mult.skill", String.format("%.1f", skill * 100f)));
                // Change summary line to omit the skill multiplicative term display
                event.getToolTip().add(Component.translatable("tooltip.taczadd.mult.summary_noskill",
                        String.format("%.1f", (gem + gunReinf + equipPart + setPart) * 100f),
                        String.format("%.1f", totalMult * 100f)
                ).withStyle(ChatFormatting.GREEN));

                int kills = stack.getOrCreateTag().getInt("gun_kills");
                event.getToolTip().add(Component.translatable("tooltip.taczadd.kills", kills).withStyle(ChatFormatting.GRAY));
            }

                // --- Armor item tooltip: show this piece's own contributions ---
            if (stack.getItem() instanceof ArmorItem) {
                Player viewer = event.getEntity();
                // Compute this piece's attribute contributions
                EquipmentSlot slot = ((ArmorItem) stack.getItem()).getEquipmentSlot();
                com.google.common.collect.Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> mods = stack.getAttributeModifiers(slot);
                double gdMul = 1.0;
                double ctaMul = 1.0;
                double aiMul = 1.0;
                double drAdd = 0.0;
                for (java.util.Map.Entry<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> entry : mods.entries()) {
                    net.minecraft.world.entity.ai.attributes.Attribute attr = entry.getKey();
                    AttributeModifier mod  = entry.getValue();
                    if (attr == ModAttributes.GunDamage.get()) {
                        gdMul *= (1.0 + mod.getAmount());
                    } else if (attr == ModAttributes.CTA.get()) {
                        ctaMul *= (1.0 + mod.getAmount());
                    } else if (attr == ModAttributes.armorIgnore.get()) {
                        aiMul *= (1.0 + mod.getAmount());
                    } else if (attr == ModAttributes.DamageReduction.get()) {
                        drAdd += mod.getAmount();
                    }
                }
                double gdPct  = (gdMul - 1.0) * 100.0;
                double ctaPct = (ctaMul - 1.0) * 100.0;
                double aiPct  = (aiMul - 1.0) * 100.0;
                event.getToolTip().add(Component.translatable("tooltip.taczadd.equip.header").withStyle(ChatFormatting.YELLOW));
                if (gdPct != 0.0)  event.getToolTip().add(Component.translatable("tooltip.taczadd.equip.gundamage", String.format("%.1f", gdPct)));
                if (ctaPct != 0.0) event.getToolTip().add(Component.translatable("tooltip.taczadd.equip.cta", String.format("%.1f", ctaPct)));
                if (aiPct != 0.0)  event.getToolTip().add(Component.translatable("tooltip.taczadd.equip.armorignore", String.format("%.1f", aiPct)));
                if (drAdd != 0.0)  event.getToolTip().add(Component.translatable("tooltip.taczadd.equip.damagereduction", String.format("%.0f", drAdd)));

                // Set header + active/inactive state per viewer
                ResourceLocation akey = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (viewer != null && akey != null && "lrarmor".equals(akey.getNamespace())) {
                    String p = akey.getPath();
                    String setPrefix = null;
                    if (p.startsWith("chemical_protective_")) setPrefix = "chemical_protective";
                    else if (p.startsWith("armored_chemical_")) setPrefix = "armored_chemical";
                    else if (p.startsWith("attacker_")) setPrefix = "attacker";
                    else if (p.startsWith("defender_")) setPrefix = "defender";
                    if (setPrefix != null) {
                        boolean active = isWearingFullSet(viewer, setPrefix);
                        event.getToolTip().add(Component.translatable("tooltip.taczadd.set.header").withStyle(ChatFormatting.AQUA));
                        event.getToolTip().add(Component.translatable(active ? "tooltip.taczadd.set.status.active" : "tooltip.taczadd.set.status.inactive"));
                    }
                }


            // ----- lrarmor set tooltip (all pieces show set bonus like gems) -----
            Item item = stack.getItem();
            if (item instanceof ArmorItem) {
                ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
                if (key != null && "lrarmor".equals(key.getNamespace())) {
                    String p = key.getPath();
                    if (p.startsWith("chemical_protective_")) {
                        event.getToolTip().add(Component.translatable("tooltip.taczadd.lrarmor.chemical_protective").withStyle(ChatFormatting.AQUA));
                    } else if (p.startsWith("armored_chemical_")) {
                        event.getToolTip().add(Component.translatable("tooltip.taczadd.lrarmor.armored_chemical").withStyle(ChatFormatting.AQUA));
                    } else if (p.startsWith("attacker_")) {
                        event.getToolTip().add(Component.translatable("tooltip.taczadd.lrarmor.attacker").withStyle(ChatFormatting.AQUA));
                    } else if (p.startsWith("defender_")) {
                        event.getToolTip().add(Component.translatable("tooltip.taczadd.lrarmor.defender").withStyle(ChatFormatting.AQUA));
                    }
                }
            }
//            tag.forEach((k,v)-> event.getToolTip().add(Component.literal(k.getDescription().getString()+"*"+v)));
            }

        }
    }

    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event){
        ItemStack gun = event.getAttacker().getMainHandItem();
        // 增强鲁棒性：确保 GemEffects 存在
        CompoundTag gemEffects = gun.getOrCreateTag().getCompound("GemEffects");
        if (gemEffects.isEmpty() && !GamHandler.getGams(gun).isEmpty()) {
            try {
                GamHandler.applygam(gun);
                gemEffects = gun.getOrCreateTag().getCompound("GemEffects");
            } catch (Exception ignored) {}
        }
        // Field Commander party buffs on kill
        if (gemEffects.getFloat("field_commander")>0){
            event.getAttacker().level().getEntitiesOfClass(Player.class, event.getAttacker().getBoundingBox().inflate(15)).forEach(player -> {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP,60,2));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,60,2));
            });
        }
        // Increment gun kill counter (stored on the attacking gun's NBT)
        if (gun.getItem() instanceof AbstractGunItem) {
            int kills = gun.getOrCreateTag().getInt("gun_kills");
            gun.getOrCreateTag().putInt("gun_kills", kills + 1);
        }
    }

    @SubscribeEvent
    public static void onGunDamage(EntityHurtByGunEvent.Pre event){
        if (event.getAttacker().hasEffect(ModEffect.CriticalHitE.get())){
            event.setBaseAmount(event.getBaseAmount()*2);
        }
        if (event.getAttacker().getAttribute(ModAttributes.armorIgnore.get())!=null&&event.getAttacker().getAttribute(ModAttributes.armorIgnore.get()).getValue()>0){
            event.getAttacker().setHealth(event.getAttacker().getHealth()-2);
        }
    }

    @SubscribeEvent
    public static void onSlotChange(TickEvent.PlayerTickEvent event){
        AttributeInstance attributeinstance = event.player.getAttribute(Attributes.MAX_HEALTH);
        if (attributeinstance != null&& !ItemStack.matches(event.player.lastItemInMainHand, event.player.getMainHandItem())) {
            attributeinstance.removeModifier(HealthModifierUUID);
            ItemStack mainHand = event.player.getMainHandItem();
            // 增强鲁棒性：确保 GemEffects 存在
            CompoundTag gemEffects = mainHand.getOrCreateTag().getCompound("GemEffects");
            if (gemEffects.isEmpty() && !GamHandler.getGams(mainHand).isEmpty()) {
                try {
                    GamHandler.applygam(mainHand);
                    gemEffects = mainHand.getOrCreateTag().getCompound("GemEffects");
                } catch (Exception ignored) {}
            }
            attributeinstance.addTransientModifier(new AttributeModifier(HealthModifierUUID, "Health Modifier", gemEffects.getFloat("health_m"), AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event){
        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if (player.getAttribute(ModAttributes.DamageReduction.get())!=null){
                event.setAmount((float) (event.getAmount()-player.getAttribute(ModAttributes.DamageReduction.get()).getValue()));
            }
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event){
        if (!DPSScoreHandler.isRecording())return;
        Player player;
        if(event.getSource().getEntity() instanceof Player){
            player=(Player) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof Player) {
            player=(Player) event.getSource().getDirectEntity();
        }else if (event.getSource().getEntity() instanceof Projectile projectile && projectile.getOwner() instanceof Player){
            player= (Player) projectile.getOwner();
        }else return;
        DPSScoreHandler.setData(player.getName().getString(), (int) event.getAmount());
    }

    // ----- LesRaisins Armor set bonuses -----
    private static final UUID SET_GUNDAMAGE_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SET_CTA_UUID       = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID SET_SPEED_UUID     = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private static boolean isLrarmorPiece(ItemStack stack, String setPrefix) {
        if (stack.isEmpty()) return false;
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null && "lrarmor".equals(key.getNamespace()) && key.getPath().startsWith(setPrefix + "_");
    }

    public static boolean isWearingFullSet(Player p, String setPrefix) {
        return isLrarmorPiece(p.getItemBySlot(EquipmentSlot.HEAD), setPrefix)
            && isLrarmorPiece(p.getItemBySlot(EquipmentSlot.CHEST), setPrefix)
            && isLrarmorPiece(p.getItemBySlot(EquipmentSlot.LEGS), setPrefix)
            && isLrarmorPiece(p.getItemBySlot(EquipmentSlot.FEET), setPrefix);
    }

    @SubscribeEvent
    public static void onPlayerTickSetBonus(TickEvent.PlayerTickEvent event) {
        Player p = event.player;
        if (event.phase != TickEvent.Phase.END) return;

        // Decide which set (if any) is fully worn
        String activeSet = null;
        if (isWearingFullSet(p, "defender")) {
            activeSet = "defender";
        } else if (isWearingFullSet(p, "attacker")) {
            activeSet = "attacker";
        } else if (isWearingFullSet(p, "armored_chemical")) {
            activeSet = "armored_chemical";
        } else if (isWearingFullSet(p, "chemical_protective")) {
            activeSet = "chemical_protective";
        }

        double gunDamage = 0.0;
        double cta = 0.0;
        double move = 0.0;
        double rpmBonus = 0.0;
        boolean glide = false;

        if (activeSet != null) {
            switch (activeSet) {
                case "chemical_protective":
                    gunDamage = 0.10; move = 0.10; glide = false; break;
                case "armored_chemical":
                    gunDamage = 0.15; move = 0.15; glide = true; break;
                case "attacker":
                    gunDamage = 0.20; cta = 0.30; move = 0.20; glide = true; break;
                case "defender":
                    gunDamage = 0.30; cta = 0.50; move = 0.25; rpmBonus = 0.10; glide = true; break;
            }
        }

        // Apply/remove attributes
        AttributeInstance gunAttr = p.getAttribute(ModAttributes.GunDamage.get());
        if (gunAttr != null) {
            gunAttr.removeModifier(SET_GUNDAMAGE_UUID);
            if (gunDamage > 0) {
                gunAttr.addTransientModifier(new AttributeModifier(SET_GUNDAMAGE_UUID, "lrarmor_set_gun", gunDamage, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
        AttributeInstance ctaAttr = p.getAttribute(ModAttributes.CTA.get());
        if (ctaAttr != null) {
            ctaAttr.removeModifier(SET_CTA_UUID);
            if (cta > 0) {
                ctaAttr.addTransientModifier(new AttributeModifier(SET_CTA_UUID, "lrarmor_set_cta", cta, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
        AttributeInstance spdAttr = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (spdAttr != null) {
            spdAttr.removeModifier(SET_SPEED_UUID);
            if (move > 0) {
                spdAttr.addTransientModifier(new AttributeModifier(SET_SPEED_UUID, "lrarmor_set_speed", move, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }

        if (!p.level().isClientSide) {
            // RPM bonus for DEFENDER: multiplicative on current gun NBT using a reversible tag
            ItemStack main = p.getMainHandItem();
            if (main.getItem() instanceof AbstractGunItem) {
                float applied = main.getOrCreateTag().getFloat("set_rpmadd");
                if (rpmBonus > 0) {
                    float want = 1.0f + (float) rpmBonus;
                    if (Math.abs(applied - want) > 1e-4) {
                        // revert previous if any
                        if (applied > 0) {
                            float cur = Math.max(main.getOrCreateTag().getFloat("rpmadd"), 1.0f);
                            main.getOrCreateTag().putFloat("rpmadd", Math.max(cur / applied, 1.0f));
                        }
                        float cur = Math.max(main.getOrCreateTag().getFloat("rpmadd"), 1.0f);
                        main.getOrCreateTag().putFloat("rpmadd", cur * want);
                        main.getOrCreateTag().putFloat("set_rpmadd", want);
                    }
                } else {
                    // remove previous set rpm bonus if exists
                    if (applied > 0) {
                        float cur = Math.max(main.getOrCreateTag().getFloat("rpmadd"), 1.0f);
                        main.getOrCreateTag().putFloat("rpmadd", Math.max(cur / applied, 1.0f));
                        main.getOrCreateTag().putFloat("set_rpmadd", 0f);
                    }
                }
            }
        }

        // Glide effect: vanilla-like: requires player double-press jump to request (client), then keep-alive only while requested and airborne
        if (glide) {
            boolean canGlide = !p.onGround() && !p.isInWaterOrBubble() && !p.isPassenger();
            boolean want = p.getCapability(PlayerCapProvider.PLAYER_DATA).map(c -> c.isGlideActive()).orElse(false);
            if (canGlide && want) {
                p.resetFallDistance(); // avoid any accumulated fall damage while gliding
                if (!p.isFallFlying()) {
                    p.startFallFlying();
                } else if ((p.level().getGameTime() & 0b1111) == 0) {
                    p.startFallFlying(); // keep-alive when not wearing real elytra
                }
            } else if (!p.level().isClientSide) {
                // Clear request when landed/invalid
                p.getCapability(PlayerCapProvider.PLAYER_DATA).ifPresent(c -> c.setGlideActive(false));
                p.resetFallDistance(); // ensure no leftover fall damage on landing
            }
        }
        }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if (!DPSScoreHandler.isRecording())return;
        if (event.phase==TickEvent.Phase.END&&event.getServer().getLevel(Level.OVERWORLD).getGameTime()%20==0){
            DPSScoreHandler.Uppdate(event.getServer());
        }
    }
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        DPSCommand.register(event.getDispatcher());
        TaczaddCommand.register(event.getDispatcher());
    }
}

package com.qdd.taczadd.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.datafixers.util.Pair;
import com.qdd.taczadd.item.Attributes.ModAttributes;
import com.qdd.taczadd.item.GamItem;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GamHandler {
    private static final UUID ARMOR_A=UUID.fromString("41fc617d-257d-40e9-ae47-36d4b472fd90");
    private static final UUID ARMOR_M=UUID.fromString("6f785e69-0f5c-4939-a96d-926e3c9edd98");
    private static final UUID HEALTH_A=UUID.fromString("69a188fc-8334-4186-8a56-630708e4ef95");
    private static final UUID HEALTH_M=UUID.fromString("783c2177-0377-46a5-8a4f-e71c6c5b2a2e");
    private static final UUID armorIgnore=UUID.fromString("4ca345a0-ebd0-4772-bbe0-2cf527ea3fd1");
    private static final UUID CTA=UUID.fromString("6a1a3c10-be17-4cff-a8f0-e5bb0738814a");
    public static List<ItemStack> getGams(ItemStack stack){
        List<ItemStack> list = new ArrayList<>();
        if (stack.getItem() instanceof ArmorItem || stack.getItem() instanceof AbstractGunItem){
            stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(gams-> {
                for (int i=0;i<gams.getSlots();i++){
                    if (gams.getStackInSlot(i).isEmpty())continue;
                    ItemStack gam=gams.getStackInSlot(i);
                    list.add(gam);
                }
            });
        }
        return list;
    }

    public static Map<Item, Long> getGamNum(ItemStack stack) {
        List<ItemStack> gams = getGams(stack);
        Map<Item, Long> map = new ConcurrentHashMap<>();

        if (gams.isEmpty()) return map;

        for (ItemStack stack1 : gams) {
            Item item = stack1.getItem();
            boolean found = false;

            // 检查是否已有同类GamItem
            for (Item existing : map.keySet()) {
                if (((GamItem) item).getGamname().equals(((GamItem) existing).getGamname())) {
                    map.put(existing, map.get(existing) + 1);
                    found = true;
                    break;
                }
            }

            if (!found) {
                map.put(item, 1L);
            }
        }

        return map;
    }

    public static void applygam(ItemStack stack) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Pair<String,Float>> list=new ArrayList<>();
        for (Map.Entry<Item,Long> e : getGamNum(stack).entrySet()){
                list.addAll(((GamItem)e.getKey()).applygam(stack, e.getValue()));
        }
        getGams(stack).forEach(stack1 -> list.add(((GamItem)stack1.getItem()).getEffct(stack1)));
        Map<String, Float> resultMap=new HashMap<>();
        for (Pair<String,Float> p :list){
            if (p.getSecond()==0)return;
            resultMap.merge(
                    p.getFirst(),
                    p.getSecond(),
                    Float::sum
            );
        }
        CompoundTag nbt = new CompoundTag();
        resultMap.forEach(nbt::putFloat);
        stack.getOrCreateTag().put("GemEffects", nbt);
        if(stack.getItem() instanceof ArmorItem armor) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            armor.defaultModifiers.forEach(
                    (attribute, modifier)->
                    {
                        if (!(modifier.getId().equals(ARMOR_A)||modifier.getId().equals(ARMOR_M)||modifier.getId().equals(HEALTH_A)||modifier.getId().equals(HEALTH_M)
                        || modifier.getId().equals(armorIgnore)||modifier.getId().equals(CTA))) {
                            builder.put(attribute, modifier);
                        }
                    }
            );
            builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_A,"ArmorA", resultMap.getOrDefault("armor_a",0f), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_M,"ArmorM", resultMap.getOrDefault("armor_m",0f), AttributeModifier.Operation.MULTIPLY_TOTAL));
            builder.put(Attributes.MAX_HEALTH, new AttributeModifier(HEALTH_A,"HealthA", resultMap.getOrDefault("health_a",0f), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.MAX_HEALTH, new AttributeModifier(HEALTH_M,"HealthM", resultMap.getOrDefault("health_m",0f), AttributeModifier.Operation.MULTIPLY_TOTAL));
            builder.put(ModAttributes.armorIgnore.get(), new AttributeModifier(armorIgnore,"armorIgnore", resultMap.getOrDefault("thorn_curse",0f), AttributeModifier.Operation.MULTIPLY_TOTAL));
            builder.put(ModAttributes.CTA.get(), new AttributeModifier(CTA,"cta", resultMap.getOrDefault("heavy_warrior",0f), AttributeModifier.Operation.MULTIPLY_TOTAL));
            if (resultMap.getOrDefault("heavy_warrior",0f)>0){
                builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(CTA,"slow", -0.45f, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            stack.getOrCreateTag().getList("AttributeModifiers", 10).clear();
            builder.build().forEach((attribute, modifier) -> stack.addAttributeModifier(attribute, modifier, LivingEntity.getEquipmentSlotForItem(stack)));
        }
    }

    public static List<Component> getGamInfo(ItemStack stack){
        List<Component> list=new ArrayList<>();
        CompoundTag tag=stack.getOrCreateTag().getCompound("GemEffects");
        for(String k:tag.getAllKeys()){
            if (k.equals("battlefield_physician")){
                list.add(Component.translatable("item.taczadd.battlefield_physician_gem").withStyle(ChatFormatting.GOLD));
            } else if (k.equals("field_commander")) {
                list.add(Component.translatable("item.taczadd.field_commander_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("critical_hit")) {
                list.add(Component.translatable("item.taczadd.critical_hit_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("infinite_firepower")) {
                list.add(Component.translatable("item.taczadd.infinite_firepower_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("firepower_suppression")) {
                list.add(Component.translatable("item.taczadd.firepower_suppression_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("royal_gift")) {
                list.add(Component.translatable("item.taczadd.royal_gift_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("glorious_emblem")) {
                list.add(Component.translatable("item.taczadd.glorious_emblem_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("heavy_warrior")) {
                list.add(Component.translatable("item.taczadd.heavy_warrior_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("thorn_curse")) {
                list.add(Component.translatable("item.taczadd.thorn_curse_gem").withStyle(ChatFormatting.GOLD));
            }else if (k.equals("armor_a")||k.equals("armor_m")||k.equals("health_a")||k.equals("health_m")) {
                continue;
            }else {
                String s = tag.getFloat(k) > 1 ? String.format("%.0f", tag.getFloat(k)) : String.format("%.0f%%", tag.getFloat(k) * 100);
                list.add(Component.literal(Component.translatable("GemEffects." + k).getString() + "+" + s).withStyle(ChatFormatting.GREEN));
            }
        }
        return list;
    }
}

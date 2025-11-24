package com.qdd.taczadd.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.tacz.guns.api.item.gun.AbstractGunItem;

import java.util.Map;

public class AmmocCount {
    public int count1;
    public int count2;
    public int count3;
    public int count4;
    public boolean shouldSkill1;
    public boolean shouldSkill2;
    public boolean shouldSkill3;
    public boolean shouldSkill4;
    public int maxcount1;
    public int maxcount2;
    public int maxcount3;
    public int maxcount4;
    public final ItemStack stack;



    public AmmocCount(ItemStack stack){
        this.stack=stack;
        CompoundTag tag=stack.getOrCreateTag().getCompound("AmmocCount");
        count1=tag.getInt("count1");
        count2=tag.getInt("count2");
        count3=tag.getInt("count3");
        count4=tag.getInt("count4");
        shouldSkill1=tag.getBoolean("shouldSkill1");
        shouldSkill2=tag.getBoolean("shouldSkill2");
        shouldSkill3=tag.getBoolean("shouldSkill3");
        shouldSkill4=tag.getBoolean("shouldSkill4");
        if (stack.getItem() instanceof AbstractGunItem gunItem) {
            ResourceLocation gunId = gunItem.getGunId(stack);
            maxcount1=(int) GunSkill.Skill.getOrDefault(gunId.getPath(), Map.of("ammoc",-1)).getOrDefault("ammoc",-1);
            maxcount2=(int) GunSkill.Skill.getOrDefault(gunId.getPath(), Map.of("ammoc2",-1)).getOrDefault("ammoc2",-1);
            maxcount3=(int) GunSkill.Skill.getOrDefault(gunId.getPath(), Map.of("ammoc3",-1)).getOrDefault("ammoc3",-1);
            maxcount4=(int) GunSkill.Skill.getOrDefault(gunId.getPath(), Map.of("ammoc4",-1)).getOrDefault("ammoc4",-1);
        }
    }
    public void save(){
        CompoundTag tag=new CompoundTag();
        tag.putInt("count1", count1);
        tag.putInt("count2", count2);
        tag.putInt("count3", count3);
        tag.putInt("count4", count4);
        tag.putBoolean("shouldSkill1", shouldSkill1);
        tag.putBoolean("shouldSkill2", shouldSkill2);
        tag.putBoolean("shouldSkill3", shouldSkill3);
        tag.putBoolean("shouldSkill4", shouldSkill4);
        stack.getOrCreateTag().put("AmmocCount", tag);
    }
    public void add(int count,boolean cd){
        // Skill 1: -1 => cd-only; >0 => count gated; 0 => disabled
        if (maxcount1 == -1) {
            shouldSkill1 = cd;
        } else if (maxcount1 > 0) {
            count1 = Math.min(count1 + (shouldSkill1 ? 0 : count), maxcount1);
            shouldSkill1 = (count1 >= maxcount1) && cd;
        } else {
            shouldSkill1 = false;
        }

        // Skill 2: same rules
        if (maxcount2 == -1) {
            shouldSkill2 = cd;
        } else if (maxcount2 > 0) {
            count2 = Math.min(count2 + (shouldSkill2 ? 0 : count), maxcount2);
            shouldSkill2 = (count2 >= maxcount2) && cd;
        } else {
            shouldSkill2 = false;
        }

        // Skill 3: same rules
        if (maxcount3 == -1) {
            shouldSkill3 = cd;
        } else if (maxcount3 > 0) {
            count3 = Math.min(count3 + (shouldSkill3 ? 0 : count), maxcount3);
            shouldSkill3 = (count3 >= maxcount3) && cd;
        } else {
            shouldSkill3 = false;
        }

        // Skill 4: same rules
        if (maxcount4 == -1) {
            shouldSkill4 = cd;
        } else if (maxcount4 > 0) {
            count4 = Math.min(count4 + (shouldSkill4 ? 0 : count), maxcount4);
            shouldSkill4 = (count4 >= maxcount4) && cd;
        } else {
            shouldSkill4 = false;
        }
        save();
    }

    public boolean shouldskill(int skill){
        return switch (skill) {
            case 1 -> shouldSkill1;
            case 2 -> shouldSkill2;
            case 3 -> shouldSkill3;
            case 4 -> shouldSkill4;
            default -> false;
        };
    }

    public void skill(int skill){
         switch (skill){
            case 1: {
                count1 = 1;
                shouldSkill1 = false;
                break;
            }
            case 2: {
                count2 = 1;
                shouldSkill2 = false;
                break;
            }
            case 3: {
                count3 = 1;
                shouldSkill3 = false;
                break;
            }
            case 4: {
                count4 = 1;
                shouldSkill4 = false;
                break;
            }
        }
        save();
    }
}

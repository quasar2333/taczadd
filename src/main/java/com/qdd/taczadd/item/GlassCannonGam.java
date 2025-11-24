package com.qdd.taczadd.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlassCannonGam extends GamItem{
    public GlassCannonGam(Properties properties) {
        super(properties,type.GUN);
    }
    @Override
    public int getMaxCount() {
        return 1;
    }

    @Override
    public boolean isBig(){
        return true;
    }

    @Override
    public List<Pair<String,Float>> applygam(ItemStack stack, Long num){
        List<Pair<String,Float>> list=new ArrayList<>();
        list.add(new Pair<>("health_m", -0.5f));
        list.add(new Pair<>("damageModifier", 0.5f));
        return list;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        Arrays.stream(Component.translatable("tooltip.gam.glass_cannon").getString().split("\n")).forEach(
                s -> list.add(Component.literal(s))
        );
    }
}

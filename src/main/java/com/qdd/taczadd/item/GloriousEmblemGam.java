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
import java.util.Random;

public class GloriousEmblemGam extends GamItem{
    public GloriousEmblemGam(Properties properties) {
        super(properties,type.ARMOR);
    }
    @Override
    public int getMaxCount() {
        return 2;
    }

    @Override
    public List<Pair<String,Float>> applygam(ItemStack stack, Long num){
        List<Pair<String,Float>> list=new ArrayList<>();
        if (num>=2){
            list.add(new Pair<>("health_m", 1f));
        }
        return list;
    }
    @Override
    public Pair<String,Float> getEffct(ItemStack stack) {
        return new Pair<>("health_a",stack.getOrCreateTag().getFloat("effect"));
    }

    @Override
    public Float randomEffect(){
        return new Random().nextInt(10,50)/10f;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        String string=stack.getOrCreateTag().getFloat("effect")==0?"???":stack.getOrCreateTag().getFloat("effect")+"";
        Arrays.stream(Component.translatable("tooltip.gam.glorious_emblem", string).getString().split("\n")).forEach(
                s -> list.add(Component.literal(s))
        );
    }
}

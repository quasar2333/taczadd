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

public class FuriousFragmentsGam extends GamItem{
    private int min =0;
    private int max =1;
    public FuriousFragmentsGam(Properties properties,int min,int max) {
        this(properties);
        this.min=min;
        this.max=max;
    }

    public FuriousFragmentsGam(Properties properties) {
        super(properties,type.GUN);
    }
    @Override
    public List<Pair<String,Float>> applygam(ItemStack stack, Long num){
        List<Pair<String,Float>> list=new ArrayList<>();
        if (num==4){
            list.add(new Pair<>("CRT", 0.2f));
        }
        if (num>=2){
            list.add(new Pair<>("rpmgam", 0.15f));
        }
        return list;
    }

    @Override
    public int getMaxCount() {
        return 4;
    }

    @Override
    public Pair<String,Float> getEffct(ItemStack stack) {
        return new Pair<>("rpmgam",stack.getOrCreateTag().getFloat("effect"));
    }

    @Override
    public Float randomEffect(){
        return new Random().nextInt(min,max)/1000f;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        String string=stack.getOrCreateTag().getFloat("effect")==0?"???":String.format("%.2f%%",stack.getOrCreateTag().getFloat("effect")*100);
        Arrays.stream(Component.translatable("tooltip.gam.furiousfragments", string).getString().split("\n")).forEach(
                s -> list.add(Component.literal(s))
        );
    }
}

package com.qdd.taczadd.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AccurateShooterGam extends GamItem{
    public AccurateShooterGam(Properties properties) {
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
    public Pair<String,Float> getEffct(ItemStack stack) {
        return new Pair<>("accurate_shooter",stack.getOrCreateTag().getFloat("effect"));
    }

    @Override
    public Float randomEffect(){
        return new Random().nextInt(50,200)/1000f;
    }


    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        String string=stack.getOrCreateTag().getFloat("effect")==0?"???":String.format("%.2f%%",stack.getOrCreateTag().getFloat("effect")*100);
        list.add(Component.translatable("tooltip.gam.accurate_shooter", string,string));
    }
}

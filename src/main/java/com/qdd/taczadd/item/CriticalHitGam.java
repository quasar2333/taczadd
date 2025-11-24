package com.qdd.taczadd.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class CriticalHitGam extends GamItem{
    public CriticalHitGam(Properties properties) {
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
        return new Pair<>("critical_hit",0.05f);
    }


    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("tooltip.gam.critical_hit"));
    }
}

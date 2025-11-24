package com.qdd.taczadd.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GamItem extends Item {
    private final type type;
    public GamItem(Properties properties,type type) {
        super(properties);
        this.type=type;
    }

    public List<Pair<String,Float>> applygam(ItemStack stack, Long num){
        return new ArrayList<>();
    }

    public String getGamname() {
        return getDescription().getString().split(" ")[0];
    }

    public type getType(){
        return type;
    }

    public int getMaxCount() {
        return 0;
    }
    public boolean isBig(){
        return false;
    }

    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("identify");
    }
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).append(!isFoil(stack)?Component.translatable("gam.identify"):Component.empty());
    }
    public Float randomEffect(){return 0f;}

    public Pair<String,Float> getEffct(ItemStack stack){
        return new Pair<>("", 0f);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("tooltip.gam."+getType().name().toLowerCase()).withStyle(ChatFormatting.BLUE));
    }


    public enum type{
        ARMOR,GUN
    }

}

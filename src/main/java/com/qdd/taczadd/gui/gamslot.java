package com.qdd.taczadd.gui;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.item.GamItem;
import com.qdd.taczadd.item.ModItems;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class gamslot extends SlotItemHandler {
    ItemStack stack;
    public gamslot(IItemHandler itemHandler, int index, int xPosition, int yPosition,ItemStack stack) {
        super(itemHandler, index, xPosition, yPosition);
        this.stack=stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
    @Override
    public boolean isHighlightable() {
        return !stack.isEmpty()&&stack.getOrCreateTag().getIntArray("gamholes")[this.getSlotIndex()]!=-1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack gam){
        long gamnum=Arrays.stream(stack.getOrCreateTag().getIntArray("gamholes"))
                .filter(num -> num == -1)
                .count();
        int num=0;
        if(stack.getItem() instanceof ArmorItem ai &&ai.getEquipmentSlot().isArmor()){
            if (ai.getEquipmentSlot()== EquipmentSlot.CHEST){
                num=2;
            }else {
                num=3;
                if (gam.getItem() instanceof GamItem gi&&gi.isBig()){return false;}
            }
        }
        boolean b=gam.getItem()== ModItems.BreakGam.get() && !stack.isEmpty() && !isHighlightable()&& gamnum>num;
        return (gam.getItem() instanceof GamItem gi &&gi.getType()==(stack.getItem() instanceof AbstractGunItem? GamItem.type.GUN: GamItem.type.ARMOR))
                &&isHighlightable()&&gam.getOrCreateTag().getBoolean("identify")&&(!gi.isBig()&&this.getSlotIndex()!=4||gi.isBig()&&this.getSlotIndex()==4)
                ||b;
    }

    @Override
    public IItemHandler getItemHandler(){
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(super.getItemHandler());
    }
}

package com.qdd.taczadd.gui;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.item.GamItem;
import com.qdd.taczadd.item.ModItems;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class gamslot extends SlotItemHandler {
    private java.util.function.Supplier<ItemStack> stackSupplier;
    
    public gamslot(IItemHandler itemHandler, int index, int xPosition, int yPosition, java.util.function.Supplier<ItemStack> stackSupplier) {
        super(itemHandler, index, xPosition, yPosition);
        this.stackSupplier = stackSupplier;
    }

    private ItemStack getEquipStack() {
        return stackSupplier != null ? stackSupplier.get() : ItemStack.EMPTY;
    }

    @Deprecated
    public void setStack(ItemStack stack) {
        // 不再使用缓存引用，改用 Supplier 动态获取
    }
    @Override
    public boolean isHighlightable() {
        ItemStack stack = getEquipStack();
        if (stack.isEmpty()) return false;
        int[] holes = stack.getOrCreateTag().getIntArray("gamholes");
        return isAllowedSlotForEquipOrHasItem() && holes.length > this.getSlotIndex() && holes[this.getSlotIndex()] != -1;
    }

    private boolean isAllowedSlotForEquipOrHasItem() {
        ItemStack stack = getEquipStack();
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof ArmorItem ai && ai.getEquipmentSlot().isArmor()) {
            boolean allowed;
            if (ai.getEquipmentSlot() == EquipmentSlot.CHEST) {
                allowed = this.getSlotIndex() == 0 || this.getSlotIndex() == 1 || this.getSlotIndex() == 4;
            } else if (ai.getEquipmentSlot() == EquipmentSlot.LEGS) {
                allowed = this.getSlotIndex() == 0 || this.getSlotIndex() == 3;
            } else {
                allowed = true;
            }
            if (!allowed) {
                return !getItemHandler().getStackInSlot(getSlotIndex()).isEmpty();
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack gam){
        if (!isAllowedSlotForEquipOrHasItem()) return false;
        ItemStack stack = getEquipStack();
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
    public boolean mayPickup(Player player) {
        return isAllowedSlotForEquipOrHasItem() && super.mayPickup(player);
    }

    @Override
    public void set(@NotNull ItemStack stackIn) {
        IItemHandler handler = getItemHandler();
        if (handler instanceof IItemHandlerModifiable mod) {
            mod.setStackInSlot(getSlotIndex(), stackIn);
            this.setChanged();
            return;
        }
        handler.extractItem(getSlotIndex(), Integer.MAX_VALUE, false);
        handler.insertItem(getSlotIndex(), stackIn, false);
        this.setChanged();
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        IItemHandler handler = getItemHandler();
        ItemStack extracted = handler.extractItem(getSlotIndex(), amount, false);
        if (!extracted.isEmpty()) {
            this.setChanged();
        }
        return extracted;
    }

    @Override
    public IItemHandler getItemHandler(){
        // 每次都动态获取当前物品的 capability，确保客户端/服务端一致
        ItemStack stack = getEquipStack();
        if (stack.isEmpty()) {
            return super.getItemHandler();
        }
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseGet(super::getItemHandler);
    }
    
    @Override
    public boolean hasItem() {
        // 确保从正确的 handler 检查物品
        return !getItemHandler().getStackInSlot(getSlotIndex()).isEmpty();
    }
    
    @Override
    public @NotNull ItemStack getItem() {
        // 确保从正确的 handler 获取物品
        return getItemHandler().getStackInSlot(getSlotIndex());
    }
}

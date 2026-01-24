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


public class gamslot extends SlotItemHandler {
    // 使用menu引用而非缓存stack，避免快速切换时数据滞后
    private com.qdd.taczadd.gui.menu.GamSettingMenu menu;
    @Deprecated
    ItemStack stack; // 保留用于兼容，优先使用menu
    
    public gamslot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ItemStack stack) {
        super(itemHandler, index, xPosition, yPosition);
        this.stack = stack;
    }

    public void setMenu(com.qdd.taczadd.gui.menu.GamSettingMenu menu) {
        this.menu = menu;
    }
    
    // 获取当前主槽位物品，优先从 menu 获取以避免缓存滞后
    private ItemStack getOwnerStack() {
        if (menu != null) {
            return menu.getItemStack();
        }
        return stack != null ? stack : ItemStack.EMPTY;
    }
    
    @Deprecated
    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
    // 解锁状态判断：只看gamholes，与装备部位限制解耦
    public boolean isUnlocked() {
        ItemStack ownerStack = getOwnerStack();
        if (ownerStack.isEmpty()) return false;
        int[] holes = ownerStack.getOrCreateTag().getIntArray("gamholes");
        return holes.length > this.getSlotIndex() && holes[this.getSlotIndex()] != -1;
    }
    
    @Override
    public boolean isHighlightable() {
        // 高亮显示需要同时满足：已解锁 且 允许放置
        return isUnlocked() && isAllowedSlotForEquipOrHasItem();
    }

    private boolean isAllowedSlotForEquipOrHasItem() {
        ItemStack ownerStack = getOwnerStack();
        if (ownerStack.isEmpty()) return false;
        if (ownerStack.getItem() instanceof ArmorItem ai && ai.getEquipmentSlot().isArmor()) {
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
        ItemStack ownerStack = getOwnerStack();
        if (ownerStack.isEmpty()) return false;
        
        // 解锁器逻辑：只要槽位未解锁就可以使用解锁器（与装备部位限制解耦）
        if (gam.getItem() == ModItems.BreakGam.get()) {
            return !isUnlocked();
        }
        
        // 宝石放置需要检查装备部位限制
        if (!isAllowedSlotForEquipOrHasItem()) return false;
        
        // 宝石放置逻辑
        if (!(gam.getItem() instanceof GamItem gi)) return false;
        
        // 检查宝石类型是否匹配
        GamItem.type requiredType = ownerStack.getItem() instanceof AbstractGunItem ? GamItem.type.GUN : GamItem.type.ARMOR;
        if (gi.getType() != requiredType) return false;
        
        // 检查槽位是否已解锁
        if (!isUnlocked()) return false;
        
        // 检查宝石是否已鉴定
        if (!gam.getOrCreateTag().getBoolean("identify")) return false;
        
        // 检查大宝石只能放在第5个槽位，普通宝石不能放在第5个槽位
        int slotIdx = this.getSlotIndex();
        if (gi.isBig()) {
            return slotIdx == 4;
        } else {
            return slotIdx != 4;
        }
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
        ItemStack ownerStack = getOwnerStack();
        if (ownerStack.isEmpty()) {
            return super.getItemHandler();
        }
        // 直接从 capability 获取，GamCap 会自动从 NBT 同步
        return ownerStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
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

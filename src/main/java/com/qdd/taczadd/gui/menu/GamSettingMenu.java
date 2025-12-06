package com.qdd.taczadd.gui.menu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.gui.ModMenuType;
import com.qdd.taczadd.gui.gamslot;
import com.qdd.taczadd.handler.GamHandler;
import com.qdd.taczadd.item.GamItem;
import com.qdd.taczadd.item.ModItems;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.lang.reflect.InvocationTargetException;

public class GamSettingMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final Container ItemSlots = new SimpleContainer(1);
    private final ItemStackHandler ish=new ItemStackHandler(5);

    public GamSettingMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv,new SimpleContainerData(0));
    }

    public GamSettingMenu(int pContainerId, Inventory inv,ContainerData data) {
        super(ModMenuType.GAM_SETTING_MENU.get(), pContainerId);
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.addSlot(new Slot(ItemSlots,0,44,25){
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ArmorItem||stack.getItem() instanceof AbstractGunItem;
            }
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                super.setByPlayer(stack);
                GamSettingMenu.this.slotsChanged(this.container);
            }
        });
        this.addSlot(new gamslot(ish, 0, 14, 8, getItemStack()));
        this.addSlot(new gamslot(ish, 1, 74, 8, getItemStack()));
        this.addSlot(new gamslot(ish, 2, 14, 42, getItemStack()));
        this.addSlot(new gamslot(ish, 3, 74, 42, getItemStack()));
        this.addSlot(new gamslot(ish, 4, 44, 61, getItemStack()));
        addDataSlots(data);
    }

    public ItemStack getItemStack(){
        return this.ItemSlots.getItem(0);
    }

    public void slotsChanged(Container container) {
        if (container==this.ItemSlots){
            ItemStack stack=getItemStack();
            if (!stack.isEmpty()) {
                if (stack.getOrCreateTag().getIntArray("gamholes").length==0) {
                    stack.getOrCreateTag().putIntArray("gamholes", new int[]{0, -1, -1, -1, -1});
                }
                // 从 NBT 备份恢复宝石数据到 capability (Mohist 兼容)
                restoreGemsFromNBT(stack);
            }
            ((gamslot)this.slots.get(37)).setStack(stack);
            ((gamslot)this.slots.get(38)).setStack(stack);
            ((gamslot)this.slots.get(39)).setStack(stack);
            ((gamslot)this.slots.get(40)).setStack(stack);
            ((gamslot)this.slots.get(41)).setStack(stack);
        }
        this.broadcastChanges();
    }
    
    /**
     * 从 NBT 备份恢复宝石到 capability (用于 Mohist 等混合服务端兼容)
     */
    private void restoreGemsFromNBT(ItemStack stack) {
        if (stack.isEmpty()) return;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("GemBackup", 9)) return; // 没有备份数据
        
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ListTag backup = tag.getList("GemBackup", 10);
            // 检查 capability 是否为空，如果为空则从备份恢复
            boolean capEmpty = true;
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!handler.getStackInSlot(i).isEmpty()) {
                    capEmpty = false;
                    break;
                }
            }
            if (capEmpty && !backup.isEmpty()) {
                // 恢复备份
                if (handler instanceof ItemStackHandler ish) {
                    ish.deserializeNBT(tag.getCompound("GemBackupHandler"));
                }
            }
        });
    }
    
    /**
     * 将宝石数据备份到 NBT (用于 Mohist 等混合服务端兼容)
     */
    private void backupGemsToNBT(ItemStack stack) {
        if (stack.isEmpty()) return;
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof ItemStackHandler ish) {
                CompoundTag handlerTag = ish.serializeNBT();
                stack.getOrCreateTag().put("GemBackupHandler", handlerTag);
                
                ListTag list = new ListTag();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack gem = handler.getStackInSlot(i);
                    if (!gem.isEmpty()) {
                        CompoundTag gemTag = new CompoundTag();
                        gemTag.putInt("Slot", i);
                        gem.save(gemTag);
                        list.add(gemTag);
                    }
                }
                stack.getOrCreateTag().put("GemBackup", list);
            }
        });
    }
    @Override
    public void removed(Player p_38940_) {
        super.removed(p_38940_);
        ItemStack stack = getItemStack();
        if (!stack.isEmpty()){
            // 备份宝石数据到 NBT (Mohist 兼容)
            backupGemsToNBT(stack);
            p_38940_.getInventory().placeItemBackInInventory(stack);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slot, int p_150401_, ClickType p_150402_, Player p_150403_) {
        if (slot != -1 && slot != -999 && slot < this.slots.size()) {
            Slot slot7 = this.slots.get(slot);
            ItemStack itemstack9 = slot7.getItem();
            ItemStack itemstack10 = this.getCarried();
            if (itemstack10.getItem() == ModItems.IdentifyGam.get() && itemstack9.getItem() instanceof GamItem && !itemstack9.getOrCreateTag().getBoolean("identify")) {
                itemstack9.getOrCreateTag().putBoolean("identify", true);
                itemstack9.getOrCreateTag().putFloat("effect", ((GamItem)itemstack9.getItem()).randomEffect());
                itemstack10.shrink(1);
            } else if (slot == 36) {
                // 主槽位（枪械/护甲）被点击
                // 取出前先备份宝石数据
                if (!getItemStack().isEmpty()) {
                    backupGemsToNBT(getItemStack());
                }
                super.clicked(slot, p_150401_, p_150402_, p_150403_);
            } else if (slot > 36) {
                if (itemstack10.getItem()==ModItems.BreakGam.get()&&slot7.mayPlace(itemstack10)){
                    int [] gamholes=getItemStack().getOrCreateTag().getIntArray("gamholes");
                    gamholes[slot7.getSlotIndex()]=slot7.getSlotIndex();
                    getItemStack().getOrCreateTag().putIntArray("gamholes",gamholes);
                    itemstack10.shrink(1);
                    return;
                }
                super.clicked(slot, p_150401_, p_150402_, p_150403_);
                try {
                    GamHandler.applygam(getItemStack());
                    // 每次修改宝石后备份到 NBT
                    backupGemsToNBT(getItemStack());
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                super.clicked(slot, p_150401_, p_150402_, p_150403_);
            }
        }
        else {
            super.clicked(slot, p_150401_, p_150402_, p_150403_);
        }
    }

    @Override
    public boolean stillValid(Player Player) {
        return true;
    }
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}

package com.qdd.taczadd.gui.menu;

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
import net.minecraftforge.items.ItemStackHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamSettingMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final Container ItemSlots = new SimpleContainer(1);
    private final ItemStackHandler ish=new ItemStackHandler(5);
    // 防止快速操作导致的竞态条件
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    // 缓存上一次的枪械ItemStack，用于验证数据一致性
    private ItemStack lastGunStack = ItemStack.EMPTY;

    private int[] ensureGamholes(ItemStack stack) {
        if (stack.isEmpty()) {
            return new int[0];
        }
        int[] holes = stack.getOrCreateTag().getIntArray("gamholes");
        // 只有当数组长度不是 5 时才需要完全重置
        if (holes.length == 5) {
            // 规范化数组：将非法值替换为合法值，但保留已解锁状态
            boolean modified = false;
            for (int i = 0; i < 5; i++) {
                if (holes[i] != -1 && holes[i] != i) {
                    // 如果值不是 -1 也不是索引，但是非负数，视为已解锁，规范化为索引值
                    if (holes[i] >= 0) {
                        holes[i] = i;
                        modified = true;
                    } else {
                        // 其他负数视为锁定
                        holes[i] = -1;
                        modified = true;
                    }
                }
            }
            if (modified) {
                stack.getOrCreateTag().putIntArray("gamholes", holes);
            }
            return holes;
        }
        // 所有槽位默认锁定，需要使用解锁器解锁
        int[] fixed = new int[]{-1, -1, -1, -1, -1};
        stack.getOrCreateTag().putIntArray("gamholes", fixed);
        return fixed;
    }

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
            
            // 如果之前有枪械且现在取出，确保宝石数据已保存
            if (!lastGunStack.isEmpty() && stack.isEmpty()) {
                forceSyncGemData(lastGunStack);
            }
            
            if (!stack.isEmpty()) {
                ensureGamholes(stack);
                // 从NBT恢复宝石数据到Capability
                syncGemFromNBT(stack);
            }
            
            // 更新缓存
            lastGunStack = stack.copy();
            
            ((gamslot)this.slots.get(37)).setStack(stack);
            ((gamslot)this.slots.get(38)).setStack(stack);
            ((gamslot)this.slots.get(39)).setStack(stack);
            ((gamslot)this.slots.get(40)).setStack(stack);
            ((gamslot)this.slots.get(41)).setStack(stack);
        }
        this.broadcastChanges();
    }
    
    // 强制同步宝石数据到NBT
    private void forceSyncGemData(ItemStack stack) {
        if (stack.isEmpty()) return;
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            net.minecraft.nbt.CompoundTag gemTag = new net.minecraft.nbt.CompoundTag();
            net.minecraft.nbt.ListTag items = new net.minecraft.nbt.ListTag();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack gemStack = handler.getStackInSlot(i);
                if (!gemStack.isEmpty()) {
                    net.minecraft.nbt.CompoundTag itemTag = new net.minecraft.nbt.CompoundTag();
                    itemTag.putByte("Slot", (byte) i);
                    gemStack.save(itemTag);
                    items.add(itemTag);
                }
            }
            gemTag.put("Items", items);
            gemTag.putInt("Size", handler.getSlots());
            stack.getOrCreateTag().put("GemStorage", gemTag);
        });
    }
    
    // 从NBT同步宝石数据到Capability
    private void syncGemFromNBT(ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!stack.hasTag() || !stack.getTag().contains("GemStorage")) return;
        // GamCap会在getCapability时自动从NBT同步，这里只需要触发一次获取
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {});
    }
    @Override
    public void removed(Player p_38940_) {
        super.removed(p_38940_);
        ItemStack stack = getItemStack();
        if (!stack.isEmpty()){
            // 关闭界面前强制同步宝石数据
            forceSyncGemData(stack);
            try {
                GamHandler.applygam(stack);
            } catch (Exception ignored) {}
            p_38940_.getInventory().placeItemBackInInventory(stack);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slot, int p_150401_, ClickType p_150402_, Player p_150403_) {
        // 防止快速操作导致的竞态条件
        if (!isProcessing.compareAndSet(false, true)) {
            return;
        }
        
        try {
            clickedInternal(slot, p_150401_, p_150402_, p_150403_);
        } finally {
            isProcessing.set(false);
        }
    }
    
    private void clickedInternal(int slot, int p_150401_, ClickType p_150402_, Player p_150403_) {
        if (slot != -1 && slot != -999 && slot < this.slots.size()) {
            Slot slot7 = this.slots.get(slot);
            ItemStack itemstack9 = slot7.getItem();
            ItemStack itemstack10 = this.getCarried();
            if (itemstack10.getItem() == ModItems.IdentifyGam.get() && itemstack9.getItem() instanceof GamItem && !itemstack9.getOrCreateTag().getBoolean("identify")) {
                itemstack9.getOrCreateTag().putBoolean("identify", true);
                itemstack9.getOrCreateTag().putFloat("effect", ((GamItem)itemstack9.getItem()).randomEffect());
                itemstack10.shrink(1);
            } else if (slot == 36) {
                // 主槽位（枪械/护甲）被点击 - 在操作前保存当前宝石数据
                ItemStack currentGun = getItemStack();
                if (!currentGun.isEmpty()) {
                    forceSyncGemData(currentGun);
                }
                super.clicked(slot, p_150401_, p_150402_, p_150403_);
                // 操作后更新缓存
                lastGunStack = getItemStack().copy();
            } else if (slot > 36) {
                // 宝石槽位解锁逻辑
                if (itemstack10.getItem()==ModItems.BreakGam.get()){
                    ItemStack stack = getItemStack();
                    if (!stack.isEmpty()) {
                        int [] gamholes = ensureGamholes(stack);
                        int idx = slot7.getSlotIndex();
                        // 检查槽位是否已锁定且可以解锁
                        if (idx >= 0 && idx < gamholes.length && gamholes[idx] == -1) {
                            gamholes[idx] = idx;
                            stack.getOrCreateTag().putIntArray("gamholes", gamholes);
                            itemstack10.shrink(1);
                            this.broadcastChanges();
                        }
                    }
                    return;
                }
                super.clicked(slot, p_150401_, p_150402_, p_150403_);
                try {
                    ItemStack gunStack = getItemStack();
                    if (!gunStack.isEmpty()) {
                        GamHandler.applygam(gunStack);
                        forceSyncGemData(gunStack);
                    }
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

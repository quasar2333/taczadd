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
    // 脏标记：标记数据是否需要在关闭时写回
    private boolean isDirty = false;

    private int[] ensureGamholes(ItemStack stack) {
        if (stack.isEmpty()) {
            return new int[0];
        }
        int[] holes = stack.getOrCreateTag().getIntArray("gamholes");
        // 只有当数组长度不是 5 时才需要完全重置
        if (holes.length == 5) {
            // 规范化数组：只保留明确的解锁值(等于索引值)，其他都视为锁定
            boolean modified = false;
            for (int i = 0; i < 5; i++) {
                // 只有当值等于索引时才视为已解锁，其他所有值都视为锁定
                if (holes[i] != -1 && holes[i] != i) {
                    holes[i] = -1;
                    modified = true;
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
            
            if (!stack.isEmpty()) {
                ensureGamholes(stack);
                // 从NBT恢复宝石数据到Capability
                syncGemFromNBT(stack);
            }
            
            // 更新所有宝石槽位的引用（传递menu引用而非缓存stack）
            for (int i = 37; i <= 41; i++) {
                ((gamslot)this.slots.get(i)).setMenu(this);
            }
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
            // 关闭界面前统一写回宝石数据（防抖：只在关闭时写回一次）
            if (isDirty) {
                forceSyncGemData(stack);
                try {
                    GamHandler.applygam(stack);
                } catch (Exception ignored) {}
            }
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
                // 主槽位（枪械/护甲）被点击 - 标记为脏，延迟到关闭时写回
                isDirty = true;
                super.clicked(slot, p_150401_, p_150402_, p_150403_);
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
                // 宝石槽位操作后标记为脏
                isDirty = true;
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

package com.qdd.taczadd.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 宝石 Capability - 使用 NBT 作为唯一真实数据源
 * 每次读取时从 NBT 同步，每次写入后保存到 NBT
 * 这样可以解决 Mohist 等混合服务端的 capability 同步问题
 */
public class GamCap implements ICapabilitySerializable<CompoundTag> {
    private static final String NBT_KEY = "GemStorage";
    
    private final ItemStack owner;
    private final SyncedItemStackHandler gam;
    private final LazyOptional<IItemHandler> optional;
    
    public GamCap() {
        this(ItemStack.EMPTY);
    }
    
    public GamCap(ItemStack owner) {
        this.owner = owner;
        this.gam = new SyncedItemStackHandler(5, owner);
        this.optional = LazyOptional.of(() -> this.gam);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 每次获取 capability 时，先从 NBT 同步数据
        if (cap == ForgeCapabilities.ITEM_HANDLER && !owner.isEmpty()) {
            syncFromNBT();
        }
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, optional);
    }
    
    /**
     * 从 ItemStack 的 NBT 同步数据到 handler
     */
    private void syncFromNBT() {
        if (owner.isEmpty()) return;
        CompoundTag tag = owner.getTag();
        if (tag != null && tag.contains(NBT_KEY)) {
            CompoundTag gemTag = tag.getCompound(NBT_KEY);
            // 只有当 NBT 数据与当前不同时才同步，避免覆盖刚写入的数据
            CompoundTag currentTag = gam.serializeNBT();
            if (!gemTag.equals(currentTag)) {
                gam.deserializeNBTSilent(gemTag);
            }
        }
    }
    
    /**
     * 保存数据到 ItemStack 的 NBT
     */
    public void syncToNBT() {
        if (owner.isEmpty()) return;
        owner.getOrCreateTag().put(NBT_KEY, gam.serializeNBT());
    }

    @Override
    public CompoundTag serializeNBT() {
        return gam.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        gam.deserializeNBT(nbt);
        syncToNBT();
    }
    
    /**
     * 自动同步到 NBT 的 ItemStackHandler
     */
    private static class SyncedItemStackHandler extends ItemStackHandler {
        private final ItemStack owner;
        private boolean silent = false;
        
        public SyncedItemStackHandler(int size, ItemStack owner) {
            super(size);
            this.owner = owner;
        }
        
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (!silent) {
                saveToOwner();
            }
        }
        
        private void saveToOwner() {
            if (!owner.isEmpty()) {
                owner.getOrCreateTag().put(NBT_KEY, this.serializeNBT());
            }
        }
        
        /**
         * 静默反序列化，不触发保存（用于从 NBT 同步时）
         */
        public void deserializeNBTSilent(CompoundTag nbt) {
            this.silent = true;
            this.deserializeNBT(nbt);
            this.silent = false;
        }
    }
}

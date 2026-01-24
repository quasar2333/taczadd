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
    private static final String VERSION_KEY = "GemVersion";
    
    private final ItemStack owner;
    private final SyncedItemStackHandler gam;
    private final LazyOptional<IItemHandler> optional;
    // 版本戳：用于避免刚写入的数据被旧NBT覆盖
    private int localVersion = 0;
    
    public GamCap() {
        this(ItemStack.EMPTY);
    }
    
    public GamCap(ItemStack owner) {
        this.owner = owner;
        this.gam = new SyncedItemStackHandler(5);
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
            // 版本戳检查：如果NBT版本低于本地版本，跳过同步避免覆盖刚写入的数据
            int nbtVersion = tag.getInt(VERSION_KEY);
            if (nbtVersion < localVersion) {
                return;
            }
            // 只有当 NBT 数据与当前不同时才同步
            CompoundTag currentTag = gam.serializeNBT();
            if (!gemTag.equals(currentTag)) {
                gam.deserializeNBTSilent(gemTag);
                localVersion = nbtVersion;
            }
        }
    }
    
    /**
     * 保存数据到 ItemStack 的 NBT
     */
    public void syncToNBT() {
        if (owner.isEmpty()) return;
        localVersion++;
        CompoundTag tag = owner.getOrCreateTag();
        tag.put(NBT_KEY, gam.serializeNBT());
        tag.putInt(VERSION_KEY, localVersion);
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
    private class SyncedItemStackHandler extends ItemStackHandler {
        private boolean silent = false;
        
        public SyncedItemStackHandler(int size) {
            super(size);
        }
        
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (!silent) {
                saveToOwner();
            }
        }
        
        private void saveToOwner() {
            // 使用外部类的syncToNBT以更新版本戳
            syncToNBT();
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

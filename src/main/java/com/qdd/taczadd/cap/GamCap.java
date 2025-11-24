package com.qdd.taczadd.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GamCap implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private ItemStackHandler gam=null;

    private LazyOptional<IItemHandler> optional=LazyOptional.of(this::createPlayerCap);
    private ItemStackHandler createPlayerCap() {
        if(this.gam == null) {
            this.gam = new ItemStackHandler(5);
        }

        return this.gam;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return createPlayerCap().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerCap().deserializeNBT(nbt);
    }

}

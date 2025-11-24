package com.qdd.taczadd.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerCap> PLAYER_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });

    private PlayerCap data = null;
    private final LazyOptional<PlayerCap> optional = LazyOptional.of(this::createPlayerCap);
    private PlayerCap createPlayerCap() {
        if(this.data == null) {
            this.data = new PlayerCap();
        }

        return this.data;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
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

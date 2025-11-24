package com.qdd.taczadd.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerCap implements INBTSerializable<CompoundTag> {
    private String talent = "";
    private boolean glideActive = false; // 是否处于鞘翅滑翔请求状态（由双击空格触发）

    public String getTalent(){
        return this.talent;
    }
    public void setTalent(String talent){
        this.talent = talent;
    }

    public boolean isGlideActive() {
        return glideActive;
    }
    public void setGlideActive(boolean active) {
        this.glideActive = active;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("talent", talent);
        tag.putBoolean("glideActive", glideActive);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        talent = tag.getString("talent");
        glideActive = tag.getBoolean("glideActive");
    }
}

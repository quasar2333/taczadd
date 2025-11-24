package com.qdd.taczadd.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalDataStorage extends SavedData {
    public static GlobalDataStorage getGlobalData() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(GlobalDataStorage::load, GlobalDataStorage::new, "global_data");
    }
    private Map<String, Double> playerDamageMap = new LinkedHashMap<>();
    private boolean isRecording;

    // 必须有的加载方法
    public static GlobalDataStorage load(CompoundTag nbt) {
        GlobalDataStorage storage = new GlobalDataStorage();
        storage.playerDamageMap = new LinkedHashMap<>();
        for(String key : nbt.getCompound("playerDamageMap").getAllKeys()){
            storage.playerDamageMap.put(key, nbt.getDouble(key));
        }
        storage.isRecording = nbt.getBoolean("isRecording");
        return storage;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        CompoundTag nbt1 = new CompoundTag();
        for (Map.Entry<String, Double> entry : playerDamageMap.entrySet()){
            nbt1.putDouble(entry.getKey(), entry.getValue());
        }
        nbt.put("playerDamageMap", nbt1);
        nbt.putBoolean("isRecording", isRecording);
        return nbt;
    }

    // 自定义方法：跨维度安全存取
    public Map<String, Double> getData() {
        return playerDamageMap;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
        setDirty(); // 标记需要保存
    }

    public void setData( String playername, double damage) {
        playerDamageMap.merge(playername, damage, Double::sum);
        setDirty(); // 标记需要保存
    }
}

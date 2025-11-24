package com.qdd.taczadd.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 装备诱导器网络包
 */
public class InducerPacket {
    
    public InducerPacket() {
        // 空构造函数
    }
    
    public void encode(FriendlyByteBuf buffer) {
        // 不需要额外数据
    }
    
    public static InducerPacket decode(FriendlyByteBuf buffer) {
        return new InducerPacket();
    }
    
    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();
        handler.enqueueWork(() -> {
            // 触发菜单按钮点击事件
            handler.getSender().containerMenu.clickMenuButton(handler.getSender(), 0);
        });
        handler.setPacketHandled(true);
    }
}




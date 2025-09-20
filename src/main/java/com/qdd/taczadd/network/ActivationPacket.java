package com.qdd.taczadd.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 激活核心的网络包：点击按钮触发
 */
public class ActivationPacket {
    public ActivationPacket() {}

    public void encode(FriendlyByteBuf buffer) {}

    public static ActivationPacket decode(FriendlyByteBuf buffer) {
        return new ActivationPacket();
    }

    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();
        handler.enqueueWork(() -> {
            handler.getSender().containerMenu.clickMenuButton(handler.getSender(), 0);
        });
        handler.setPacketHandled(true);
    }
}


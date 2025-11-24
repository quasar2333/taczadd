package com.qdd.taczadd.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReinforcedPacket {
    public ReinforcedPacket(){

    }
    public void encode(FriendlyByteBuf buffer) {

    }

    public static ReinforcedPacket decode(FriendlyByteBuf buffer) {
        return new ReinforcedPacket();
    }
    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();
        handler.enqueueWork(() -> handler.getSender().containerMenu.clickMenuButton(handler.getSender(), 0));
        handler.setPacketHandled(true);
    }
}

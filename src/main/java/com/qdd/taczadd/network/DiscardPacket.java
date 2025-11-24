package com.qdd.taczadd.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DiscardPacket<D> {
    private final int entityId;

    public DiscardPacket(int entity){
        entityId=entity;
    }
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
    }

    public static <D> DiscardPacket<D> decode(FriendlyByteBuf buffer) {
        return new DiscardPacket<>(buffer.readVarInt());
    }
    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();
        handler.enqueueWork(() -> handler.getSender().level().getEntity(entityId).discard());
        handler.setPacketHandled(true);
    }
}

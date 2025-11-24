package com.qdd.taczadd.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CountShootPacket(int count) {
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.count);
    }

    public static CountShootPacket decode(FriendlyByteBuf buffer) {
        return new CountShootPacket(buffer.readVarInt());
    }

    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();
        handler.enqueueWork(() -> {
                    Player player = handler.getSender();
                    if (player != null) {
                        if (player.getMainHandItem().getItem() instanceof AbstractGunItem) {

                        }
                    }
                }
        );
        handler.setPacketHandled(true);
    }
}

package com.qdd.taczadd.network;

import com.qdd.taczadd.cap.PlayerCap;
import com.qdd.taczadd.cap.PlayerCapProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartGlidePacket {
    public StartGlidePacket() {}
    public static void encode(StartGlidePacket pkt, FriendlyByteBuf buf) {}
    public static StartGlidePacket decode(FriendlyByteBuf buf) { return new StartGlidePacket(); }
    public static void receivePacket(StartGlidePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp == null) return;
            sp.getCapability(PlayerCapProvider.PLAYER_DATA).ifPresent(PlayerCap::setGlideActive);
            // 立即尝试开启一次（其后由tick保活），仅在空中时
            if (!sp.onGround() && !sp.isInWaterOrBubble() && !sp.isPassenger()) {
                sp.startFallFlying();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}


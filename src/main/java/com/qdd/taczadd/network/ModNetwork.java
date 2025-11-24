package com.qdd.taczadd.network;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings({"removal"})
public class ModNetwork {
    public static final String VERSION = "1.0";
    public static SimpleChannel PACKET_CHANNEL= NetworkRegistry.newSimpleChannel(new ResourceLocation(Taczadd.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);
    public static void init() {
        int id = 0;
        PACKET_CHANNEL.registerMessage(id++, DiscardPacket.class, DiscardPacket::encode, DiscardPacket::decode, DiscardPacket::receivePacket);
        PACKET_CHANNEL.registerMessage(id++, GamSettingPacket.class, GamSettingPacket::encode, GamSettingPacket::decode, GamSettingPacket::receivePacket);
        PACKET_CHANNEL.registerMessage(id++, ReinforcedPacket.class, ReinforcedPacket::encode, ReinforcedPacket::decode, ReinforcedPacket::receivePacket);
        PACKET_CHANNEL.registerMessage(id++, CountShootPacket.class, CountShootPacket::encode, CountShootPacket::decode, CountShootPacket::receivePacket);
        PACKET_CHANNEL.registerMessage(id++, UpgradePacket.class, UpgradePacket::encode, UpgradePacket::decode, UpgradePacket::receivePacket);
        PACKET_CHANNEL.registerMessage(id++, ActivationPacket.class, ActivationPacket::encode, ActivationPacket::decode, ActivationPacket::receivePacket);
        PACKET_CHANNEL.registerMessage(id++, StartGlidePacket.class, StartGlidePacket::encode, StartGlidePacket::decode, StartGlidePacket::receivePacket);
        PACKET_CHANNEL.registerMessage(id++, InducerPacket.class, InducerPacket::encode, InducerPacket::decode, InducerPacket::receivePacket);
    }
}

package com.qdd.taczadd.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.SimpleContainerData;
import com.qdd.taczadd.gui.menu.GamSettingMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class GamSettingPacket {
    public GamSettingPacket() {}

    public void encode(FriendlyByteBuf buffer) {

    }

    public static  GamSettingPacket decode(FriendlyByteBuf buffer) {
        return new GamSettingPacket();
    }
    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();
        var t=new SimpleMenuProvider((containerId, playerInventory, player) -> new GamSettingMenu(containerId, playerInventory,new SimpleContainerData(0))
                ,Component.translatable("menu.title.taczadd.gamsetting"));
        handler.enqueueWork(() -> NetworkHooks.openScreen(handler.getSender(),t));
        handler.setPacketHandled(true);
    }
}

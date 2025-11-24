package com.qdd.taczadd.event;

import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.handler.KeyBindingHandler;
import com.qdd.taczadd.network.GamSettingPacket;
import com.qdd.taczadd.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side only game events.
 * This class is only loaded on the client side to prevent server crashes.
 */
@Mod.EventBusSubscriber(modid = Taczadd.MODID, value = Dist.CLIENT)
public class ClientGameEvent {
    
    // Client double-jump detection state for Elytra-like glide
    private static boolean prevJumpPressed = false;
    private static long lastJumpPressTick = -1000L;

    @SubscribeEvent
    public static void onKeyBind(InputEvent.Key event){
        Player player = Minecraft.getInstance().player;
        if(player==null)return;
        if (KeyBindingHandler.GamSetting.consumeClick()){
            ModNetwork.PACKET_CHANNEL.sendToServer(new GamSettingPacket());
        }
//        if (KeyBindingHandler.count40.consumeClick()){
//            ModNetwork.PACKET_CHANNEL.sendToServer(new CountShootPacket(40));
//        }
//        if (KeyBindingHandler.count60.consumeClick()){
//            ModNetwork.PACKET_CHANNEL.sendToServer(new CountShootPacket(60));
//        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        boolean down = mc.options.keyJump.isDown();
        long now = mc.level.getGameTime();
        if (down && !prevJumpPressed) {
            // pressed edge
            if (now - lastJumpPressTick <= 7) { // double-press within ~0.35s
                // only for sets that grant glide
                if (GameEvent.isWearingFullSet(mc.player, "defender")
                        || GameEvent.isWearingFullSet(mc.player, "attacker")
                        || GameEvent.isWearingFullSet(mc.player, "armored_chemical")) {
                    ModNetwork.PACKET_CHANNEL.sendToServer(new com.qdd.taczadd.network.StartGlidePacket());
                }
                lastJumpPressTick = -1000L; // reset
            } else {
                lastJumpPressTick = now;
            }
        }
        prevJumpPressed = down;
    }
}


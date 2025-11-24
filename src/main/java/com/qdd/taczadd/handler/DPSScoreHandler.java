package com.qdd.taczadd.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.LinkedHashMap;
import java.util.Map;

public class DPSScoreHandler {
    private static Scoreboard scoreboard;
    private static Objective objective;
    private static boolean isRecording;
    private static Map<String, Integer> playerDamageMap = new LinkedHashMap<>();
    // 自定义方法：跨维度安全存取
    public static Map<String, Integer> getData() {
        return playerDamageMap;
    }

    public static boolean isRecording() {
        return isRecording;
    }

    public static void setRecording(boolean recording) {
        isRecording = recording;// 标记需要保存
    }

    public static void setData( String playername, int damage) {
        playerDamageMap.merge(playername, damage, Integer::sum);
    }
    public static void Show(MinecraftServer server){
        scoreboard=server.getScoreboard();
        playerDamageMap.clear();
        if (!scoreboard.hasObjective("DPS")){
            objective=scoreboard.addObjective("DPS", ObjectiveCriteria.DUMMY, Component.literal(ChatFormatting.GOLD+"DPS"),ObjectiveCriteria.RenderType.INTEGER);
        }
        objective=scoreboard.getObjective("DPS");
        scoreboard.setDisplayObjective(1, objective);
        server.getPlayerList().getPlayers().forEach(player -> {
            scoreboard.getOrCreatePlayerScore(ChatFormatting.AQUA+player.getName().getString(), objective).setScore(0);
            player.connection.send(
                    new ClientboundSetDisplayObjectivePacket(
                            1,
                            objective
                    )
            );
        });
        setRecording(true);
    }
    public static void Hide(MinecraftServer server){
        scoreboard=server.getScoreboard();
        scoreboard.removeObjective(objective);
        server.getPlayerList().getPlayers().forEach(player -> {
            player.connection.send(
                    new ClientboundSetDisplayObjectivePacket(
                            19,
                            objective
                    )
            );
        });
        setRecording(false);
    }
    public static void Uppdate(MinecraftServer server){
        playerDamageMap.forEach((player,damage) -> {
            scoreboard.getOrCreatePlayerScore(ChatFormatting.AQUA+player, objective).setScore(damage);
        });
    }

}

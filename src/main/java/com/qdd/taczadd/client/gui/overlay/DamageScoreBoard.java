package com.qdd.taczadd.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class DamageScoreBoard implements IGuiOverlay {
    public static final boolean ifshow=false;

    public DamageScoreBoard(){
    }
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player= Minecraft.getInstance().player;
    }
}

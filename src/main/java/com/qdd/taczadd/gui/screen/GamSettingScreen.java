package com.qdd.taczadd.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.gui.menu.GamSettingMenu;
import com.qdd.taczadd.handler.GamHandler;
import com.qdd.taczadd.item.GamItem;

import java.util.Map;

public class GamSettingScreen extends AbstractContainerScreen<GamSettingMenu> {
    private static final ResourceLocation BG=new ResourceLocation(Taczadd.MODID,"textures/gui/gam_setting.png");

    public GamSettingScreen(GamSettingMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        this.titleLabelX=100;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG,i,j,0,0,imageWidth,imageHeight);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        ItemStack stack=this.menu.getItemStack();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int h=0;
        if (!stack.isEmpty()){

            for (Map.Entry<Item,Long> e : GamHandler.getGamNum(stack).entrySet()){
                GamItem gam =(GamItem)e.getKey();
                int color=0xFFFFFF;
                if (gam.getMaxCount()==1||e.getValue()>=4){
                    float glow = 0.5f + 0.5f * Mth.sin((float)(System.currentTimeMillis() % 2000) / 2000 * Mth.TWO_PI);
                    color= Mth.hsvToRgb(0.11f, 0.9f, 0.7f + 0.3f * glow);
                } else if (e.getValue()>=2) {
                    color=0x55FF55;
                }
                guiGraphics.drawString(this.font, "✧"+gam.getGamname()+":"+e.getValue()+"/"+gam.getMaxCount(),i+100,j+9+6+h*9,color);
                h++;
            }
        }

    }
}

package com.qdd.taczadd.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.handler.AmmocCount;
import com.qdd.taczadd.handler.GunSkill;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.HashMap;

public class SkillOverlay implements IGuiOverlay {
    public SkillOverlay(){

    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player= Minecraft.getInstance().player;
        ItemStack stack=player.getMainHandItem();
        if (stack !=ItemStack.EMPTY && stack.getItem() instanceof AbstractGunItem gunItem){
            ResourceLocation gunId = gunItem.getGunId(stack);
            ResourceLocation texture = (ResourceLocation) GunSkill.Skill.getOrDefault(gunId.getPath(),new HashMap<>()).getOrDefault("skillicon",null);
            if (texture==null)return;
            guiGraphics.blit(texture,screenWidth/5*3,screenHeight/5*3,0,0,16,16,16,16);
            AmmocCount ac = new AmmocCount(stack);
            double cd = (double) (player.level().getGameTime()-stack.getOrCreateTag().getLong("cd")) /20;

            int f = 0;
            if (stack.getOrCreateTag().getFloat("multiple")>1){
                f= (int) ((stack.getOrCreateTag().getFloat("multiple")-1)*10);
            }else if(stack.getOrCreateTag().getFloat("rpmadd")>1){
                f=(int) ((stack.getOrCreateTag().getFloat("rpmadd")-1)*10);
            }
            if(f>0&&cd<8&&(int)GunSkill.Skill.get(gunId.getPath()).getOrDefault("cd",-1)>0){
                guiGraphics.drawString(gui.getFont(),String.valueOf(f),screenWidth/5*3+16,screenHeight/5*3+13,0x00ff00);
            }
            if(cd>0&&cd<5&&(int)GunSkill.Skill.get(gunId.getPath()).getOrDefault("cd",-1)>0){
                guiGraphics.fill(screenWidth/5*3, (int) (screenHeight/5*3+(16*cd/5)),screenWidth/5*3+16, screenHeight/5*3+16 ,0x60_f0f0f0);
                guiGraphics.drawCenteredString(gui.getFont(),String.valueOf((int)(5-cd)),screenWidth/5*3+8,screenHeight/5*3+5,0xf0f0f0);
            }
            String s= ac.maxcount1>0?ac.count1+"/"+ac.maxcount1:"∞/∞";
            guiGraphics.drawCenteredString(gui.getFont(),s,screenWidth/5*3+8,screenHeight/5*3+18,ac.shouldSkill1?0x00ff00:0xffffff);
            if (gunId.getPath().equals("zmzy")){
                ResourceLocation texture2 = (ResourceLocation) GunSkill.Skill.get(gunId.getPath()).get("skillicon2");
                guiGraphics.blit(texture2,screenWidth/5*3-30,screenHeight/5*3,0,0,16,16,16,16);
                guiGraphics.drawCenteredString(gui.getFont(),ac.count2+"/"+ac.maxcount2,screenWidth/5*3-22,screenHeight/5*3+18,ac.shouldSkill2?0x00ff00:0xffffff);
            }
            if (gunId.getPath().equals("icedragon")){
                ResourceLocation texture3 = (ResourceLocation) GunSkill.Skill.get(gunId.getPath()).get("skillicon3");
                guiGraphics.blit(texture3,screenWidth/5*3,screenHeight/5*3+30,0,0,16,16,16,16);
                guiGraphics.drawCenteredString(gui.getFont(),ac.count3+"/"+ac.maxcount3,screenWidth/5*3+8,screenHeight/5*3+48,ac.shouldSkill3?0x00ff00:0xffffff);
                ResourceLocation texture4 = (ResourceLocation) GunSkill.Skill.get(gunId.getPath()).get("skillicon4");
                guiGraphics.blit(texture4,screenWidth/5*3-30,screenHeight/5*3+30,0,0,16,16,16,16);
                guiGraphics.drawCenteredString(gui.getFont(),ac.count4+"/"+ac.maxcount4,screenWidth/5*3-22,screenHeight/5*3+48,ac.shouldSkill4?0x00ff00:0xffffff);

            }
            if (gunId.getPath().equals("pozhanzhe")){
                ResourceLocation texture2 = (ResourceLocation) GunSkill.Skill.get(gunId.getPath()).get("skillicon2");
                guiGraphics.blit(texture2,screenWidth/5*3-30,screenHeight/5*3,0,0,16,16,16,16);
                guiGraphics.drawCenteredString(gui.getFont(),ac.count2+"/"+ac.maxcount2,screenWidth/5*3-22,screenHeight/5*3+18,ac.shouldSkill2?0x00ff00:0xffffff);
            }

        }
    }
}

package com.qdd.taczadd.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.gui.menu.ReinforcedMenu;
import com.qdd.taczadd.item.ReinforcedCrystal;
import com.qdd.taczadd.network.ModNetwork;
import com.qdd.taczadd.network.ReinforcedPacket;

public class ReinforcedScreen extends AbstractContainerScreen<ReinforcedMenu> {
    private static final ResourceLocation BG=new ResourceLocation(Taczadd.MODID,"textures/gui/reinforced.png");
    private Button button;

    public ReinforcedScreen(ReinforcedMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Override
    public void init() {
        super.init();
        button =new Button.Builder(Component.translatable("gui.taczadd.reinforced.button"), (p_97797_) -> {
            ModNetwork.PACKET_CHANNEL.sendToServer(new ReinforcedPacket());
        })
                .pos((this.width-this.imageWidth)/2+120,(this.height-this.imageHeight)/2+30)
                .size(50,20)
                .build();
        this.addRenderableWidget(button);
    }
    @Override
    public void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG,i,j,0,0,imageWidth,imageHeight);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        button.active=!this.getMenu().getItemStack().isEmpty();
        button.setTooltip(button.active?Tooltip.create(((ReinforcedCrystal)this.getMenu().stack.getItem()).getGuitip(this.getMenu().getItemStack())):null);
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        // crystal icon only; detailed bonus breakdown moved to item tooltip per requirement
        int left = (this.width-this.imageWidth)/2;
        int top = (this.height-this.imageHeight)/2;
        guiGraphics.renderItem(this.getMenu().stack, left+90, top+30);
    }
}

package com.qdd.taczadd.gui.screen;

import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.gui.menu.ActivationMenu;
import com.qdd.taczadd.network.ModNetwork;
import com.qdd.taczadd.network.ActivationPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 
 * 										Core Activation Screen (reuse reinforced GUI)
 */
public class ActivationScreen extends AbstractContainerScreen<ActivationMenu> {
    private static final ResourceLocation BG = new ResourceLocation(Taczadd.MODID, "textures/gui/reinforced.png");
    private Button button;

    public ActivationScreen(ActivationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        button = new Button.Builder(Component.translatable("gui.taczadd.activate.button"), (b) -> {
            ModNetwork.PACKET_CHANNEL.sendToServer(new ActivationPacket());
        })
                .pos((this.width - this.imageWidth) / 2 + 100, (this.height - this.imageHeight) / 2 + 30)
                .size(50, 20)
                .build();
        this.addRenderableWidget(button);
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG, i, j, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // 									Button active when player put modules
        button.active = !this.getMenu().getItemStack().isEmpty();
        button.setTooltip(button.active ? Tooltip.create(Component.translatable("gui.taczadd.activate.tooltip")) : null);
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        // no extra item render to avoid placeholder texture
    }
}


package com.qdd.taczadd.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.gui.menu.UpgradeMenu;
import com.qdd.taczadd.item.UpgradeCore;
import com.qdd.taczadd.network.ModNetwork;
import com.qdd.taczadd.network.UpgradePacket;

/**
 * 枪械升级界面（复用强化界面的设计）
 */
public class UpgradeScreen extends AbstractContainerScreen<UpgradeMenu> {
    private static final ResourceLocation BG = new ResourceLocation(Taczadd.MODID, "textures/gui/reinforced.png");
    private Button button;

    public UpgradeScreen(UpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        button = new Button.Builder(Component.translatable("gui.taczadd.upgrade.button"), (p_97797_) -> {
            ModNetwork.PACKET_CHANNEL.sendToServer(new UpgradePacket());
        })
                .pos((this.width - this.imageWidth) / 2 + 120, (this.height - this.imageHeight) / 2 + 30)
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
        button.active = !this.getMenu().getItemStack().isEmpty();
        button.setTooltip(button.active ? Tooltip.create(((UpgradeCore) this.getMenu().stack.getItem()).getGuitip(this.getMenu().getItemStack())) : null);
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        guiGraphics.renderItem(this.getMenu().stack, (this.width - this.imageWidth) / 2 + 90, (this.height - this.imageHeight) / 2 + 30);
    }
}

package com.qdd.taczadd.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.gui.menu.InducerMenu;
import com.qdd.taczadd.network.ModNetwork;
import com.qdd.taczadd.network.InducerPacket;

/**
 * 装备诱导器界面
 */
public class InducerScreen extends AbstractContainerScreen<InducerMenu> {
    private static final ResourceLocation BG = new ResourceLocation(Taczadd.MODID, "textures/gui/reinforced.png");
    private Button button;

    public InducerScreen(InducerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        button = new Button.Builder(Component.translatable("gui.taczadd.inducer.button"), (p_97797_) -> {
            ModNetwork.PACKET_CHANNEL.sendToServer(new InducerPacket());
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
        // 检查槽位中是否有装备或枪械
        boolean hasItem = !this.getMenu().getItemStack().isEmpty() && 
                         (this.getMenu().getItemStack().getItem() instanceof ArmorItem || 
                          this.getMenu().getItemStack().getItem() instanceof com.tacz.guns.api.item.gun.AbstractGunItem);
        button.active = hasItem;
        
        // 设置工具提示
        if (button.active) {
            boolean alreadyInduced = this.getMenu().getItemStack().getOrCreateTag().getBoolean("armor_induced");
            if (alreadyInduced) {
                button.setTooltip(Tooltip.create(Component.translatable("gui.taczadd.inducer.already_induced")));
            } else {
                button.setTooltip(Tooltip.create(Component.translatable("gui.taczadd.inducer.tooltip")));
            }
        } else {
            button.setTooltip(Tooltip.create(Component.translatable("gui.taczadd.inducer.no_item")));
        }
        
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}


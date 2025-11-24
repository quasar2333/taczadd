package com.qdd.taczadd.gui.menu;

import com.qdd.taczadd.gui.ModMenuType;
import com.qdd.taczadd.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 核心激活菜单：放入20个通用枪械进阶模块以激活
 */
public class ActivationMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final Container itemSlots = new SimpleContainer(1);
    public final ItemStack stack; // 当前手中的核心物品
    private final Inventory inv;

    public ActivationMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, extraData.readItem(), new SimpleContainerData(1));
    }

    public ActivationMenu(int containerId, Inventory inv, ItemStack stack, ContainerData data) {
        super(ModMenuType.ACTIVATION_MENU.get(), containerId);
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.inv = inv;
        this.stack = stack;
        // 插槽：仅能放入通用枪械进阶模块
        this.addSlot(new Slot(itemSlots, 0, 26, 30) {
            @Override
            public boolean mayPlace(ItemStack s) {
                return s.getItem() == ModItems.GeneralUpgradeModule.get();
            }
            @Override
            public int getMaxStackSize() {
                return 64;
            }
        });
        addDataSlots(data);
    }

    public ItemStack getItemStack() {
        return this.itemSlots.getItem(0);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) { // 激活按钮
            // 检查是否已激活
            if (this.stack.getOrCreateTag().getBoolean("activated")) {
                player.sendSystemMessage(Component.translatable("message.taczadd.activate.already"));
                return true;
            }
            ItemStack modules = getItemStack();
            if (!modules.isEmpty() && modules.getItem() == ModItems.GeneralUpgradeModule.get() && modules.getCount() >= 20) {
                // 扣除材料
                modules.shrink(20);
                // 设置核心为已激活
                this.stack.getOrCreateTag().putBoolean("activated", true);
                player.sendSystemMessage(Component.translatable("message.taczadd.activate.success"));
                this.slotsChanged(this.itemSlots);
            } else {
                player.sendSystemMessage(Component.translatable("message.taczadd.activate.lack"));
            }
        }
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!getItemStack().isEmpty()) {
            player.getInventory().placeItemBackInInventory(this.itemSlots.getItem(0));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        boolean flag = false;
        for (int i = 0; i < this.inv.getContainerSize(); i++) {
            if (this.inv.getItem(i).getItem() == this.stack.getItem()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}


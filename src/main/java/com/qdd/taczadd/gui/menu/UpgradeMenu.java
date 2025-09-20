package com.qdd.taczadd.gui.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.gui.ModMenuType;
import com.qdd.taczadd.handler.UpgradeHandler;
import com.qdd.taczadd.item.UpgradeCore;
import com.tacz.guns.api.item.gun.AbstractGunItem;

/**
 * 枪械升级菜单（复用强化菜单的设计）
 */
public class UpgradeMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final Container ItemSlots = new SimpleContainer(1);
    public final ItemStack stack;
    private final Inventory inv;

    public UpgradeMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, extraData.readItem(), new SimpleContainerData(1));
    }

    public UpgradeMenu(int containerId, Inventory inv, ItemStack stack, ContainerData data) {
        super(ModMenuType.UPGRADE_MENU.get(), containerId);
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.inv = inv;
        this.stack = stack;
        this.addSlot(new Slot(ItemSlots, 0, 26, 30) {
            public boolean mayPlace(ItemStack stack) {
                return UpgradeMenu.this.stack.getItem() instanceof UpgradeCore uc && uc.mayPlace(stack);
            }
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                super.setByPlayer(stack);
                UpgradeMenu.this.slotsChanged(this.container);
            }
        });
        addDataSlots(data);
    }

    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            ItemStack gunStack = getItemStack();
            if (!gunStack.isEmpty() && stack.getItem() instanceof UpgradeCore core) {
                // 执行升级
                ItemStack upgradedGun = UpgradeHandler.performUpgrade(stack, gunStack);
                if (!upgradedGun.isEmpty()) {
                    // 替换枪械
                    ItemSlots.setItem(0, upgradedGun);
                    player.sendSystemMessage(Component.translatable("message.taczadd.upgrade.success"));
                } else {
                    player.sendSystemMessage(Component.translatable("message.taczadd.upgrade.failed"));
                }

                // 消耗核心物品
                for (int i = 0; i < this.inv.getContainerSize(); i++) {
                    if (this.inv.getItem(i).getItem() == this.stack.getItem()) {
                        this.inv.getItem(i).shrink(1);
                        break;
                    }
                }
                slotsChanged(this.ItemSlots);
            }
        }
        return true;
    }

    public ItemStack getItemStack() {
        return this.ItemSlots.getItem(0);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!getItemStack().isEmpty()) {
            player.getInventory().placeItemBackInInventory(this.ItemSlots.getItem(0));
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
        return (UpgradeMenu.this.stack.getItem() instanceof UpgradeCore uc && uc.mayPlace(getItemStack()) || getItemStack().isEmpty()) && flag;
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

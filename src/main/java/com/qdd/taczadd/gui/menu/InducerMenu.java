package com.qdd.taczadd.gui.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.gui.ModMenuType;
import com.tacz.guns.api.item.gun.AbstractGunItem;

/**
 * 装备诱导器菜单
 */
public class InducerMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final Container ItemSlots = new SimpleContainer(1);
    public final ItemStack stack; // 装备诱导器
    private final Inventory inv;

    public InducerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, extraData.readItem(), new SimpleContainerData(1));
    }

    public InducerMenu(int containerId, Inventory inv, ItemStack stack, ContainerData data) {
        super(ModMenuType.INDUCER_MENU.get(), containerId);
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.inv = inv;
        this.stack = stack;
        // 插槽：可以放入盔甲或枪械
        this.addSlot(new Slot(ItemSlots, 0, 26, 30) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ArmorItem || stack.getItem() instanceof AbstractGunItem;
            }
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                super.setByPlayer(stack);
                InducerMenu.this.slotsChanged(this.container);
            }
        });
        addDataSlots(data);
    }

    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            ItemStack targetStack = getItemStack();
            // 检查是否为盔甲或枪械
            boolean isArmor = targetStack.getItem() instanceof ArmorItem;
            boolean isGun = targetStack.getItem() instanceof AbstractGunItem;
            
            if (!targetStack.isEmpty() && (isArmor || isGun)) {
                // 检查是否已经被诱导过
                if (targetStack.getOrCreateTag().getBoolean("armor_induced")) {
                    player.sendSystemMessage(Component.translatable("message.taczadd.inducer.already_induced").withStyle(ChatFormatting.YELLOW));
                    return true;
                }
                
                // 1%概率成功
                boolean success = player.getRandom().nextFloat() < 0.01f;
                
                if (success) {
                    // 诱导成功
                    targetStack.getOrCreateTag().putBoolean("armor_induced", true);
                    String messageKey = isGun ? "message.taczadd.inducer.success.gun" : "message.taczadd.inducer.success";
                    player.sendSystemMessage(Component.translatable(messageKey).withStyle(ChatFormatting.GREEN));
                } else {
                    // 诱导失败
                    player.sendSystemMessage(Component.translatable("message.taczadd.inducer.failed").withStyle(ChatFormatting.YELLOW));
                }
                
                // 消耗诱导器
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


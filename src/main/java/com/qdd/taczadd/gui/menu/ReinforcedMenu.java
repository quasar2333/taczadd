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
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.gui.ModMenuType;
import com.qdd.taczadd.sound.ModSounds;

import com.qdd.taczadd.item.ReinforcedCrystal;

public class ReinforcedMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final Container ItemSlots = new SimpleContainer(1);
    public final ItemStack stack;
    private final Inventory inv;

    public ReinforcedMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv,extraData.readItem(),new SimpleContainerData(1));
    }
    public ReinforcedMenu(int pContainerId, Inventory inv, ItemStack stack, ContainerData data) {
        super(ModMenuType.Reinforced_MENU.get(), pContainerId);
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.inv=inv;
        this.stack=stack;
        this.addSlot(new Slot(ItemSlots,0,26,30){
            public boolean mayPlace(ItemStack stack) {
                return ReinforcedMenu.this.stack.getItem() instanceof ReinforcedCrystal rc && rc.mayPlace(stack);
            }
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                super.setByPlayer(stack);
                ReinforcedMenu.this.slotsChanged(this.container);
            }
        });
        addDataSlots(data);
    }
    public boolean clickMenuButton(Player player, int id) {
        if (id==0){
            int reinforced=getItemStack().getOrCreateTag().getInt("reinforced");
            int reinforced_count=getItemStack().getOrCreateTag().getInt("reinforced_count");
            int max=ReinforcedCrystal.getProbability(reinforced).get(1).intValue();
            if (reinforced_count+1==max || player.getRandom().nextFloat()<=ReinforcedCrystal.getProbability(reinforced).get(0)){
                getItemStack().getOrCreateTag().putInt("reinforced_count",0);
                getItemStack().getOrCreateTag().putInt("reinforced",reinforced+1);
                ((ReinforcedCrystal)stack.getItem()).up(getItemStack());
                if (player.level().isClientSide) {
                    player.playSound(ModSounds.REINFORCE_SUCCESS.get(), 1.0f, 1.0f);
                } else {
                    ((net.minecraft.server.level.ServerPlayer) player).playNotifySound(ModSounds.REINFORCE_SUCCESS.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                }
                player.sendSystemMessage(Component.translatable("tooltip.reinforced.success").withStyle(ChatFormatting.GREEN));
            }else {
                if (player.level().isClientSide) {
                    player.playSound(ModSounds.REINFORCE_FAIL.get(), 1.0f, 1.0f);
                } else {
                    ((net.minecraft.server.level.ServerPlayer) player).playNotifySound(ModSounds.REINFORCE_FAIL.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                }
                player.sendSystemMessage(Component.translatable("tooltip.reinforced.false").withStyle(ChatFormatting.YELLOW));
                getItemStack().getOrCreateTag().putInt("reinforced_count",getItemStack().getOrCreateTag().getInt("reinforced_count")+1);
            }
            for (int i = 0; i < this.inv.getContainerSize(); i++) {
                if (this.inv.getItem(i).getItem()==this.stack.getItem()){
                    this.inv.getItem(i).shrink(1);
                    break;
                }
            }
            slotsChanged(this.ItemSlots);
        }
        return true;
    }

    public ItemStack getItemStack(){
        return this.ItemSlots.getItem(0);
    }

    @Override
    public void removed(Player p_38940_) {
        super.removed(p_38940_);
        if (!getItemStack().isEmpty()){
            p_38940_.getInventory().placeItemBackInInventory(this.ItemSlots.getItem(0));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player Player) {
        boolean flag =false;
        for (int i = 0; i < this.inv.getContainerSize(); i++) {
            if (this.inv.getItem(i).getItem()==this.stack.getItem()){
                flag=true;
                break;
            }
        }
        return (ReinforcedMenu.this.stack.getItem() instanceof ReinforcedCrystal rc && rc.mayPlace(getItemStack())||getItemStack().isEmpty())&&flag;
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

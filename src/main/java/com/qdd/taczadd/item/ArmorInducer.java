package com.qdd.taczadd.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import com.qdd.taczadd.gui.menu.InducerMenu;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * 装备诱导器：诱导装备强化方向的道具
 * 使用后有1%几率诱导成功，成功后装备在强化时有90%概率增加枪械伤害
 */
public class ArmorInducer extends Item implements MenuProvider {
    
    public ArmorInducer(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, this, friendlyByteBuf -> friendlyByteBuf.writeItem(itemstack));
        }
        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        Arrays.stream(Component.translatable("tooltip.taczadd.armor_inducer").getString().split("\n"))
                .forEach(s -> list.add(Component.literal(s).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.taczadd.armor_inducer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new InducerMenu(id, inventory, this.getDefaultInstance(), new SimpleContainerData(1));
    }
}


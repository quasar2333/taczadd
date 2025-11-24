package com.qdd.taczadd.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import com.qdd.taczadd.gui.menu.UpgradeMenu;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 枪械进阶核心基类
 * 用于将前置枪械升级为目标枪械的核心物品
 */
public class UpgradeCore extends Item implements MenuProvider {
    private final ResourceLocation sourceGunId;
    private final ResourceLocation targetGunId;
    private final String coreName;

    public UpgradeCore(Properties properties, String coreName, String sourceGun, String targetGun) {
        super(properties);
        this.coreName = coreName;
        // 根据枪械名称确定正确的命名空间
        this.sourceGunId = getGunResourceLocation(sourceGun);
        this.targetGunId = getGunResourceLocation(targetGun);
    }

    /**
     * 根据枪械名称获取正确的ResourceLocation
     */
    private ResourceLocation getGunResourceLocation(String gunName) {
        // IFCD 枪械包
        if (gunName.equals("ak47gm") || gunName.equals("augfly") || gunName.equals("enddragon") ||
            gunName.equals("ak117jxdf") || gunName.equals("ak117jxdf_a") || gunName.equals("ak117jxdf_b") ||
            gunName.equals("ak24") || gunName.equals("pozhanzhe") || gunName.equals("zmzy")) {
            return new ResourceLocation("ifcd", gunName);
        }
        // Applied Armorer 枪械包
        if (gunName.equals("moritz_shotgun_sg914") || gunName.equals("moritz_sniper_semi_k30")) {
            return new ResourceLocation("applied_armorer", gunName);
        }
        // 默认 TACZ 枪械
        return new ResourceLocation("tacz", gunName);
    }

    /**
     * 右键使用核心打开升级界面
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean activated = isActivated(stack);
            // 根据激活状态打开不同界面
            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable(activated ? "menu.taczadd.upgrade_core" : "menu.taczadd.activate_core", coreName);
                }
                @Override
                public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, Player p) {
                    if (activated) {
                        return new com.qdd.taczadd.gui.menu.UpgradeMenu(id, inv, stack, new net.minecraft.world.inventory.SimpleContainerData(1));
                    } else {
                        return new com.qdd.taczadd.gui.menu.ActivationMenu(id, inv, stack, new net.minecraft.world.inventory.SimpleContainerData(1));
                    }
                }
            };
            NetworkHooks.openScreen((ServerPlayer) player, provider, buf -> buf.writeItem(stack));
        }
        return InteractionResultHolder.success(stack);
    }

    /**
     * 获取前置枪械ID
     */
    public ResourceLocation getSourceGunId() {
        return sourceGunId;
    }

    /**
     * 获取目标枪械ID
     */
    public ResourceLocation getTargetGunId() {
        return targetGunId;
    }

    /**
     * 获取核心名称
     */
    public String getCoreName() {
        return coreName;
    }

    /**
     * 检查是否可以升级指定的枪械（类似ReinforcedCrystal的mayPlace方法）
     */
    private boolean isActivated(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("activated");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActivated(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack))
                .append(!isActivated(stack) ? Component.translatable("core.unactivated") : Component.empty());
    }

    public boolean mayPlace(ItemStack gunStack) {
        if (!(gunStack.getItem() instanceof AbstractGunItem gunItem)) {
            return false;
        }
        ResourceLocation gunId = gunItem.getGunId(gunStack);
        return sourceGunId.equals(gunId);
    }

    /**
     * 检查是否可以升级指定的枪械
     */
    public boolean canUpgrade(ItemStack gunStack) {
        return mayPlace(gunStack);
    }

    /**
     * 添加工具提示信息
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        boolean activated = isActivated(stack);
        tooltip.add(Component.translatable("tooltip.taczadd.upgrade_core").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(activated ? "tooltip.taczadd.upgrade_core.state.active" : "tooltip.taczadd.upgrade_core.state.inactive").withStyle(activated?ChatFormatting.AQUA:ChatFormatting.RED));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tooltip.taczadd.upgrade_core.source",
            Component.translatable("gun." + sourceGunId.getNamespace() + "." + sourceGunId.getPath()))
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.taczadd.upgrade_core.target",
            Component.translatable("gun." + targetGunId.getNamespace() + "." + targetGunId.getPath()))
            .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal(""));
        if (!activated) {
            tooltip.add(Component.translatable("tooltip.taczadd.upgrade_core.activate_hint").withStyle(ChatFormatting.YELLOW));
        } else {
            tooltip.add(Component.translatable("tooltip.taczadd.upgrade_core.usage").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.taczadd.upgrade_core", coreName);
    }

    /**
     * 获取升级提示信息（类似ReinforcedCrystal的getGuitip方法）
     */
    public Component getGuitip(ItemStack gunStack) {
        if (gunStack.isEmpty()) {
            return Component.translatable("gui.taczadd.upgrade.tooltip.no_gun");
        }

        if (!canUpgrade(gunStack)) {
            return Component.translatable("gui.taczadd.upgrade.tooltip.invalid_gun");
        }

        return Component.translatable("gui.taczadd.upgrade.tooltip",
            Component.translatable("gun." + targetGunId.getNamespace() + "." + targetGunId.getPath()));
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId,
            net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
        return new UpgradeMenu(containerId, playerInventory, player.getItemInHand(player.getUsedItemHand()),
            new net.minecraft.world.inventory.SimpleContainerData(1));
    }
}

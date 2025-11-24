package com.qdd.taczadd.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import com.qdd.taczadd.gui.menu.ReinforcedMenu;
import com.qdd.taczadd.item.Attributes.ModAttributes;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ReinforcedCrystal extends Item implements MenuProvider {
    public final Type type;
    public final Rank rank;

    public ReinforcedCrystal(Properties properties,Type type, Rank rank) {
        super(properties);
        this.type = type;
        this.rank = rank;
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, this,friendlyByteBuf -> friendlyByteBuf.writeItem(itemstack));
        }
        return InteractionResultHolder.success(itemstack);
    }
    public void up(ItemStack stack){
        if (type==Type.Gun){
            double damagebase=stack.getOrCreateTag().getDouble("damagebase");
            if (rank==Rank.Low){
                stack.getOrCreateTag().putDouble("damagebase",damagebase+0.15d);
            }else if (rank==Rank.Mid){
                stack.getOrCreateTag().putDouble("damagebase",damagebase+0.2d);
            }
        } else if (type==Type.Armor) {
            if (!stack.getOrCreateTag().contains("AttributeModifiers", 9)){
                stack.getItem().getAttributeModifiers(LivingEntity.getEquipmentSlotForItem(stack), stack).forEach(
                        (a,b)->stack.addAttributeModifier(a,b, LivingEntity.getEquipmentSlotForItem(stack))
                );
            }
            // 检查是否被诱导过，如果有诱导标记则使用90%概率选择枪械伤害，否则50%
            boolean isInduced = stack.getOrCreateTag().getBoolean("armor_induced");
            float threshold = isInduced ? 0.1f : 0.5f; // 诱导后10%选伤害减免（90%选枪械伤害），否则50/50
            
            if (new Random().nextFloat()>threshold){
                // 选择枪械伤害
                if (rank==Rank.Low){
                    stack.addAttributeModifier(ModAttributes.GunDamage.get(), new AttributeModifier("gundamage",0.05d, AttributeModifier.Operation.MULTIPLY_BASE), LivingEntity.getEquipmentSlotForItem(stack));
                }else if (rank==Rank.Mid){
                    stack.addAttributeModifier(ModAttributes.GunDamage.get(), new AttributeModifier("gundamage",0.10d, AttributeModifier.Operation.MULTIPLY_BASE), LivingEntity.getEquipmentSlotForItem(stack));
                }
            }else {
                // 选择伤害减免
                if (rank==Rank.Low){
                    stack.addAttributeModifier(ModAttributes.DamageReduction.get(), new AttributeModifier("damagereduction",1d, AttributeModifier.Operation.ADDITION), LivingEntity.getEquipmentSlotForItem(stack));
                }else if (rank==Rank.Mid){
                    stack.addAttributeModifier(ModAttributes.DamageReduction.get(), new AttributeModifier("damagereduction",2d, AttributeModifier.Operation.ADDITION), LivingEntity.getEquipmentSlotForItem(stack));
                }
            }
            ListTag lt=stack.getOrCreateTag().getList("AttributeModifiers", 10);
            ListTag newlt=new ListTag();
            lt.forEach(tag ->  {
                if (newlt.isEmpty()||(!((CompoundTag)tag).getString("Name").equals("damagereduction")&&!((CompoundTag)tag).getString("Name").equals("gundamage"))){
                    newlt.add(tag);
                }else{
                    boolean flag=false;
                    for (Tag nt : newlt) {
                        if (((CompoundTag) tag).getString("Name").equals(((CompoundTag) nt).getString("Name"))) {
                            ((CompoundTag) nt).putDouble("Amount", ((CompoundTag) nt).getDouble("Amount") + ((CompoundTag) tag).getDouble("Amount"));
                            flag = true;
                        }
                    }
                    if (!flag) {
                        newlt.add(tag);
                    }
                }
            });
            stack.getOrCreateTag().put("AttributeModifiers",newlt);

        }
    }


    public boolean mayPlace(ItemStack stack){
        if (this.rank==Rank.Low&&stack.getOrCreateTag().getInt("reinforced")>=13&&stack.getOrCreateTag().getInt("reinforced")<20){
            return false;
        } else if (this.rank==Rank.Mid&&stack.getOrCreateTag().getInt("reinforced")<13) {
            return false;
        }
        if (this.type==Type.Armor){
            return stack.getItem() instanceof ArmorItem;
        }else if (this.type==Type.Gun){
            return stack.getItem() instanceof AbstractGunItem;
        }
        return false;
    }

    public static List<Double> getProbability(int reinforced){
        return switch (reinforced) {
                    case 0, 13 -> List.of(1d,1d);
                    case 1 -> List.of(0.5d,2d);
                    case 2, 14 -> List.of(0.25d,4d);
                    case 3 -> List.of(0.12,8d);
                    case 4, 15 -> List.of(0.06,16d);
                    case 5 -> List.of(0.03,32d);
                    case 6, 16 -> List.of(0.015,64d);
                    case 7 -> List.of(0.01,128d);
                    case 8, 17 -> List.of(0.005,256d);
                    case 9 -> List.of(0.002,256d);
                    case 10, 18 -> List.of(0.001,512d);
                    case 11 -> List.of(0.0005,1024d);
                    case 12, 19 -> List.of(0.00025,2048d);
                    default -> List.of(0d,0d);
                };
    }

    public Component getGuitip(ItemStack stack){
        int reinforced = stack.getOrCreateTag().getInt("reinforced");
        List<Double> probability = getProbability(reinforced);
        String s = switch (this.type) {
            case Gun -> Component.translatable("gui.taczadd.reinforced.guntip",this.rank==Rank.Low?"15%":"20%").getString();
            case Armor -> Component.translatable("gui.taczadd.reinforced.armortip",this.rank==Rank.Low?"1":"2",this.rank==Rank.Low?"5%":"10%").getString();
        };
        return Component.translatable("gui.taczadd.reinforced.tip",s,reinforced+1, probability.get(0)*100+"%",stack.getOrCreateTag().getInt("reinforced_count"),probability.get(1).intValue());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        String key = this.type == Type.Gun ? "tooltip.gam.reinforced_crystal_gun" : "tooltip.gam.reinforced_crystal_armor";
        Arrays.stream(Component.translatable(key).getString().split("\n")).forEach(s -> list.add(Component.literal(s)));
        // show a concise per-rank effect hint
        if (this.type == Type.Gun) {
            String eff = this.rank == Rank.Low ? "15%" : "20%";
            list.add(Component.translatable("gui.taczadd.reinforced.guntip", eff));
        } else {
            String points = this.rank == Rank.Low ? "1" : "2";
            String rate = this.rank == Rank.Low ? "5%" : "10%";
            list.add(Component.translatable("gui.taczadd.reinforced.armortip", points, rate));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ReinforcedMenu(id, inventory,this.getDefaultInstance(), new SimpleContainerData(1));
    }


    public enum Type {
        Gun,Armor
    }
    public enum Rank{
        Low,
        Mid
    }
}

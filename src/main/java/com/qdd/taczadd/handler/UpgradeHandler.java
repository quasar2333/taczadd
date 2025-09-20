package com.qdd.taczadd.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import com.qdd.taczadd.item.UpgradeCore;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.api.TimelessAPI;

/**
 * 枪械升级处理器
 * 负责处理枪械升级的核心逻辑
 */
public class UpgradeHandler {
    
    /**
     * 检查是否可以进行升级
     * @param coreStack 升级核心物品
     * @param gunStack 要升级的枪械
     * @return 是否可以升级
     */
    public static boolean canUpgrade(ItemStack coreStack, ItemStack gunStack) {
        if (!(coreStack.getItem() instanceof UpgradeCore core)) {
            return false;
        }
        
        if (!(gunStack.getItem() instanceof AbstractGunItem gunItem)) {
            return false;
        }
        
        ResourceLocation gunId = gunItem.getGunId(gunStack);
        return core.getSourceGunId().equals(gunId);
    }
    
    /**
     * 执行枪械升级
     * @param coreStack 升级核心物品
     * @param gunStack 要升级的枪械
     * @return 升级后的枪械，如果升级失败返回空物品栈
     */
    public static ItemStack performUpgrade(ItemStack coreStack, ItemStack gunStack) {
        if (!canUpgrade(coreStack, gunStack)) {
            return ItemStack.EMPTY;
        }

        UpgradeCore core = (UpgradeCore) coreStack.getItem();
        ResourceLocation targetGunId = core.getTargetGunId();

        // 直接复制现有枪械栈并切换 gunId（TACZ 枪械为统一物品 + NBT gunId）
        ItemStack newGunStack = gunStack.copy();
        if (newGunStack.getItem() instanceof AbstractGunItem newGunItem) {
            try {
                // 优先使用 API（若存在）
                newGunItem.setGunId(newGunStack, targetGunId);
            } catch (Throwable ignore) {
                // 回退：写入通用 NBT 键（不同版本可能为 gun_id 或 id）
                newGunStack.getOrCreateTag().putString("gun_id", targetGunId.toString());
                newGunStack.getOrCreateTag().putString("id", targetGunId.toString());
            }
        }

        // 保留原枪械的重要数据（再次覆盖确保不丢失）
        preserveGunData(gunStack, newGunStack);

        return newGunStack;
    }
    
    /**
     * 保留枪械的重要数据
     * @param sourceGun 源枪械
     * @param targetGun 目标枪械
     */
    private static void preserveGunData(ItemStack sourceGun, ItemStack targetGun) {
        CompoundTag sourceTag = sourceGun.getOrCreateTag();
        CompoundTag targetTag = targetGun.getOrCreateTag();
        
        // 保留宝石效果
        if (sourceTag.contains("GemEffects")) {
            targetTag.put("GemEffects", sourceTag.getCompound("GemEffects").copy());
        }
        
        // 保留强化等级
        if (sourceTag.contains("reinforced")) {
            targetTag.putInt("reinforced", sourceTag.getInt("reinforced"));
        }
        if (sourceTag.contains("reinforced_count")) {
            targetTag.putInt("reinforced_count", sourceTag.getInt("reinforced_count"));
        }
        if (sourceTag.contains("damagebase")) {
            targetTag.putDouble("damagebase", sourceTag.getDouble("damagebase"));
        }
        
        // 保留技能计数
        if (sourceTag.contains("AmmocCount")) {
            targetTag.put("AmmocCount", sourceTag.getCompound("AmmocCount").copy());
        }
        
        // 保留冷却时间
        if (sourceTag.contains("cd")) {
            targetTag.putLong("cd", sourceTag.getLong("cd"));
        }
        
        // 保留其他重要数据
        if (sourceTag.contains("multiple")) {
            targetTag.putFloat("multiple", sourceTag.getFloat("multiple"));
        }
        if (sourceTag.contains("rpmadd")) {
            targetTag.putFloat("rpmadd", sourceTag.getFloat("rpmadd"));
        }
        
        // 保留弹药数据
        if (sourceTag.contains("ammocount")) {
            targetTag.putInt("ammocount", sourceTag.getInt("ammocount"));
        }
        
        // 保留自定义名称
        if (sourceGun.hasCustomHoverName()) {
            targetGun.setHoverName(sourceGun.getHoverName());
        }
        
        // 保留附魔
        if (sourceTag.contains("Enchantments")) {
            targetTag.put("Enchantments", sourceTag.getList("Enchantments", 10).copy());
        }
        
        // 保留属性修饰符
        if (sourceTag.contains("AttributeModifiers")) {
            targetTag.put("AttributeModifiers", sourceTag.getList("AttributeModifiers", 10).copy());
        }
    }
    
    /**
     * 获取升级成功率
     * @param coreStack 升级核心
     * @param gunStack 枪械
     * @return 成功率 (0.0 - 1.0)
     */
    public static double getUpgradeSuccessRate(ItemStack coreStack, ItemStack gunStack) {
        // 基础成功率为100%，后续可以根据需要调整
        return 1.0;
    }
    
    /**
     * 计算升级消耗
     * @param coreStack 升级核心
     * @param gunStack 枪械
     * @return 是否消耗核心物品
     */
    public static boolean shouldConsumeCore(ItemStack coreStack, ItemStack gunStack) {
        // 默认消耗核心物品
        return true;
    }
}

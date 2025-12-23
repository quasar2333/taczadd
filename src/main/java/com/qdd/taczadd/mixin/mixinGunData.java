package com.qdd.taczadd.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.handler.GamHandler;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GunData.class)
public class mixinGunData {
    @Inject(method = "getShootInterval" ,at=@At("RETURN"),remap = false,cancellable = true)
    public void mgetShootInterval(LivingEntity shooter, FireMode fireMode, ItemStack gunStack, CallbackInfoReturnable<Long> cir) {
        long re =cir.getReturnValue();
        float rpmadd=Math.max(gunStack.getOrCreateTag().getFloat("rpmadd"),1);
        // 增强鲁棒性：确保 GemEffects 存在
        CompoundTag gemEffects = gunStack.getOrCreateTag().getCompound("GemEffects");
        if (gemEffects.isEmpty() && !GamHandler.getGams(gunStack).isEmpty()) {
            try {
                GamHandler.applygam(gunStack);
                gemEffects = gunStack.getOrCreateTag().getCompound("GemEffects");
            } catch (Exception ignored) {}
        }
        rpmadd*=gemEffects.getFloat("rpmgam")+1;
        cir.setReturnValue((long) (re/rpmadd));
    }
}

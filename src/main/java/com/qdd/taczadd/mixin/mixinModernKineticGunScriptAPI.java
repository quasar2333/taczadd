package com.qdd.taczadd.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.effect.ModEffect;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModernKineticGunScriptAPI.class)
public class mixinModernKineticGunScriptAPI {
    @Shadow(remap = false)
    private LivingEntity shooter;

    @Inject(method = "reduceAmmoOnce", at = @At("HEAD"), cancellable = true,remap = false)
    public void mreduceAmmoOnce(CallbackInfoReturnable<Boolean> cir){
        if (shooter.hasEffect(ModEffect.InfiniteFirepowerE.get())){
            cir.setReturnValue(true);
        }
    }
}

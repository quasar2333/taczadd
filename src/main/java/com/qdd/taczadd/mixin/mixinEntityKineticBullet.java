package com.qdd.taczadd.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import com.qdd.taczadd.Config;
import com.qdd.taczadd.effect.ModEffect;
import com.qdd.taczadd.handler.AmmocCount;
import com.qdd.taczadd.handler.GamHandler;
import com.qdd.taczadd.handler.GunSkill;
import com.qdd.taczadd.item.Attributes.ModAttributes;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.TacHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityKineticBullet.class)
public abstract class mixinEntityKineticBullet  extends Projectile {
    @Unique
    private ItemStack gunItem;

    @Shadow(remap = false)
    private ResourceLocation gunId;

    @Shadow(remap = false)
    private float armorIgnore;

    @Shadow(remap = false)
    private float damageModifier;

    protected mixinEntityKineticBullet(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }


    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V",at = @At("TAIL"),remap = false)
    public void init(EntityType type, Level worldIn, LivingEntity throwerIn, ItemStack gunItem, ResourceLocation ammoId, ResourceLocation gunId, ResourceLocation gunDisplayId, boolean isTracerAmmo, GunData gunData, BulletData bulletData, CallbackInfo ci){
        this.gunItem=gunItem;
        // 增强鲁棒性：确保 GemEffects 存在
        CompoundTag gemEffects = gunItem.getOrCreateTag().getCompound("GemEffects");
        if (gemEffects.isEmpty() && !GamHandler.getGams(gunItem).isEmpty()) {
            try {
                GamHandler.applygam(gunItem);
                gemEffects = gunItem.getOrCreateTag().getCompound("GemEffects");
            } catch (Exception ignored) {}
        }
        this.armorIgnore+=gemEffects.getFloat("armorIgnore");
        if (throwerIn.getAttribute(ModAttributes.armorIgnore.get())!=null) {
            this.armorIgnore += (float) throwerIn.getAttribute(ModAttributes.armorIgnore.get()).getValue();
        }
        this.armorIgnore=Math.max(this.armorIgnore,1);
        float gem = gemEffects.getFloat("damageModifier");
        float crt=gemEffects.getFloat("CRT");
        if (throwerIn.hasEffect(ModEffect.AccurateShooterE.get())){
            crt+=gemEffects.getFloat("accurate_shooter");
        }
        float cta=gemEffects.getFloat("CTA")+1;
        if (throwerIn.getAttribute(ModAttributes.CTA.get())!=null) {
            cta += (float) throwerIn.getAttribute(ModAttributes.CTA.get()).getValue();
        }
        this.damageModifier*=throwerIn.getRandom().nextFloat()< crt?cta:1;
        float gunReinf = gunItem.getOrCreateTag().getFloat("damagebase");
        float equipSet = 0.0f;
        if (throwerIn.getAttribute(ModAttributes.GunDamage.get())!=null) {
            equipSet = (float) throwerIn.getAttribute(ModAttributes.GunDamage.get()).getValue() - 1.0f;
        }
        float baseSum = Math.max(0f, gem + gunReinf + equipSet);
        this.damageModifier *= (1.0f + baseSum);
        this.damageModifier*=gunItem.getOrCreateTag().getFloat("multiple")+1;
    }

    @Inject(method = "onHitEntity",at = @At("HEAD"),remap = false, cancellable = true)
    public void monHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci){
        // 增强鲁棒性：确保 GemEffects 存在
        CompoundTag gemEffects = gunItem.getOrCreateTag().getCompound("GemEffects");
        if (gemEffects.isEmpty() && !GamHandler.getGams(gunItem).isEmpty()) {
            try {
                GamHandler.applygam(gunItem);
                gemEffects = gunItem.getOrCreateTag().getCompound("GemEffects");
            } catch (Exception ignored) {}
        }
        if (gemEffects.getFloat("battlefield_physician")==1&&result.getEntity() instanceof Player pattener &&this.getOwner() instanceof Player player){
            pattener.heal(1);
            player.heal(1);
            ci.cancel();
        }
        if (gemEffects.getFloat("firepower_suppression")>0){
            if(gunItem.getOrCreateTag().getInt("ammocount")%3==0&&result.getEntity() instanceof LivingEntity living){
                if (Config.skillKnockbackEnabled) {
                    living.knockback(1,this.getDeltaMovement().x,this.getDeltaMovement().z);
                }
            }
        }
        AmmocCount ac=new AmmocCount(gunItem);
        if (ac.shouldskill(1)){
            GunSkill.Skill(gunId,1, this,result.getEntity().position(),result.getEntity());
            GunSkill.Skill3(gunItem,gunId,this.getOwner());
            gunItem.getOrCreateTag().putLong("cd", this.level().getGameTime());
            ac.skill(1);
        }if(ac.shouldskill(2)){
            GunSkill.Skill2(gunId,1, this,result.getEntity().position(),result.getEntity());
            ac.skill(2);
        }
        if(ac.shouldskill(3)){
            GunSkill.Skill4(gunId,1, this,result.getEntity().position(),result.getEntity());
            ac.skill(3);
        }
        if(ac.shouldskill(4)){
            GunSkill.Skill5(gunId,1, this,result.getEntity().position(),result.getEntity());
            ac.skill(4);
        }
    }

    @Shadow(remap = false)
    protected abstract void defineSynchedData();
}

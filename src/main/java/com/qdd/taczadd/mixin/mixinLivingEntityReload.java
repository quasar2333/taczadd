package com.qdd.taczadd.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.qdd.taczadd.handler.GamHandler;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityDrawGun;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.event.ServerMessageGunReload;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityReload.class)
public class mixinLivingEntityReload {
    @Final
    @Shadow(remap = false)
    private LivingEntity shooter;

    @Final
    @Shadow(remap = false)
    private ShooterDataHolder data;

    @Final
    @Shadow(remap = false)
    private LivingEntityDrawGun draw;

    @Final
    @Shadow(remap = false)
    private LivingEntityShoot shoot;

    @Inject(method = "reload",at= @At(value = "HEAD"), cancellable = true,remap = false)
    public void mreload(CallbackInfo ci) {
        if (this.data.currentGunItem != null) {
            ItemStack currentGunItem = (ItemStack)this.data.currentGunItem.get();
            Item var3 = currentGunItem.getItem();
            if (var3 instanceof AbstractGunItem) {
                AbstractGunItem gunItem = (AbstractGunItem)var3;
                ResourceLocation gunId = gunItem.getGunId(currentGunItem);
                TimelessAPI.getCommonGunIndex(gunId).ifPresent((gunIndex) -> {
                    if (!gunItem.useInventoryAmmo(currentGunItem)) {
                        if (!this.data.reloadStateType.isReloading()) {
                            if (this.shoot.getShootCoolDown() == 0L) {
                                if (this.draw.getDrawCoolDown() == 0L) {
                                    if (!this.data.isBolting) {
                                        if (!IGunOperator.fromLivingEntity(this.shooter).needCheckAmmo() || gunItem.canReload(this.shooter, currentGunItem)) {
                                            if (!MinecraftForge.EVENT_BUS.post(new GunReloadEvent(this.shooter, currentGunItem, LogicalSide.SERVER))) {
                                                NetworkHandler.sendToTrackingEntity(new ServerMessageGunReload(this.shooter.getId(), currentGunItem), this.shooter);
                                                Bolt boltType = gunIndex.getGunData().getBolt();
                                                int ammoCount = gunItem.getCurrentAmmoCount(currentGunItem) + (gunItem.hasBulletInBarrel(currentGunItem) && boltType != Bolt.OPEN_BOLT ? 1 : 0);
                                                if (ammoCount <= 0) {
                                                    this.data.reloadStateType = ReloadState.StateType.EMPTY_RELOAD_FEEDING;
                                                } else {
                                                    this.data.reloadStateType = ReloadState.StateType.TACTICAL_RELOAD_FEEDING;
                                                }
                                                this.data.reloadTimestamp = System.currentTimeMillis();
                                                // 增强鲁棒性：确保 GemEffects 存在
                                                CompoundTag gemEffects = currentGunItem.getOrCreateTag().getCompound("GemEffects");
                                                if (gemEffects.isEmpty() && !GamHandler.getGams(currentGunItem).isEmpty()) {
                                                    try {
                                                        GamHandler.applygam(currentGunItem);
                                                        gemEffects = currentGunItem.getOrCreateTag().getCompound("GemEffects");
                                                    } catch (Exception ignored) {}
                                                }
                                                float r=gemEffects.getFloat("reloadgam");
                                                if (r>0){
                                                    this.data.reloadTimestamp -= (long) (gunIndex.getGunData().getReloadData().getCooldown().getTacticalTime()*1000/(1+r));
                                                    this.shooter.setHealth((float) (this.shooter.getHealth()- this.shooter.getMaxHealth()*0.1));
                                                }
                                                if (!gunItem.startReload(this.data, currentGunItem, this.shooter)) {
                                                    this.data.reloadStateType = ReloadState.StateType.NOT_RELOADING;
                                                    this.data.reloadTimestamp = -1L;
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        ci.cancel();
    }
}

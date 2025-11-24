package com.qdd.taczadd.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.tacz.guns.entity.EntityKineticBullet;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterWaveEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("skill1");
    private EntityKineticBullet kb;
    private int tick;

    public WaterWaveEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public WaterWaveEntity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos) {
        super(type,entity);
    }
    @Override
    protected  EntityKineticBullet getkb() {
        return kb;
    }

    @Override
    public void setkb(EntityKineticBullet entity){
        kb= entity;
        this.setOwner(kb.getOwner());
    }

    @Override
    protected int multiple(){
        // 扩散水波仅用于展示动画，不再造成伤害
        return 0;
    }

    @Override
    public void tick(){
        super.tick();
        if (tick==1){
            triggerAnim("ww","ww");
        }
        // 仅展示动画，不进行伤害计算
        if (tick==50){
            discard();
        }
        Vec3 movement = this.getDeltaMovement();
        double x = movement.x;
        double y = movement.y;
        double z = movement.z;
        Vec3 vec3 = movement;
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        double nextPosX = this.getX() + x;
        double nextPosY = this.getY() + y;
        double nextPosZ = this.getZ() + z;
        this.setPos(nextPosX, nextPosY, nextPosZ);
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"ww",0, state ->  PlayState.STOP
        ).triggerableAnim("ww", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

}

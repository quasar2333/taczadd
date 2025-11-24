package com.qdd.taczadd.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.qdd.taczadd.Taczadd;
import com.tacz.guns.entity.EntityKineticBullet;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WatrtWavecEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("skill1");
    private EntityKineticBullet kb;
    private int tick;

    public WatrtWavecEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public WatrtWavecEntity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos) {
        super(type,entity);
        this.setPos(pos.add(0,0.1,0));
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
        return 20; // 大技能2水波2000%伤害
    }

    @Override
    public void tick(){
        super.tick();
        if (tick==1&&!level().isClientSide()){
            triggerAnim("wwc","wwc");
        }
        // 保留动画：tick==15 播放中心动画，但不造成伤害
        if ((tick==15)&&!level().isClientSide()) {
            // no-op: 仅保留动画
        }
        if (tick==30&&!level().isClientSide()){
            // 仅在水波生成时造成一次中心范围伤害（2000%），随后生成扩散水波（纯视觉，不再造成伤害）
            level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(this.position().add(-6, -2, -6), this.position().add(6, 2, 6)),
                    this::canskill).forEach(
                    this::skill
            );
            discard();
            for(int i=0;i<8;i++) {
                WaterWaveEntity wave = new WaterWaveEntity(ModEntities.WW_Entity.get(), getkb(), this.position());
                wave.setPos(this.position());
                Vec3 vv= this.getOwner().getViewVector(0).yRot((float) Math.toRadians(45*i));
                wave.shoot(vv.x(),0,vv.z(),0.5f,0);
                level().addFreshEntity(wave);
            }
        }
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"wwc",0, state ->  PlayState.STOP
        ).triggerableAnim("wwc", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

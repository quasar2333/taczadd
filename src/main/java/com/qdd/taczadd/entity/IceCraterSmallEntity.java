package com.qdd.taczadd.entity;

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

public class IceCraterSmallEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("skill");
    private EntityKineticBullet kb;
    private int tick;

    public IceCraterSmallEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public IceCraterSmallEntity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos) {
        super(type,entity);
        this.setPos(pos.add(0,0.9,0));
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
        return 3; // 300%伤害
    }

    @Override
    public void tick(){
        super.tick();
        if (tick==1){
            triggerAnim("ics","ics");
        }
        // 四次300%伤害，范围5*5*2
        if ((tick==10||tick==20||tick==30||tick==40)&&!level().isClientSide()) {
            level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(this.position().add(-2.5, -1, -2.5), this.position().add(2.5, 1, 2.5)),
                    this::canskill).forEach(
                    this::skill
            );
        }
        if (tick==50){
            discard();
        }
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"ics",0,state ->  PlayState.STOP
        ).triggerableAnim("ics", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

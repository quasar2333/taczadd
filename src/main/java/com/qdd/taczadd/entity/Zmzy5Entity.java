package com.qdd.taczadd.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
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

public class Zmzy5Entity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell1 = RawAnimation.begin().thenPlay("lrd");
    private static final RawAnimation spell2 = RawAnimation.begin().thenPlay("rld");
    private static final RawAnimation spell3 = RawAnimation.begin().thenPlay("lr");
    private static final RawAnimation spell4 = RawAnimation.begin().thenPlay("v");
    private static final RawAnimation spell5 = RawAnimation.begin().thenPlay("v2");
    private EntityKineticBullet kb;
    private int tick;
    private Entity target;

    public Zmzy5Entity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public Zmzy5Entity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos, Entity target) {
        super(type,entity);
        this.setPos(pos.add(0,1,0));
        this.target=target;
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
    public void tick(){
        super.tick();
        tick++;
        if (tick==69){
            discard();
        }
        if (this.level().isClientSide()) {
            if (tick == 1) {
                // kickstart first segment immediately on client
                triggerAnim("zmzy", "zmzy51");
            } else if (tick % 14 == 0) {
                triggerAnim("zmzy", "zmzy5" +(int) Math.ceil((double) tick / 14));
            }
        } else {
            if (tick%14==3&&target!=null&&target.isAlive()) {
                skill(target);
            }
        }
    }

    @Override
    protected int multiple(){
        return 2;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"zmzy",0, state ->  PlayState.STOP
        ).triggerableAnim("zmzy51", spell1).triggerableAnim("zmzy52", spell2).triggerableAnim("zmzy53", spell3)
                .triggerableAnim("zmzy54", spell4).triggerableAnim("zmzy55", spell5));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
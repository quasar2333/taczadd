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

public class Zmzy30Entity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell1 = RawAnimation.begin().thenPlay("skill1");
    private static final RawAnimation spell2 = RawAnimation.begin().thenPlay("skill2");
    private static final RawAnimation spell3 = RawAnimation.begin().thenPlay("skill3");
    private static final RawAnimation spell4 = RawAnimation.begin().thenPlay("skill4");
    private static final RawAnimation spell5 = RawAnimation.begin().thenPlay("skill5");
    private static final RawAnimation spell6 = RawAnimation.begin().thenPlay("skill6");
    private EntityKineticBullet kb;
    private int tick;
    private Entity target;

    public Zmzy30Entity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public Zmzy30Entity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos,Entity target) {
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
        if (tick==179){
            discard();
        }
        if (this.level().isClientSide()) {
            // 立即触发动画，无间隙切换，删除中间罚站动画
            if (tick == 1) {
                triggerAnim("zmzy", "zmzy301");
            } else if (tick == 28) {
                triggerAnim("zmzy", "zmzy302");
            } else if (tick == 56) {
                triggerAnim("zmzy", "zmzy303");
            } else if (tick == 84) {
                triggerAnim("zmzy", "zmzy304");
            } else if (tick == 112) {
                triggerAnim("zmzy", "zmzy305");
            } else if (tick == 140) {
                triggerAnim("zmzy", "zmzy306");
            }
        } else {
            if (tick%30==3&&target!=null&&target.isAlive()) {
                skill(target);
            }
        }
    }

    @Override
    protected int multiple(){
        return 8;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"zmzy",0, state ->  PlayState.STOP
        ).triggerableAnim("zmzy301", spell1).triggerableAnim("zmzy302", spell2).triggerableAnim("zmzy303", spell3)
                .triggerableAnim("zmzy304", spell4).triggerableAnim("zmzy305", spell5).triggerableAnim("zmzy306", spell6));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

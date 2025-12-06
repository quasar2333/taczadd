package com.qdd.taczadd.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

/**
 * 末影龙技能：射击20发触发，对目标3x3x3范围内的敌人造成一次1000%的枪械伤害
 */
public class EndDragonGun extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("skill");
    private EntityKineticBullet kb;
    private int tick;
    private Entity target;

    public EndDragonGun(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    
    public EndDragonGun(EntityType<? extends SkillEntity> type, Entity entity, Vec3 pos, Entity target) {
        super(type, entity);
        this.target = target;
        // 在目标位置生成
        if (target != null) {
            this.setPos(target.position().add(0, 1, 0));
        } else {
            this.setPos(pos);
        }
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
        
        // 在第1帧立即对目标3x3x3范围内的敌人造成1000%伤害
        if (tick == 1 && !this.level().isClientSide() && target != null) {
            // 获取目标周围3x3x3范围内的所有生物实体
            level().getEntitiesOfClass(LivingEntity.class,
                    target.getBoundingBox().inflate(1.5D, 1.5D, 1.5D),
                    this::canskill).forEach(this::skill);
        }
        
        // 短暂存在后消失（无动画）
        if (tick >= 5) {
            discard();
        }
        tick++;
    }

    @Override
    protected int multiple(){
        return 10; // 1000% 伤害 = 10倍
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"ed",0, state ->  PlayState.STOP
        ).triggerableAnim("ed", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

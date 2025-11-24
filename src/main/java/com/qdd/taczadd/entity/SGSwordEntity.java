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

public class SGSwordEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("skill");
    private EntityKineticBullet kb;
    private int tick;

    public SGSwordEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }

    public SGSwordEntity(EntityType<? extends SkillEntity> type, Entity entity, Vec3 pos) {
        super(type, entity);
        this.setPos(pos.add(0, 0.9, 0));
    }

    @Override
    protected EntityKineticBullet getkb() {
        return kb;
    }

    @Override
    public void setkb(EntityKineticBullet entity) {
        kb = entity;
        this.setOwner(kb.getOwner());
    }

    @Override
    protected int multiple() {
        return 15; // 1500%
    }

    @Override
    public void tick() {
        super.tick();
        if (tick == 1) {
            triggerAnim("sg", "sg");
        }
        if (tick == 10 && !level().isClientSide()) {
            AABB range = new AABB(this.position().add(-3, -4, -1), this.position().add(3, 4, 5));
            level().getEntitiesOfClass(LivingEntity.class, range, this::canskill).forEach(this::skill);
        }
        if (tick >= 40) {
            discard();
        }
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "sg", 0, state -> PlayState.STOP)
                .triggerableAnim("sg", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}



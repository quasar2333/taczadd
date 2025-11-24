package com.qdd.taczadd.entity;

import net.minecraft.core.particles.ParticleTypes;
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

public class PozhanzheSkillEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation fist = RawAnimation.begin().thenPlay("fist");
    private EntityKineticBullet kb;
    private int tick;
    private Entity target;

    public PozhanzheSkillEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    
    public PozhanzheSkillEntity(EntityType<? extends SkillEntity> type, Entity entity, Vec3 pos, Entity target) {
        super(type, entity);
        this.setPos(pos.add(0, 1, 0));
        this.target = target;
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
        return 10; // 1000% damage = 10x multiplier
    }

    @Override
    public void tick() {
        super.tick();
        
        // Trigger animation at start
        if (tick == 1) {
            triggerAnim("pozhanzhe", "pozhanzhe");
        }
        
        // Deal AOE damage at tick 60 (2 seconds into 3-second animation)
        if (tick == 30 && !this.level().isClientSide() && target != null) {
            // Get all living entities in 8x8x8 area around target
            level().getEntitiesOfClass(LivingEntity.class,
                    target.getBoundingBox().inflate(4.0D, 4.0D, 4.0D),
                    this::canskill).forEach(entity -> {
                        // Deal damage
                        skill(entity);
                        
                        // Add explosion particles
                        level().addParticle(ParticleTypes.EXPLOSION, 
                            entity.getX(), entity.getY(), entity.getZ(), 0, 0.1, 0);
                        level().addParticle(ParticleTypes.EXPLOSION_EMITTER, 
                            entity.getX(), entity.getY() + 1, entity.getZ(), 0, 0.1, 0);
                        
                        // Knockback effect
                        float distance = entity.distanceTo(this);
                        if (distance > 0) {
                            double x = entity.getX() - getX();
                            double z = entity.getZ() - getZ();
                            entity.setDeltaMovement(entity.getDeltaMovement().add(x / distance / 2, 0.3, z / distance / 2));
                        }
                    });
        }
        
        // Remove entity after animation completes (3 seconds = 60 ticks)
        if (tick == 90) {
            discard();
        }
        
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "pozhanzhe", 0, state -> PlayState.STOP)
                .triggerableAnim("pozhanzhe", fist));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}


package com.qdd.taczadd.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.qdd.taczadd.sound.ModSounds;
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
    
    // 朝向同步（像K30一样）
    private static final EntityDataAccessor<Float> DATA_YAW =
            SynchedEntityData.defineId(SGSwordEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH =
            SynchedEntityData.defineId(SGSwordEntity.class, EntityDataSerializers.FLOAT);

    public SGSwordEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }

    public SGSwordEntity(EntityType<? extends SkillEntity> type, Entity entity, Vec3 pos) {
        super(type, entity);
        this.setPos(pos.add(0, 0.9, 0));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_YAW, 0f);
        this.entityData.define(DATA_PITCH, 0f);
    }
    
    /**
     * 设置初始朝向（像K30一样）
     */
    public void setInitialFacing(float yaw, float pitch) {
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.entityData.set(DATA_YAW, yaw);
        this.entityData.set(DATA_PITCH, pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
    }
    
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_YAW.equals(key) || DATA_PITCH.equals(key)) {
            float yaw = this.entityData.get(DATA_YAW);
            float pitch = this.entityData.get(DATA_PITCH);
            this.setYRot(yaw);
            this.setXRot(pitch);
            this.yRotO = yaw;
            this.xRotO = pitch;
        }
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
            // 播放剑插入地面音效
            level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                    ModSounds.SG914_SKILL.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
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



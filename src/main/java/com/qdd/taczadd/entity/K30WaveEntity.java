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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class K30WaveEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("skill");
    private EntityKineticBullet kb;
    private int tick;
    private static final EntityDataAccessor<Float> DATA_YAW =
            SynchedEntityData.defineId(K30WaveEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH =
            SynchedEntityData.defineId(K30WaveEntity.class, EntityDataSerializers.FLOAT);

    public K30WaveEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }

    public K30WaveEntity(EntityType<? extends SkillEntity> type, Entity entity, Vec3 pos) {
        super(type, entity);
        this.setPos(pos.add(0, 0.1, 0));
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
        return 8; // 800%
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_YAW, 0f);
        this.entityData.define(DATA_PITCH, 0f);
    }


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
    public void tick() {
        super.tick();

        if (tick == 1) {
            triggerAnim("k30", "k30");
        }


        if (!level().isClientSide()) {
            if (tick == 10 || tick == 30 || tick == 50) {

                Vec3 forward = Vec3.directionFromRotation(this.getXRot(), -this.getYRot()).normalize();



                double nearDist = -3.0;
                double farDist  = 7.0;


                double halfW = 3.0;
                double halfH = 3.0;

                Vec3 origin = this.position();
                Vec3 p0 = origin.add(forward.scale(nearDist));
                Vec3 p1 = origin.add(forward.scale(farDist));


                double minX = Math.min(p0.x, p1.x) - halfW;
                double maxX = Math.max(p0.x, p1.x) + halfW;
                double minY = Math.min(p0.y, p1.y) - halfH;
                double maxY = Math.max(p0.y, p1.y) + halfH;

                double minZ = Math.min(p0.z, p1.z) - halfW;
                double maxZ = Math.max(p0.z, p1.z) + halfW;

                AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box, this::canskill)) {

                    Vec3 center = target.getBoundingBox().getCenter();

                    Vec3 rel = center.subtract(origin);
                    double along = rel.dot(forward);
                    if (along < nearDist || along > farDist) continue;


                    Vec3 residual = rel.subtract(forward.scale(along));


                    double horiz = Math.sqrt(residual.x * residual.x + residual.z * residual.z);
                    double vert  = Math.abs(residual.y);


                    double inflateW = target.getBbWidth() * 0.5;
                    double inflateH = target.getBbHeight() * 0.25;
                    if (horiz <= (halfW + inflateW) && vert <= (halfH + inflateH)) {
                        this.skill(target);}

                    }
            }
        }

        if (tick >= 60) {
            discard();
        }
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "k30", 0, state -> PlayState.STOP)
                .triggerableAnim("k30", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}



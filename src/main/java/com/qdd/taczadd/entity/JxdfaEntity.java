package com.qdd.taczadd.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.EntityUtil;
import com.tacz.guns.util.block.BlockRayTrace;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class JxdfaEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("skill");
    private EntityKineticBullet kb;
    private int tick;

    public JxdfaEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public JxdfaEntity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos) {
        super(type,entity);
        double posX = this.getOwner().xOld + (this.getOwner().getX() - this.getOwner().xOld) / (double)2.0F;
        double posY = this.getOwner().yOld + (this.getOwner().getY() - this.getOwner().yOld) / (double)2.0F + (double)this.getOwner().getEyeHeight();
        double posZ = this.getOwner().zOld + (this.getOwner().getZ() - this.getOwner().zOld) / (double)2.0F;
        this.setPos(posX, posY, posZ);
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
        if (tick==1){
            triggerAnim("dfa","dfa");
        }
        if (tick==31){
            discard();
        }
        this.onBulletTick();
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

    protected void onBulletTick() {
        if (!this.level().isClientSide()) {
            Vec3 startVec = this.position();
            Vec3 endVec = startVec.add(this.getDeltaMovement());
            BlockHitResult resultB = BlockRayTrace.rayTraceBlocks(this.level(), new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (resultB.getType() != HitResult.Type.MISS) {
                endVec = resultB.getLocation();
            }

            List<EntityKineticBullet.EntityResult> hitEntities = null;
            hitEntities = EntityUtil.findEntitiesOnPath(this, startVec, endVec);
            if (!hitEntities.isEmpty()) {
                EntityKineticBullet.EntityResult[] hitEntityResult = hitEntities.toArray(new EntityKineticBullet.EntityResult[0]);
                for(EntityKineticBullet.EntityResult entityResult : hitEntityResult) {
                    EntityHitResult var11 = new EntityHitResult(entityResult.getEntity());
                    this.onHitEntity(var11);
                }
            }

            this.onHitBlock(resultB);
        }

    }
    @Override
    protected int multiple(){
        return 12;
    }
    @Override
    protected void onHitEntity(EntityHitResult p_37259_) {
        Entity entity=p_37259_.getEntity();
        if (entity.equals(this.getOwner()))return;
        skill(entity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"dfa",0, state ->  PlayState.STOP
        ).triggerableAnim("dfa", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

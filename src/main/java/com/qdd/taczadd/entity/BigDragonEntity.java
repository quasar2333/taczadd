package com.qdd.taczadd.entity;

import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import com.qdd.taczadd.sound.ModSounds;
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

public class BigDragonEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation skill1 = RawAnimation.begin().thenPlay("skill1");
    private static final RawAnimation skill2 = RawAnimation.begin().thenPlay("skill2");
    private static final RawAnimation skill3 = RawAnimation.begin().thenPlay("skill3");
    private EntityKineticBullet kb;
    private int tick;
    private boolean isSkill1;
    private Vec3 pos;
    private int skill;
    // Skill2 (升龙) 路径伤害仅触发一次的保护开关
    private boolean skill2PathDealt;

    public BigDragonEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
        tick=71;
    }
    public BigDragonEntity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos) {
        super(type,entity);
        tick=71;
        this.pos=pos;
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

    public void skill1(){
        skill=1;
        if(!level().isClientSide())
            triggerAnim("bd","s1");
        tick=81;
        isSkill1=true;
    }
    public void skill2(){
        skill=2;
    }

    @Override
    public void tick(){
        super.tick();
        if (!isSkill1&&tick==70&&!level().isClientSide()){
            triggerAnim("bd","s3");
        }
        // 升龙：在水波生成前，保证路径范围伤害至多触发一次（若未通过碰撞触发）
        if ((isSkill1 || this.skill==2) && !skill2PathDealt && tick==52 && !level().isClientSide()) {
            Vec3 center = this.pos != null ? this.pos : this.position();
            level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(center.add(-6.0D, -2.0D, -6.0D), center.add(6.0D, 2.0D, 6.0D)),
                    this::canskill).forEach(e -> skill(e, 15f));
            skill2PathDealt = true;
        }
        if (tick==50&&isSkill1&&!level().isClientSide()){
            this.setDeltaMovement(0,0.5d,0);
            triggerAnim("bd","s2");
            this.playSound(ModSounds.SKILL2.get(), 1f,1f);
            WatrtWavecEntity wwc=new WatrtWavecEntity(ModEntities.WWC_Entity.get(),getkb(),pos);
            wwc.setPos( pos);
            level().addFreshEntity(wwc);
        }
        // 大技能1：第一击500%伤害，范围6*6*3
        if(tick==50&&!isSkill1&&!level().isClientSide()){
            level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(3.0D, 1.5D, 3.0D),
                    this::canskill).forEach(e->{
                        skill(e,5); // 500%伤害
                        e.knockback(1.5f, -Math.cos(Math.toRadians(this.getYRot())), -Math.sin(Math.toRadians(this.getYRot())));
            }
            );
        }
        if(tick==35&&!isSkill1){
            this.playSound(ModSounds.SKILL1.get(), 1f,1f);
        }

        // 大技能1：龙息800%伤害，范围6*6*3
        if(tick==29&&!isSkill1&&!level().isClientSide()){
            level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(3.0D, 1.5D, 3.0D),
                    this::canskill).forEach(e->{
                        skill(e,8f); // 800%伤害
                        float l = e.distanceTo(this);
                        double x=e.getX()-getX();
                        double z=e.getZ()-getZ();
                        e.setDeltaMovement(e.getDeltaMovement().add(x/l/2,0.5,z/l/2));
                    }
            );
        }
        // 大技能1：潜入地下1000%伤害，范围6*6*3
        if(tick==1&&!isSkill1){
            level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(3.0D, 1.5D, 3.0D),
                    this::canskill).forEach(e->{
                        level().addParticle(ParticleTypes.EXPLOSION, e.getX(), e.getY(), e.getZ(), 0, 0.1, 0);
                        level().addParticle(ParticleTypes.EXPLOSION_EMITTER, e.getX(), e.getY(), e.getZ(), 0, 0.1, 0);
                        float l = e.distanceTo(this);
                        double x=e.getX()-getX();
                        double z=e.getZ()-getZ();
                        e.setDeltaMovement(e.getDeltaMovement().add(x/l/2,0.5,z/l/2));
                        skill(e,0);
                    }
            );
        }
        if (tick==0&&!level().isClientSide()){
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
        tick--;
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
        return 15; // 大技能2路径上1500%伤害
    }
    @Override
    protected void onHitEntity(EntityHitResult p_37259_) {
        Entity entity = p_37259_.getEntity();
        if (entity.equals(this.getOwner())) return;
        // 升龙/路径命中：首次命中时，对范围内目标造成一次1500%范围伤害
        if ((isSkill1 || this.skill==2) && !skill2PathDealt && !this.level().isClientSide()) {
            level().getEntitiesOfClass(LivingEntity.class,
                    entity.getBoundingBox().inflate(6.0D, 2.0D, 6.0D),
                    this::canskill)
                .forEach(e -> skill(e, 15f));
            skill2PathDealt = true;
            return;
        }
        // 非升龙：维持原有的单体命中逻辑
        if (!isSkill1) {
            skill(entity);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"bd",0, state ->  PlayState.STOP
        ).triggerableAnim("s1", skill1).triggerableAnim("s2", skill2).triggerableAnim("s3", skill3))
        ;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

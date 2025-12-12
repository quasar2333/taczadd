package com.qdd.taczadd.entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.qdd.taczadd.Config;
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

public class GreatswordEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell1 = RawAnimation.begin().thenPlay("skill1");
    private static final RawAnimation spell2 = RawAnimation.begin().thenPlay("skill2");
    private static final RawAnimation spell3 = RawAnimation.begin().thenPlay("parry");
    private EntityKineticBullet kb;
    private int tick;

    public GreatswordEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public GreatswordEntity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos,Entity  target) {
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
        return 5;
    }

    @Override
    public void tick(){
        super.tick();
        if (tick==1){
            triggerAnim("gw","gw2");
        } else if (tick==20) {
            stopTriggeredAnimation("gw","gw2");
            triggerAnim("gw","gw3");
        } else if (tick==60) {
            stopTriggeredAnimation("gw","gw3");
            triggerAnim("gw","gw");
        }
        if ((tick==4||tick==50||tick==70||tick==100)&&!level().isClientSide()) {
            level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(this.position().add(-4, -1, -4), this.position().add(4, 1, 4)),
                    this::canskill).forEach(e->{
                            skill(e);
                            if (Config.skillKnockbackEnabled) {
                                float l = e.distanceTo(this);
                                if (l > 0.0001f) {
                                    double x=e.getX()-getX();
                                    double z=e.getZ()-getZ();
                                    e.setDeltaMovement(e.getDeltaMovement().add(x/l,1,z/l));
                                }
                            }

                    }
            );
        }
        if(tick==85){
            this.playSound(ModSounds.SKILL3.get(),1f,1f);
        }
        if (tick==105){
            discard();
        }
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"gw",0, state ->  PlayState.STOP
        ).triggerableAnim("gw", spell1).triggerableAnim("gw2", spell2).triggerableAnim("gw3", spell3));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

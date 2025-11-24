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

public class JxdfbEntity extends SkillEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation spell = RawAnimation.begin().thenPlay("impact");
    private EntityKineticBullet kb;
    private int tick;
    private Entity target;

    public JxdfbEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public JxdfbEntity(EntityType<? extends SkillEntity> type, Entity entity , Vec3 pos, Entity target) {
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
    protected int multiple(){
        return 10;
    }

    @Override
    public void tick(){
        super.tick();
        if (tick==1){
            triggerAnim("dfb","dfb");
        }
        if (tick==14){
            discard();
        }
        if (tick==3&&!this.level().isClientSide()&&target!=null&&target.isAlive()){
            skill(target);
        }
        tick++;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this,"dfb",0, state ->  PlayState.STOP
        ).triggerableAnim("dfb", spell));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

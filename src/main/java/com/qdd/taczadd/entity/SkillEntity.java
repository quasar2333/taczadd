package com.qdd.taczadd.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.init.ModDamageTypes;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.event.ServerMessageGunHurt;
import com.tacz.guns.network.message.event.ServerMessageGunKill;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import static com.tacz.guns.entity.EntityKineticBullet.PRETEND_MELEE_DAMAGE_ON;

public abstract class SkillEntity extends Projectile {
    public SkillEntity(EntityType<? extends SkillEntity> type, Level level) {
        super(type, level);
    }
    public SkillEntity(EntityType<? extends SkillEntity> type,Entity entity){
        this(type,entity.level());
        setkb((EntityKineticBullet) entity);
    }

    public abstract void setkb(EntityKineticBullet entity);

    protected abstract int multiple();
    protected abstract EntityKineticBullet getkb();

    public boolean canskill(Entity entity){
        return entity.isAlive()&&entity!=getOwner();
    }

    protected void skill(Entity entity){
        this.skill(entity,multiple());
    }

    protected void skill(Entity entity , float d){
        if (!canskill(entity))return;
        EntityType<?> hitPartType = EntityKineticBullet.MaybeMultipartEntity.of(entity).hitPart().getType();
        Entity directCause = hitPartType.is(PRETEND_MELEE_DAMAGE_ON) ? this.getOwner() : this;
        EntityKineticBullet.MaybeMultipartEntity parts = EntityKineticBullet.MaybeMultipartEntity.of(entity);
        DamageSource source1 = ModDamageTypes.Sources.bullet(this.level().registryAccess(), directCause, this.getOwner(), false);
        DamageSource source2 = ModDamageTypes.Sources.bullet(this.level().registryAccess(), directCause, this.getOwner(), true);
        Pair<DamageSource,DamageSource> sources=Pair.of(source1,source2);
        tacAttackEntity(parts,getkb()!=null? getkb().getDamage(entity.position())*d:10*d, sources);
    }

    private void tacAttackEntity(EntityKineticBullet.MaybeMultipartEntity parts, float damage, Pair<DamageSource, DamageSource> sources) {
        DamageSource source1 = sources.getLeft();
        DamageSource source2 = sources.getRight();
        float armorDamagePercent = Mth.clamp(0.5f, 0.0F, 1.0F);
        float normalDamagePercent = 1.0F - armorDamagePercent;
        parts.core().invulnerableTime = 0;
        parts.hitPart().hurt(source1, damage * normalDamagePercent);
        parts.core().invulnerableTime = 0;
        parts.hitPart().hurt(source2, damage * armorDamagePercent);
    }

    @Override
    protected void defineSynchedData() {

    }

}


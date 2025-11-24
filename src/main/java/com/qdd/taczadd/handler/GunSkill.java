package com.qdd.taczadd.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.*;

import java.util.Map;
import static java.util.Map.entry;

public class GunSkill {
    public static final Map<String, Object> icedragon= Map.of(
        "ammoc",20,
        "ammoc3",40,
        "ammoc4",60,
        "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/icedragon2.png"),
        "skillicon3",new ResourceLocation(Taczadd.MODID,"textures/icon/icedragon3.png"),
        "skillicon4",new ResourceLocation(Taczadd.MODID,"textures/icon/icedragon4.png")
    );
    public static final Map<String, Object> ak24= Map.of(
        "ammoc",30,
        "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/icedragon.png")
    );

    public static final Map<String, Object> enddragon= Map.of(
            "ammoc",30,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/enddragon.png")
    );

    public static final Map<String, Object> zmzy= Map.of(
            "ammoc",30,
            "ammoc2",5,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/zmzy30.png"),
            "skillicon2",new ResourceLocation(Taczadd.MODID,"textures/icon/zmzy5.png")
    );
    public static final Map<String, Object> ak117jxdf_a= Map.of(
            "ammoc",30,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/ak117jxdfa.png")
    );
    public static final Map<String, Object> ak117jxdf_b= Map.of(
            "ammoc",30,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/ak117jxdfb.png")
    );
    public static final Map<String, Object> augfly= Map.of(
            "ammoc",-1,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/augfly.png"),
            "cd",100
    );
    public static final Map<String, Object> ak117jxdf= Map.of(
            "ammoc",-1,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/ak117jxdf.png"),
            "cd",100

    );public static final Map<String, Object> ak47gm= Map.of(
            "ammoc",-1,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/ak47gm.png"),
            "cd",100
    );
    public static final Map<String, Object> rm68zrgl= Map.of(
            "ammoc",-1,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/rm68zrgl.png")
    );
    // SG-914: 每射击10发子弹触发，剑在目标处插入地面升起特效，一次1500%伤害
    public static final Map<String, Object> moritz_shotgun_sg914 = Map.of(
            "ammoc", 10,
            "skillicon", new ResourceLocation(Taczadd.MODID, "textures/icon/zmzy30.png")
    );
    // K30: 每射击3发子弹触发，短延迟后发出光波，对范围内敌人3次800%伤害
    public static final Map<String, Object> moritz_sniper_semi_k30 = Map.of(
            "ammoc", 3,
            "skillicon", new ResourceLocation(Taczadd.MODID, "textures/icon/zmzy5.png")
    );
    // Pozhanzhe: Transfer ak117jxdf skill (cooldown-based) and add new 30-shot explosion skill
    public static final Map<String, Object> pozhanzhe= Map.of(
            "ammoc",-1,
            "ammoc2",30,
            "skillicon",new ResourceLocation(Taczadd.MODID,"textures/icon/ak117jxdf.png"),
            "skillicon2",new ResourceLocation(Taczadd.MODID,"textures/icon/pozhanzhe.png"),
            "cd",100
    );

    public static final Map<String,Map<String,Object>> Skill = Map.ofEntries(
            entry("icedragon", icedragon),
            entry("enddragon", enddragon),
            entry("zmzy", zmzy),
            entry("ak117jxdf_a", ak117jxdf_a),
            entry("ak117jxdf_b", ak117jxdf_b),
            entry("augfly", augfly),
            entry("ak117jxdf", ak117jxdf),
            entry("ak47gm", ak47gm),
            entry("rm68zrgl", rm68zrgl),
            entry("ak24", ak24),
            entry("moritz_shotgun_sg914", moritz_shotgun_sg914),
            entry("moritz_sniper_semi_k30", moritz_sniper_semi_k30),
            entry("pozhanzhe", pozhanzhe)
    );
    public static void Skill(ResourceLocation gunid, int pierce,Entity entity, Vec3 pos,Entity target){
        switch (gunid.getPath()){
            case "ak24":if (pierce==1){
                IceCraterSmallEntity skill=new IceCraterSmallEntity(ModEntities.ICS_Entity.get(),entity,pos);
                entity.level().addFreshEntity(skill);
                break;
            }
            case "icedragon":{
                GreatswordEntity skill=new GreatswordEntity(ModEntities.GW_Entity.get(),entity,pos,target);
                entity.level().addFreshEntity(skill);
                break;
            }
            case "moritz_shotgun_sg914": {
                com.qdd.taczadd.entity.SGSwordEntity skill = new com.qdd.taczadd.entity.SGSwordEntity(com.qdd.taczadd.entity.ModEntities.SGS_Entity.get(), entity, pos);
                entity.level().addFreshEntity(skill);
                break;
            }
            case "enddragon":{
                EndDragonGun skill=new EndDragonGun(ModEntities.ED_Entity.get(),entity,pos);
                skill.shootFromRotation(skill,skill.getOwner().getXRot(),skill.getOwner().getYRot(),0,1f,0);
                entity.level().addFreshEntity(skill);
                break;
            } case "zmzy":if (pierce==1){
                Zmzy30Entity skill=new Zmzy30Entity(ModEntities.Zmzy30_Entity.get(),entity,pos,target);
                entity.level().addFreshEntity(skill);
                break;
            }case "ak117jxdf_a":{
                JxdfaEntity skill=new JxdfaEntity(ModEntities.Jxdfa_Entity.get(),entity,pos);
                skill.shootFromRotation(skill,skill.getOwner().getXRot(),skill.getOwner().getYRot(),0,1f,0);
                entity.level().addFreshEntity(skill);
                break;
            } case "ak117jxdf_b":if (pierce==1){
                JxdfbEntity skill=new JxdfbEntity(ModEntities.Jxdfb_Entity.get(),entity,pos,target);
                entity.level().addFreshEntity(skill);
                break;
            }
            case "moritz_sniper_semi_k30": {
                Vec3 spawn = target != null ? target.getBoundingBox().getCenter() : pos;
                K30WaveEntity wave = new K30WaveEntity(ModEntities.K30W_Entity.get(), entity, spawn);
                wave.setInitialFacing(entity.getYRot(), entity.getXRot());
                entity.level().addFreshEntity(wave);
                break;
            }

        }

    }
    public static void Skill2(ResourceLocation gunid, int pierce,Entity entity, Vec3 pos,Entity target){
        switch (gunid.getPath()){
            case "zmzy":if (pierce==1){
                Zmzy5Entity skill=new Zmzy5Entity(ModEntities.Zmzy5_Entity.get(),entity,pos,target);
                entity.level().addFreshEntity(skill);
                break;
            }
            case "pozhanzhe":if (pierce==1){
                PozhanzheSkillEntity skill=new PozhanzheSkillEntity(ModEntities.PozhanzheSkill_Entity.get(),entity,pos,target);
                entity.level().addFreshEntity(skill);
                break;
            }
        }
    }
    public static void Skill3(ItemStack gunitem, ResourceLocation gunid, Entity entity){
        long lastCd = gunitem.getOrCreateTag().getLong("cd");
        long gameTime = entity.level().getGameTime();
        if (gameTime - lastCd > 160 || gameTime < lastCd){
            // Reset to baseline: no damage bonus, rpm back to 1x
            gunitem.getOrCreateTag().putFloat("multiple",0);
            gunitem.getOrCreateTag().putFloat("rpmadd", 1);
        }
        switch (gunid.getPath()){
            case "augfly":{
                gunitem.getOrCreateTag().putFloat("rpmadd", Mth.clamp(gunitem.getOrCreateTag().getFloat("rpmadd")+0.1f,1.0f,3f));
                break;
            }
            case "ak117jxdf":{
                gunitem.getOrCreateTag().putFloat("multiple", Mth.clamp(gunitem.getOrCreateTag().getFloat("multiple")+0.1f,1.0f,3f));
                break;
            }
            case "ak47gm":{
                gunitem.getOrCreateTag().putFloat("multiple", Mth.clamp(gunitem.getOrCreateTag().getFloat("multiple")+0.1f,1.0f,3f));
                gunitem.getOrCreateTag().putFloat("rpmadd", Mth.clamp(gunitem.getOrCreateTag().getFloat("rpmadd")+0.1f,1.0f,3f));
                break;
            }
            case "rm68zrgl":{
                if (entity instanceof Player player){
                    player.level().getEntitiesOfClass(Player.class,
                            new AABB(player.position().add(-5,-5,-5),
                                    player.position().add(5,5,5)))
                            .forEach(p->p.heal(2));
                }
                break;
            }
            case "pozhanzhe":{
                gunitem.getOrCreateTag().putFloat("multiple", Mth.clamp(gunitem.getOrCreateTag().getFloat("multiple")+0.1f,1.0f,3f));
                break;
            }
        }
    }
    public static void Skill4(ResourceLocation gunid, int pierce,Entity entity, Vec3 pos,Entity target){
        switch (gunid.getPath()){
            case "icedragon":if (pierce==1){
                BigDragonEntity skill=new BigDragonEntity(ModEntities.BD_Entity.get(), entity,pos);
                entity.level().addFreshEntity(skill);
                skill.setPos(target.position());
                skill.skill2();
                break;
            }
        }
    }
    public static void Skill5(ResourceLocation gunid, int pierce,Entity entity, Vec3 pos,Entity target){
        switch (gunid.getPath()){
            case "icedragon":if (pierce==1){
                BigDragonEntity skill=new BigDragonEntity(ModEntities.BD_Entity.get(), entity,pos);
                entity.level().addFreshEntity( skill);
                Vec3 lookVec = entity.getLookAngle();
                Vec3 behindVec = lookVec.reverse();
                skill.setPos(target.position().add(behindVec.scale(17)));
                skill.shoot(entity.getViewVector(0).x(),entity.getViewVector(0).y(),entity.getViewVector(0).z(),0.5f,0);
                skill.skill1();
                break;
            }
        }
    }
}

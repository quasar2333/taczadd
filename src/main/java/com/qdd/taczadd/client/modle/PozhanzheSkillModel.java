package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.PozhanzheSkillEntity;
import software.bernie.geckolib.model.GeoModel;

public class PozhanzheSkillModel extends GeoModel<PozhanzheSkillEntity> {
    private final ResourceLocation model = new ResourceLocation(Taczadd.MODID, "geo/pozhanzhe_skill.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Taczadd.MODID, "textures/entity/pozhanzhe_skill.png");
    private final ResourceLocation animation = new ResourceLocation(Taczadd.MODID, "animations/pozhanzhe_skill.animation.json");
    
    @Override
    public ResourceLocation getModelResource(PozhanzheSkillEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(PozhanzheSkillEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(PozhanzheSkillEntity animatable) {
        return animation;
    }
}



package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.SGSwordEntity;
import software.bernie.geckolib.model.GeoModel;

public class SGSwordModel extends GeoModel<SGSwordEntity> {
    private final ResourceLocation model = new ResourceLocation(Taczadd.MODID, "geo/sg_sword.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Taczadd.MODID, "textures/entity/sg_sword.png");
    private final ResourceLocation animation = new ResourceLocation(Taczadd.MODID, "animations/sg_sword.animation.json");

    @Override
    public ResourceLocation getModelResource(SGSwordEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SGSwordEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SGSwordEntity animatable) {
        return animation;
    }
}



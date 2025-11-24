package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.JxdfbEntity;
import software.bernie.geckolib.model.GeoModel;

public class JxdfbModel extends GeoModel<JxdfbEntity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/ak117jxdfb.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/ak117jxdfb.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/ak117jxdfb.animation.json");
    @Override
    public ResourceLocation getModelResource(JxdfbEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(JxdfbEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(JxdfbEntity animatable) {
        return animation;
    }
}

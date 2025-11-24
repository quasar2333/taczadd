package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.JxdfaEntity;
import software.bernie.geckolib.model.GeoModel;

public class JxdfaModel extends GeoModel<JxdfaEntity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/ak117jxdfa.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/ak117jxdfa.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/ak117jxdfa.animation.json");
    @Override
    public ResourceLocation getModelResource(JxdfaEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(JxdfaEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(JxdfaEntity animatable) {
        return animation;
    }
}

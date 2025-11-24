package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.IceCraterSmallEntity;
import software.bernie.geckolib.model.GeoModel;

public class ICSModel extends GeoModel<IceCraterSmallEntity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/ice_crater_small.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/ice_crater_small.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/ice_crater_small.animation.json");
    @Override
    public ResourceLocation getModelResource(IceCraterSmallEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(IceCraterSmallEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(IceCraterSmallEntity animatable) {
        return animation;
    }
}

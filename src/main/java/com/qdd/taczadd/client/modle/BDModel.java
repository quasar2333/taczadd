package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.BigDragonEntity;
import software.bernie.geckolib.model.GeoModel;

public class BDModel extends GeoModel<BigDragonEntity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/bigdragon.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/bigdragon.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/bigdragon.animation.json");
    @Override
    public ResourceLocation getModelResource(BigDragonEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(BigDragonEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(BigDragonEntity animatable) {
        return animation;
    }
}

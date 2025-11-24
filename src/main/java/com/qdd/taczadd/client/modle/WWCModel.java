package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.WatrtWavecEntity;
import software.bernie.geckolib.model.GeoModel;

public class WWCModel extends GeoModel <WatrtWavecEntity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/waterwavec.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/waterwave_charge.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/waterwavec.animation.json");
    @Override
    public ResourceLocation getModelResource(WatrtWavecEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(WatrtWavecEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WatrtWavecEntity animatable) {
        return animation;
    }
}

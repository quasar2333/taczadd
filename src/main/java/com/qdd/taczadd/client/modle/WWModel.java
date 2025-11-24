package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.BigDragonEntity;
import com.qdd.taczadd.entity.WaterWaveEntity;
import software.bernie.geckolib.model.GeoModel;

public class WWModel extends GeoModel<WaterWaveEntity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/waterwave.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/waterwave.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/waterwave.animation.json");
    @Override
    public ResourceLocation getModelResource(WaterWaveEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(WaterWaveEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WaterWaveEntity animatable) {
        return animation;
    }
}

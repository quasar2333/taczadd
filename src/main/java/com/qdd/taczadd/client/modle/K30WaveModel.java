package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.K30WaveEntity;
import software.bernie.geckolib.model.GeoModel;

public class K30WaveModel extends GeoModel<K30WaveEntity> {
    private final ResourceLocation model = new ResourceLocation(Taczadd.MODID, "geo/k30_wave.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Taczadd.MODID, "textures/entity/k30_wave.png");
    private final ResourceLocation animation = new ResourceLocation(Taczadd.MODID, "animations/k30_wave.animation.json");

    @Override
    public ResourceLocation getModelResource(K30WaveEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(K30WaveEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(K30WaveEntity animatable) {
        return animation;
    }
}



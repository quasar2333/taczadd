package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.Zmzy5Entity;
import software.bernie.geckolib.model.GeoModel;

public class Zmzy5Model extends GeoModel<Zmzy5Entity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/zmzy5.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/zmzy5.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/zmzy5.animation.json");
    @Override
    public ResourceLocation getModelResource(Zmzy5Entity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(Zmzy5Entity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(Zmzy5Entity animatable) {
        return animation;
    }
}
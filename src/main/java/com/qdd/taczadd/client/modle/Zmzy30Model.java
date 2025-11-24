package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.Zmzy30Entity;
import software.bernie.geckolib.model.GeoModel;

public class Zmzy30Model extends GeoModel<Zmzy30Entity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/zmzy30.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/zmzy30.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/zmzy30.animation.json");
    @Override
    public ResourceLocation getModelResource(Zmzy30Entity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(Zmzy30Entity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(Zmzy30Entity animatable) {
        return animation;
    }
}
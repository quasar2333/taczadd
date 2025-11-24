package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.GreatswordEntity;
import software.bernie.geckolib.model.GeoModel;

public class GWModel extends GeoModel<GreatswordEntity> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/greatsword.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/greatsword.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/greatsword.animation.json");
    @Override
    public ResourceLocation getModelResource(GreatswordEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GreatswordEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GreatswordEntity animatable) {
        return animation;
    }
}

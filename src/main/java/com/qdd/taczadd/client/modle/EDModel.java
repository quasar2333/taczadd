package com.qdd.taczadd.client.modle;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.entity.EndDragonGun;
import software.bernie.geckolib.model.GeoModel;

public class EDModel extends GeoModel<EndDragonGun> {
    private final ResourceLocation model=new ResourceLocation(Taczadd.MODID,"geo/enddragon.geo.json");
    private final ResourceLocation texture=new ResourceLocation(Taczadd.MODID,"textures/entity/enddragon.png");
    private final ResourceLocation animation=new ResourceLocation(Taczadd.MODID,"animations/enddragon.animation.json");
    @Override
    public ResourceLocation getModelResource(EndDragonGun animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(EndDragonGun animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(EndDragonGun animatable) {
        return animation;
    }
}
package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.ICSModel;
import com.qdd.taczadd.entity.IceCraterSmallEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ICSRender extends GeoEntityRenderer<IceCraterSmallEntity> {
    public ICSRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ICSModel());
    }
}

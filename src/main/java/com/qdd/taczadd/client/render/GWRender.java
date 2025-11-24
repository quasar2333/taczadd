package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.GWModel;
import com.qdd.taczadd.client.modle.ICSModel;
import com.qdd.taczadd.entity.GreatswordEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GWRender extends GeoEntityRenderer<GreatswordEntity> {
    public GWRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GWModel());
    }
}

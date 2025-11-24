package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.WWCModel;
import com.qdd.taczadd.entity.WatrtWavecEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WWCRender extends GeoEntityRenderer<WatrtWavecEntity> {
    public WWCRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WWCModel());
    }
}

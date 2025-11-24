package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.JxdfaModel;
import com.qdd.taczadd.client.modle.JxdfbModel;
import com.qdd.taczadd.entity.JxdfaEntity;
import com.qdd.taczadd.entity.JxdfbEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JxdfbRender  extends GeoEntityRenderer<JxdfbEntity> {
    public JxdfbRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new JxdfbModel());
    }
}
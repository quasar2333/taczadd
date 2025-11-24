package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.PozhanzheSkillModel;
import com.qdd.taczadd.entity.PozhanzheSkillEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PozhanzheSkillRender extends GeoEntityRenderer<PozhanzheSkillEntity> {
    public PozhanzheSkillRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PozhanzheSkillModel());
    }
}



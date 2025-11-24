package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.Zmzy5Model;
import com.qdd.taczadd.entity.Zmzy5Entity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Zmzy5Render extends GeoEntityRenderer<Zmzy5Entity> {
    public Zmzy5Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Zmzy5Model());
        this.shadowRadius = 0.4f;
    }
}

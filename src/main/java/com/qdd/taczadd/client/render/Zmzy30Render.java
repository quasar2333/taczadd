package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.Zmzy30Model;
import com.qdd.taczadd.entity.Zmzy30Entity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Zmzy30Render extends GeoEntityRenderer<Zmzy30Entity> {
    public Zmzy30Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Zmzy30Model());
        // slightly scale up to avoid tiny pose-looking bug where the model appears static
        this.shadowRadius = 0.5f;
    }
}
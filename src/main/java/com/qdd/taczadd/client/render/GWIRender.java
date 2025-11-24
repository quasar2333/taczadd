package com.qdd.taczadd.client.render;

import net.minecraft.resources.ResourceLocation;
import com.qdd.taczadd.Taczadd;
import com.qdd.taczadd.item.GWItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GWIRender extends GeoItemRenderer<GWItem>{
    public GWIRender() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(Taczadd.MODID, "greatsworditem")));
    }
}

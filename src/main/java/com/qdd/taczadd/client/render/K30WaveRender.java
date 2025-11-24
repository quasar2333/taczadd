package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.K30WaveModel;
import com.qdd.taczadd.entity.K30WaveEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;

public class K30WaveRender extends GeoEntityRenderer<K30WaveEntity> {
    public K30WaveRender(EntityRendererProvider.Context ctx) {
        super(ctx, new K30WaveModel());
    }

    @Override
    protected void applyRotations(K30WaveEntity e, PoseStack stack,
                                  float ageInTicks, float rotationYawIgnored, float partialTick) {


        float yaw   = Mth.lerp(partialTick, e.yRotO, e.getYRot());
        float pitch = Mth.lerp(partialTick, e.xRotO, e.getXRot());


        stack.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
        stack.mulPose(Axis.XP.rotationDegrees(pitch));


        stack.translate(0.0F, -2.0F, 5.0F);
        stack.scale(2.0f, 2.0f, 2.0f);

    }

}

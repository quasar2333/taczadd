package com.qdd.taczadd.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qdd.taczadd.client.modle.JxdfaModel;
import com.qdd.taczadd.entity.JxdfaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JxdfaRender  extends GeoEntityRenderer<JxdfaEntity> {
    public JxdfaRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new JxdfaModel());
    }

    @Override
    protected void applyRotations(JxdfaEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
                                  float partialTick) {
//        poseStack.pushPose();

        // 1. 获取插值后的实际旋转（避免抖动）
        float renderYaw = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot());
        float renderPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());

        // 2. 应用旋转（Y轴取反+180°修正坐标系）
        poseStack.mulPose(Axis.YP.rotationDegrees(renderYaw -180));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderPitch));
//        super.applyRotations(animatable,poseStack,ageInTicks,rotationYaw,partialTick);
//        poseStack.popPose();
    }
}
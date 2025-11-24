package com.qdd.taczadd.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.SGSwordModel;
import com.qdd.taczadd.entity.SGSwordEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SGSwordRender extends GeoEntityRenderer<SGSwordEntity> {
    public SGSwordRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SGSwordModel());
    }


    @Override
    public void render(SGSwordEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {


        poseStack.pushPose();



        // --- 缩放模型 ---

        float scale = 2.0f;
        poseStack.scale(scale, scale, scale);

        // --- 移动模型 ---


        double offsetX = 0.0;
        double offsetY = -0.5;
        double offsetZ = 0.0;
        poseStack.translate(offsetX, offsetY, offsetZ);


        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);


        poseStack.popPose();
    }
}
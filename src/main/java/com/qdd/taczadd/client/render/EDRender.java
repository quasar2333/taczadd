package com.qdd.taczadd.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.qdd.taczadd.client.modle.EDModel;
import com.qdd.taczadd.entity.EndDragonGun;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class EDRender extends GeoEntityRenderer<EndDragonGun> {
    public EDRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EDModel());
    }

    @Override
    protected void applyRotations(EndDragonGun animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
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

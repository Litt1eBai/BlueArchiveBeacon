package net.littlebai.bluearchivebeacon.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BeaconHaloRenderer {

    public static void renderBeaconHalo(BeaconBlockEntity blockEntity,
                                       float partialTick,
                                       PoseStack poseStack,
                                       MultiBufferSource bufferSource,
                                       int packedLight,
                                       int packedOverlay) {
        Vec3 circleCenter = new Vec3(blockEntity.getBlockPos().getX(), 256, blockEntity.getBlockPos().getZ());
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        // TODO: 从配置文件读取颜色设置
        float red = Color.WHITE.getRed() / 255.0f;
        float green = Color.WHITE.getGreen() / 255.0f;
        float blue = Color.WHITE.getBlue() / 255.0f;
        float alpha = 0.85f;
        
        float underwaterFogFactor = calculateUnderwaterFogFactor(cameraPos, circleCenter);
        alpha *= underwaterFogFactor;

        // TODO: 从配置文件读取光环数量和分段数
        int haloCount = 5;
        int segments = 240;

        for (int i = 0; i < haloCount; i++) {
            renderSingleHalo(poseStack, cameraPos, circleCenter, i, segments, red, green, blue, alpha);
        }
    }

    private static void renderSingleHalo(PoseStack poseStack, Vec3 cameraPos, Vec3 circleCenter, 
                                        int haloIndex, int segments, 
                                        float red, float green, float blue, float alpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        poseStack.pushPose();
        poseStack.translate(circleCenter.x - cameraPos.x,
                circleCenter.y - cameraPos.y,
                circleCenter.z - cameraPos.z);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);

        double radius = getRadiusGolden(haloIndex);
        for (int j = 0; j < segments; j++) {
            double angle = 2.0 * Math.PI * j / segments;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Vector4f worldPos = new Vector4f((float) x, (float) circleCenter.y, (float) z, 1.0f);
            worldPos.mul(poseStack.last().pose());

            buffer.addVertex(worldPos.x, worldPos.y, worldPos.z).setColor(red, green, blue, alpha);
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    private static double getRadiusGolden(int haloCount) {
        double base = 160;
        double interval = 16;
        double goldenRatio = 1.618;
        
        int group = haloCount / 2;
        int inGroupIndex = haloCount % 2;
        
        double groupBaseRadius;
        
        if (group == 0) {
            // 第一组基础半径
            groupBaseRadius = base;
        } else if (group == 1) {
            // 第二组基础半径 × 黄金比例
            groupBaseRadius = base * goldenRatio;
        } else {
            // 第三组及以后的所有组递归计算上一组的最大半径, 然后 * 2
            double prevGroupMaxRadius = getRadiusGolden((group - 1) * 2 + 1);
            groupBaseRadius = prevGroupMaxRadius * 2.0;
        }
        
        // 组内第二个光环在组基础半径上加组间距
        return groupBaseRadius + (inGroupIndex * interval);
    }

    private static float calculateUnderwaterFogFactor(Vec3 cameraPos, Vec3 lightPos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 1.0f;

        BlockPos cameraBlockPos = BlockPos.containing(cameraPos);
        FluidState cameraFluidState = mc.level.getFluidState(cameraBlockPos);
        boolean cameraInWater = !cameraFluidState.isEmpty() && cameraFluidState.getType() == Fluids.WATER;
        
        if (!cameraInWater) {
            return 1.0f;
        }

        double distance = cameraPos.distanceTo(lightPos);
        float baseFogFactor = (float) Math.exp(-distance * 0.005); // 距离衰减
        double waterSurfaceY = findWaterSurface(cameraBlockPos);
        double waterDepth = Math.max(0, waterSurfaceY - cameraPos.y);
        float depthFogFactor = (float) Math.exp(-waterDepth * 0.1); // 深度衰减
        float fogFactor = baseFogFactor * depthFogFactor;

        return Math.max(0.1f, Math.min(1.0f, fogFactor));
    }

    private static double findWaterSurface(BlockPos startPos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return startPos.getY();
        
        // 从当前位置向上寻找水面
        for (int y = startPos.getY(); y <= mc.level.getMaxBuildHeight(); y++) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            FluidState fluidState = mc.level.getFluidState(checkPos);
            
            // 如果这个位置没有水，说明找到了水面
            if (fluidState.isEmpty() || fluidState.getType() != Fluids.WATER) {
                return y - 1; // 返回水面位置
            }
        }
        
        return startPos.getY(); // 找不到水面，返回原始高度
    }
} 
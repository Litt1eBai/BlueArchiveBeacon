package net.littlebai.bluearchivebeacon.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class BeaconHaloRenderer {

    private BeaconHaloRenderer() {}

    public static void drawBeaconHalo(BeaconBlockEntity blockEntity,
                                      PoseStack poseStack,
                                      Vec3 cameraPos) {
        Vec3 circleCenter = new Vec3(blockEntity.getBlockPos().getX(), 512, blockEntity.getBlockPos().getZ());

        // 计算颜色
        // 根据游戏时间计算动态颜色
        float[] haloColor = calculateTimeBasedColor(blockEntity);
        float red = haloColor[0];
        float green = haloColor[1];
        float blue = haloColor[2];
        float alpha = 0.85f;

        // 水下雾化效果
        float underwaterFogFactor = calculateUnderwaterFogFactor(cameraPos, circleCenter);
        alpha *= underwaterFogFactor;

        // TODO: 从配置文件读取光环数量和分段数
        int haloCount = 5;
        int segments = 240;

        for (int i = 0; i < haloCount; i++) {
            drawSingleHalo(poseStack, cameraPos, circleCenter, i, segments, red, green, blue, alpha);
        }
    }

    private static void drawSingleHalo(PoseStack poseStack, Vec3 cameraPos, Vec3 circleCenter,
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
        for (int j = 0; j <= segments; j++) {
            double angle = 2.0 * Math.PI * j / segments;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Vector4f worldPos = new Vector4f((float) x, 0, (float) z, 1.0f);
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

    /**
     * 根据游戏时间计算光环颜色
     * 正午为白色，午夜为淡蓝色，平滑渐变
     * @param blockEntity 信标方块实体
     * @return RGB颜色数组 [r, g, b]
     */
    private static float[] calculateTimeBasedColor(BeaconBlockEntity blockEntity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return new float[]{1.0f, 1.0f, 1.0f}; // 默认白色
        }

        // 获取游戏时间（一天 = 24000 刻）
        long worldTime = mc.level.getDayTime();
        long dayTime = worldTime % 24000L; // 获取当天时间

        // 定义颜色关键点
        // 正午 (6000 刻): 白色
        float noonRed = 1.0f, noonGreen = 1.0f, noonBlue = 1.0f;
        // 午夜 (18000 刻): 淡蓝色
        float midnightRed = 0.7f, midnightGreen = 0.8f, midnightBlue = 1.0f;

        // 计算时间因子 (0.0 - 1.0)
        // 使用余弦函数创建平滑的昼夜循环
        double timeRadians = (dayTime / 24000.0) * 2.0 * Math.PI;
        
        // 将时间映射到颜色插值因子
        // cos(0) = 1 (正午), cos(π) = -1 (午夜)
        double cosineTime = Math.cos(timeRadians - Math.PI / 2.0); // 偏移π/2使正午为峰值
        
        // 将 [-1, 1] 映射到 [0, 1]，其中0为午夜，1为正午
        float interpolationFactor = (float) ((cosineTime + 1.0) / 2.0);
        
        // 线性插值计算最终颜色
        float red = lerp(midnightRed, noonRed, interpolationFactor);
        float green = lerp(midnightGreen, noonGreen, interpolationFactor);
        float blue = lerp(midnightBlue, noonBlue, interpolationFactor);

        return new float[]{red, green, blue};
    }

    /**
     * 线性插值函数
     * @param start 起始值
     * @param end 结束值
     * @param factor 插值因子 (0.0 - 1.0)
     * @return 插值结果
     */
    private static float lerp(float start, float end, float factor) {
        return start + (end - start) * factor;
    }
} 
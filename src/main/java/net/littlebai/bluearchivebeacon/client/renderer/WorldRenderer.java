package net.littlebai.bluearchivebeacon.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class WorldRenderer {

    private Vec3 cameraPos;

    public static final WorldRenderer INSTANCE = new WorldRenderer();
    
    private WorldRenderer() {}

    public void renderWorldLast(PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Camera camera, DeltaTracker tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.options.hideGui || mc.level == null || mc.player == null) {
            return;
        }
        
        cameraPos = camera.getPosition();
        
        for (BeaconBlockEntity beacon : BeaconTracker.getAllBeacons()) {
            if (beacon != null && shouldRenderBeacon(beacon, cameraPos)) {
                BeaconHaloRenderer.drawBeaconHalo(beacon, poseStack, cameraPos);
//                renderBeaconHalo(beacon, poseStack, cameraPos, tickDelta.getGameTimeDeltaTicks());
            }
        }
    }

    private boolean shouldRenderBeacon(BeaconBlockEntity beacon, Vec3 cameraPos) {
        double distance = cameraPos.distanceTo(Vec3.atCenterOf(beacon.getBlockPos()));
        double maxRenderDistance = 4096;
        
        return distance <= maxRenderDistance;
    }
    

//    private void renderBeaconHalo(BeaconBlockEntity beacon, PoseStack poseStack, Vec3 cameraPos, float partialTick) {
//        poseStack.pushPose();
//
//        BeaconHaloRenderer.drawBeaconHalo(beacon, partialTick, poseStack,
//            Minecraft.getInstance().renderBuffers().bufferSource(),
//            15728880,
//            10);
//
//        poseStack.popPose();
//    }
} 
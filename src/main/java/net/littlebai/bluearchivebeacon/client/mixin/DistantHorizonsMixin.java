package net.littlebai.bluearchivebeacon.client.mixin;

import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.render.renderer.generic.BeaconRenderHandler;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import net.littlebai.bluearchivebeacon.client.renderer.BeaconTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconRenderHandler.class)
public class DistantHorizonsMixin {

    @Inject(method = "startRenderingBeacon", at = @At("TAIL"), remap = false)
    private void startRenderingBeacon(BeaconBeamDTO beacon, CallbackInfo ci) {
        BlockPos blockPos = new BlockPos(beacon.blockPos.getX(), beacon.blockPos.getY(), beacon.blockPos.getZ());
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.isLoaded(blockPos)) {
            BlockEntity blockEntity = mc.level.getBlockEntity(blockPos);
            if (blockEntity instanceof BeaconBlockEntity) {
                BeaconTracker.addBeacon((BeaconBlockEntity) blockEntity);
            }
        }
    }

    @Inject(method = "stopRenderingBeaconAtPos", at = @At("TAIL"))
    private void stopRenderingBeacon(DhBlockPos beaconPos, CallbackInfo ci) {
        BlockPos blockPos = new BlockPos(beaconPos.getX(), beaconPos.getY(), beaconPos.getZ());
        BeaconTracker.removeBeacon(blockPos);
    }
}
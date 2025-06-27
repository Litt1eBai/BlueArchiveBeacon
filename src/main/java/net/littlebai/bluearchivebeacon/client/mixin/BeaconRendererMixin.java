package net.littlebai.bluearchivebeacon.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.littlebai.bluearchivebeacon.client.renderer.BeaconHaloRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconRenderer.class)
public class BeaconRendererMixin {
    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;renderBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FJIII)V",shift = At.Shift.AFTER))
    private void render(BeaconBlockEntity blockEntity,
                        float partialTick,
                        PoseStack poseStack,
                        MultiBufferSource bufferSource,
                        int packedLight,
                        int packedOverlay,
                        CallbackInfo ci){

        boolean shouldRenderHalo = true;
        
        if (shouldRenderHalo) {
            BeaconHaloRenderer.drawBeaconHalo(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    @Inject(method = "getViewDistance",at = @At("RETURN"),cancellable = true)
    private void viewDistance(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(2048);
    }
}


package net.littlebai.bluearchivebeacon.client.events;

import net.littlebai.bluearchivebeacon.BlueArchiveBeacon;
import net.littlebai.bluearchivebeacon.client.renderer.WorldRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = BlueArchiveBeacon.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            WorldRenderer.INSTANCE.renderWorldLast(
                event.getPoseStack(), 
                event.getProjectionMatrix(),
                event.getModelViewMatrix(), 
                event.getCamera(), 
                event.getPartialTick()
            );
        }
    }
} 
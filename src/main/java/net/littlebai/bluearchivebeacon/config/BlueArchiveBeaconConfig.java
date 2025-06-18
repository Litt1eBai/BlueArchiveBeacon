package net.littlebai.bluearchivebeacon.config;

import net.littlebai.bluearchivebeacon.Bluearchivebeacon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Bluearchivebeacon.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BlueArchiveBeaconConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
    }
}

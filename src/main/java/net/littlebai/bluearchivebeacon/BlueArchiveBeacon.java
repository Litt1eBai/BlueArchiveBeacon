package net.littlebai.bluearchivebeacon;

import com.mojang.logging.LogUtils;
import net.littlebai.bluearchivebeacon.client.config.BaBeaconBeaconConfig;
import net.littlebai.bluearchivebeacon.client.screen.BaBeaconConfigurationScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(BlueArchiveBeacon.MODID)
public class BlueArchiveBeacon {
    public static final String MODID = "bluearchivebeacon";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BlueArchiveBeacon(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.CLIENT, BaBeaconBeaconConfig.SPEC);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>) BaBeaconConfigurationScreen::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}

package net.littlebai.bluearchivebeacon.config;

import net.littlebai.bluearchivebeacon.Bluearchivebeacon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Bluearchivebeacon.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BlueArchiveBeaconConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_RENDERING = BUILDER
            .comment("Whether to enable halo rendering")
            .translation("configuration.bluearchivebeacon.enableRendering")
            .define("enableRendering", true);

    public static final ModConfigSpec.EnumValue<HaloColorMode> HALO_COLOR_MODE = BUILDER
            .comment("Halo color mode")
            .translation("configuration.bluearchivebeacon.haloColorMode")
            .defineEnum("haloColorMode", HaloColorMode.BA_STYLE);

    public static final ModConfigSpec.IntValue FIXED_COLOR_RED = BUILDER
            .comment("Red component for fixed color mode (0-255)")
            .translation("configuration.bluearchivebeacon.fixedColorRed")
            .defineInRange("fixedColorRed", 255, 0, 255);

    public static final ModConfigSpec.IntValue FIXED_COLOR_GREEN = BUILDER
            .comment("Green component for fixed color mode (0-255)")
            .translation("configuration.bluearchivebeacon.fixedColorGreen")
            .defineInRange("fixedColorGreen", 255, 0, 255);

    public static final ModConfigSpec.IntValue FIXED_COLOR_BLUE = BUILDER
            .comment("Blue component for fixed color mode (0-255)")
            .translation("configuration.bluearchivebeacon.fixedColorBlue")
            .defineInRange("fixedColorBlue", 255, 0, 255);

    public static final ModConfigSpec.EnumValue<HaloCountMode> HALO_COUNT_MODE = BUILDER
            .comment("Halo count mode")
            .translation("configuration.bluearchivebeacon.haloCountMode")
            .defineEnum("haloCountMode", HaloCountMode.FOLLOW_BEACON_LEVEL);

    public static final ModConfigSpec.IntValue CUSTOM_HALO_COUNT = BUILDER
            .comment("Number of halos for custom count mode")
            .translation("configuration.bluearchivebeacon.customHaloCount")
            .defineInRange("customHaloCount", 5, 1, 20);

    public static final ModConfigSpec.IntValue BASE_RADIUS = BUILDER
            .comment("Base radius for halos")
            .translation("configuration.bluearchivebeacon.baseRadius")
            .defineInRange("baseRadius", 160, 50, 1000);

    public static final ModConfigSpec.IntValue GROUP_INTERVAL = BUILDER
            .comment("Interval between halo groups")
            .translation("configuration.bluearchivebeacon.groupInterval")
            .defineInRange("groupInterval", 16, 1, 100);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public enum HaloColorMode {
        BA_STYLE("ba_style"),           // Blue Archive style
        FIXED("fixed"),                 // Fixed color
        FOLLOW_BEACON("follow_beacon"); // Follow beacon beam color

        private final String name;

        HaloColorMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum HaloCountMode {
        FIXED("fixed"),                    // Fixed count
        CUSTOM("custom"),                  // Custom count
        FOLLOW_BEACON_LEVEL("follow_level"); // Follow beacon level

        private final String name;

        HaloCountMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
    }
}

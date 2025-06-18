package net.littlebai.bluearchivebeacon.client.config;

import net.littlebai.bluearchivebeacon.BlueArchiveBeacon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BlueArchiveBeacon.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BaBeaconBeaconConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_RENDERING;
    
    public static final ModConfigSpec.EnumValue<HaloColorMode> HALO_COLOR_MODE;
    public static final ModConfigSpec.ConfigValue<Integer> FIXED_COLOR_RED;
    public static final ModConfigSpec.ConfigValue<Integer> FIXED_COLOR_GREEN;
    public static final ModConfigSpec.ConfigValue<Integer> FIXED_COLOR_BLUE;
    
    public static final ModConfigSpec.EnumValue<HaloCountMode> HALO_COUNT_MODE;
    public static final ModConfigSpec.ConfigValue<Integer> CUSTOM_HALO_COUNT;
    public static final ModConfigSpec.ConfigValue<Integer> BASE_RADIUS;
    public static final ModConfigSpec.ConfigValue<Integer> GROUP_INTERVAL;

    static {
        BUILDER.push("general");
        
        ENABLE_RENDERING = BUILDER
                .comment("Whether to enable halo rendering")
                .translation("configuration.bluearchivebeacon.enableRendering")
                .define("enableRendering", true);
        
        BUILDER.pop();
        
        BUILDER.push("color");
        
        HALO_COLOR_MODE = BUILDER
                .comment("Halo color mode")
                .translation("configuration.bluearchivebeacon.haloColorMode")
                .defineEnum("haloColorMode", HaloColorMode.BA_STYLE);
        
        FIXED_COLOR_RED = BUILDER
                .comment("Red component for fixed color mode (0-255)")
                .translation("configuration.bluearchivebeacon.fixedColorRed")
                .define("fixedColorRed", 255);
        
        FIXED_COLOR_GREEN = BUILDER
                .comment("Green component for fixed color mode (0-255)")
                .translation("configuration.bluearchivebeacon.fixedColorGreen")
                .define("fixedColorGreen", 255);
        
        FIXED_COLOR_BLUE = BUILDER
                .comment("Blue component for fixed color mode (0-255)")
                .translation("configuration.bluearchivebeacon.fixedColorBlue")
                .define("fixedColorBlue", 255);
        
        BUILDER.pop();
        
        BUILDER.push("appearance");
        
        HALO_COUNT_MODE = BUILDER
                .comment("Halo count mode")
                .translation("configuration.bluearchivebeacon.haloCountMode")
                .defineEnum("haloCountMode", HaloCountMode.FOLLOW_BEACON_LEVEL);
        
        CUSTOM_HALO_COUNT = BUILDER
                .comment("Number of halos for custom count mode")
                .translation("configuration.bluearchivebeacon.customHaloCount")
                .define("customHaloCount", 5);
        
        BASE_RADIUS = BUILDER
                .comment("Base radius for halos")
                .translation("configuration.bluearchivebeacon.baseRadius")
                .define("baseRadius", 160);
        
        GROUP_INTERVAL = BUILDER
                .comment("Interval between halo groups")
                .translation("configuration.bluearchivebeacon.groupInterval")
                .define("groupInterval", 16);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }

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

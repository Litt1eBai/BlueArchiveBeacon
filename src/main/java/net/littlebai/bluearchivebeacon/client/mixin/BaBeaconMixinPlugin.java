package net.littlebai.bluearchivebeacon.client.mixin;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BaBeaconMixinPlugin implements IMixinConfigPlugin {

    private static final String DISTANT_HORIZONS = "com.seibel.distanthorizons.neoforge.NeoforgeMain";
    private boolean isDistantHorizonsLoaded = false;
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onLoad(String mixinPackage) {
        this.isDistantHorizonsLoaded = isModLoaded(DISTANT_HORIZONS);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        if (mixinClassName.startsWith("net.littlebai.bluearchivebeacon.client.mixin.DistantHorizonsMixin")) {
            return this.isDistantHorizonsLoaded;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static boolean isModLoaded(String className) {
        try {
            Class.forName(className);
            LOGGER.debug("Successfully found class '{}', assuming its mod is loaded.", className);
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Could not find class '{}', assuming its mod is not loaded.", className);
            return false;
        }
    }
}
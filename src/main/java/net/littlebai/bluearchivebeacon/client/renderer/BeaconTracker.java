package net.littlebai.bluearchivebeacon.client.renderer;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Collection;

public class BeaconTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static final Map<BlockPos, BeaconBlockEntity> trackedBeacons = new ConcurrentHashMap<>();

    public static void addBeacon(BeaconBlockEntity blockEntity) {
        if (blockEntity == null) {
            return;
        }
        
        BlockPos pos = blockEntity.getBlockPos();
        BeaconBlockEntity existing = trackedBeacons.put(pos, blockEntity);
        
        if (existing == null) {
            LOGGER.debug("Add beacon to tracking list: {}", pos);
        }
    }

    public static void removeBeacon(BlockPos pos) {
        if (pos == null) {
            return;
        }
        
        BeaconBlockEntity removed = trackedBeacons.remove(pos);
        if (removed != null) {
            LOGGER.debug("Remove beacon from tracking list: {}", pos);
        }
    }

    public static Collection<BeaconBlockEntity> getAllBeacons() {
        return trackedBeacons.values();
    }

    public static boolean isBeaconTracked(BlockPos pos) {
        return trackedBeacons.containsKey(pos);
    }

    public static BeaconBlockEntity getBeacon(BlockPos pos) {
        return trackedBeacons.get(pos);
    }

    public static void clearAllBeacons() {
        int count = trackedBeacons.size();
        trackedBeacons.clear();
        if (count > 0) {
            LOGGER.debug("Cleared {} tracked beacons", count);
        }
    }

    public static int getTrackedBeaconCount() {
        return trackedBeacons.size();
    }

    public static void updateBeacon(BeaconBlockEntity blockEntity) {
        if (blockEntity == null) {
            return;
        }
        
        BlockPos pos = blockEntity.getBlockPos();
        if (trackedBeacons.containsKey(pos)) {
            trackedBeacons.put(pos, blockEntity);
            LOGGER.debug("Update tracked beacon: {}", pos);
        }
    }
} 
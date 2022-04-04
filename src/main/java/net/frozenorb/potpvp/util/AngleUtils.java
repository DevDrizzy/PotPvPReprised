package net.frozenorb.potpvp.util;

import org.bukkit.block.BlockFace;

import java.util.EnumMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class AngleUtils {

    private static final Map<BlockFace, Integer> NOTCHES = new EnumMap<>(BlockFace.class);

    static {
        BlockFace[] radials = {
                BlockFace.WEST,
                BlockFace.NORTH_WEST,
                BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST
        };

        for (int i = 0; i < radials.length; i++) {
            NOTCHES.put(radials[i], i);
        }
    }

    public static int faceToYaw(BlockFace face) {
        return wrapAngle(45 * NOTCHES.getOrDefault(face, 0));
    }

    private static int wrapAngle(int angle) {
        int wrappedAngle = angle;

        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }

        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

}
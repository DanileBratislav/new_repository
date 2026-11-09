package com.danilebratislav.worldeditmod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class RegionSnapshot {
    private final List<BlockPos> positions = new ArrayList<>();
    private final List<BlockState> states = new ArrayList<>();

    private RegionSnapshot() {
    }

    public static RegionSnapshot capture(Level level, BlockPos min, BlockPos max) {
        RegionSnapshot snapshot = new RegionSnapshot();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    mutable.set(x, y, z);
                    snapshot.positions.add(mutable.immutable());
                    snapshot.states.add(level.getBlockState(mutable));
                }
            }
        }

        return snapshot;
    }

    public void restore(Level level) {
        for (int i = 0; i < positions.size(); i++) {
            level.setBlock(positions.get(i), states.get(i), 3);
        }
    }
}

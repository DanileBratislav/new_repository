package com.danilebratislav.worldeditmod;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class SelectionManager {
    private static final Map<UUID, RegionSelection> SELECTIONS = new HashMap<>();

    private SelectionManager() {
    }

    public static RegionSelection getOrCreate(ServerPlayer player) {
        return SELECTIONS.computeIfAbsent(player.getUUID(), ignored -> new RegionSelection());
    }

    public static RegionSelection get(ServerPlayer player) {
        return SELECTIONS.get(player.getUUID());
    }

    public static void clear(ServerPlayer player) {
        SELECTIONS.remove(player.getUUID());
    }
}

class RegionSelection {
    private BlockPos pos1;
    private BlockPos pos2;
    private final Deque<RegionSnapshot> history = new ArrayDeque<>();

    public void setPos1(BlockPos pos) {
        this.pos1 = pos;
    }

    public void setPos2(BlockPos pos) {
        this.pos2 = pos;
    }

    public BlockPos getPos1() {
        return pos1;
    }

    public BlockPos getPos2() {
        return pos2;
    }

    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }

    public BlockPos getMin() {
        if (!isComplete()) {
            throw new IllegalStateException("Selection is incomplete");
        }
        return new BlockPos(
            Math.min(pos1.getX(), pos2.getX()),
            Math.min(pos1.getY(), pos2.getY()),
            Math.min(pos1.getZ(), pos2.getZ())
        );
    }

    public BlockPos getMax() {
        if (!isComplete()) {
            throw new IllegalStateException("Selection is incomplete");
        }
        return new BlockPos(
            Math.max(pos1.getX(), pos2.getX()),
            Math.max(pos1.getY(), pos2.getY()),
            Math.max(pos1.getZ(), pos2.getZ())
        );
    }

    public void clear() {
        pos1 = null;
        pos2 = null;
        history.clear();
    }

    public void rememberSnapshot(Level level) {
        if (!isComplete()) {
            return;
        }

        BlockPos min = getMin();
        BlockPos max = getMax();
        history.addFirst(RegionSnapshot.capture(level, min, max));

        while (history.size() > 25) {
            history.removeLast();
        }
    }

    public void undo(Level level) {
        if (!history.isEmpty()) {
            RegionSnapshot snapshot = history.removeFirst();
            snapshot.restore(level);
        }
    }
}

package com.danilebratislav.worldeditmod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldEditMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AxeSelectionHandler {
    private AxeSelectionHandler() {
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getItemStack().is(Items.WOODEN_AXE)
                || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        event.setCanceled(true);
        var pos = event.getPos().immutable();
        SelectionManager.getOrCreate(player).setPos1(pos);
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Pos1 set to " + pos.toShortString()), false);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(Items.WOODEN_AXE)
                || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        event.setCanceled(true);
        var pos = event.getPos().immutable();
        SelectionManager.getOrCreate(player).setPos2(pos);
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Pos2 set to " + pos.toShortString()), false);
    }
}

package com.danilebratislav.worldeditmod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

public class WandItem extends Item {
    public WandItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        BlockPos clicked = context.getClickedPos();
        RegionSelection selection = SelectionManager.getOrCreate(serverPlayer);

        if (player.isShiftKeyDown()) {
            selection.setPos1(clicked);
            serverPlayer.displayClientMessage(Component.literal("Pos1 set to " + clicked.toShortString()), false);
        } else {
            selection.setPos2(clicked);
            serverPlayer.displayClientMessage(Component.literal("Pos2 set to " + clicked.toShortString()), false);
        }

        return InteractionResult.CONSUME;
    }
}

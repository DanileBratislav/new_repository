package com.danilebratislav.worldeditmod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldEditMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModCommands {
    private ModCommands() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("we")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("wand").executes(ModCommands::giveWand))
                .then(Commands.literal("pos1").executes(context -> setPos(context.getSource(), true)))
                .then(Commands.literal("pos2").executes(context -> setPos(context.getSource(), false)))
                .then(Commands.literal("clear").executes(ModCommands::clearSelection))
                .then(Commands.literal("set")
                    .then(Commands.argument("block", ResourceLocationArgument.id())
                        .executes(ModCommands::setRegion)
                    )
                )
                .then(Commands.literal("replace")
                    .then(Commands.argument("from", ResourceLocationArgument.id())
                        .then(Commands.argument("to", ResourceLocationArgument.id())
                            .executes(ModCommands::replaceRegion)
                        )
                    )
                )
                .then(Commands.literal("undo").executes(ModCommands::undoRegion))
        );
    }

    private static int giveWand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getInventory().add(new net.minecraft.world.item.ItemStack(ModItems.WAND.get()));
        context.getSource().sendSuccess(() -> Component.literal("You received the selection wand."), false);
        return 1;
    }

    private static int setPos(CommandSourceStack source, boolean pos1) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        RegionSelection selection = SelectionManager.getOrCreate(player);
        BlockPos pos = player.blockPosition();

        if (pos1) {
            selection.setPos1(pos);
            source.sendSuccess(() -> Component.literal("Pos1 set to " + pos.toShortString()), false);
        } else {
            selection.setPos2(pos);
            source.sendSuccess(() -> Component.literal("Pos2 set to " + pos.toShortString()), false);
        }

        return 1;
    }

    private static int clearSelection(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        RegionSelection selection = SelectionManager.get(player);
        if (selection != null) {
            selection.clear();
        }
        context.getSource().sendSuccess(() -> Component.literal("Selection cleared."), false);
        return 1;
    }

    private static int setRegion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        RegionSelection selection = SelectionManager.get(player);
        if (selection == null || !selection.isComplete()) {
            throw new SimpleCommandExceptionType(Component.literal("Select a region first.")).create();
        }

        ResourceLocation blockId = ResourceLocationArgument.getId(context, "block");
        Block block = BuiltInRegistries.BLOCK.getOptional(blockId).orElseThrow(() ->
            new SimpleCommandExceptionType(Component.literal("Unknown block: " + blockId)).create());

        Level level = player.level();
        selection.rememberSnapshot(level);
        applyFill(level, selection, block.defaultBlockState());

        context.getSource().sendSuccess(() -> Component.literal("Filled region with " + blockId), false);
        return 1;
    }

    private static int replaceRegion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        RegionSelection selection = SelectionManager.get(player);
        if (selection == null || !selection.isComplete()) {
            throw new SimpleCommandExceptionType(Component.literal("Select a region first.")).create();
        }

        ResourceLocation fromId = ResourceLocationArgument.getId(context, "from");
        ResourceLocation toId = ResourceLocationArgument.getId(context, "to");

        Block fromBlock = BuiltInRegistries.BLOCK.getOptional(fromId).orElseThrow(() ->
            new SimpleCommandExceptionType(Component.literal("Unknown source block: " + fromId)).create());
        Block toBlock = BuiltInRegistries.BLOCK.getOptional(toId).orElseThrow(() ->
            new SimpleCommandExceptionType(Component.literal("Unknown target block: " + toId)).create());

        Level level = player.level();
        selection.rememberSnapshot(level);

        BlockPos min = selection.getMin();
        BlockPos max = selection.getMax();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    pos.set(x, y, z);
                    if (level.getBlockState(pos).is(fromBlock)) {
                        level.setBlock(pos, toBlock.defaultBlockState(), 3);
                    }
                }
            }
        }

        context.getSource().sendSuccess(() -> Component.literal("Replaced blocks from " + fromId + " to " + toId), false);
        return 1;
    }

    private static int undoRegion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        RegionSelection selection = SelectionManager.get(player);
        if (selection == null) {
            throw new SimpleCommandExceptionType(Component.literal("No region history found.")).create();
        }

        selection.undo(player.level());
        context.getSource().sendSuccess(() -> Component.literal("Undid last region operation."), false);
        return 1;
    }

    private static void applyFill(Level level, RegionSelection selection, BlockState state) {
        BlockPos min = selection.getMin();
        BlockPos max = selection.getMax();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    pos.set(x, y, z);
                    level.setBlock(pos, state, 3);
                }
            }
        }
    }
}

package com.danilebratislav.worldeditmod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WorldEditMod.MOD_ID)
public class WorldEditMod {
    public static final String MOD_ID = "worldeditmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WorldEditMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modBus);
        MinecraftForge.EVENT_BUS.register(ModCommands.class);
    }
}

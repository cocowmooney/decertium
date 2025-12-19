package com.coenmooney.decertium.world.dimension;

import com.coenmooney.decertium.DecertiumMod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class DecertiumDimensions {
    public static final ResourceKey<Level> DARK_WORLD = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(DecertiumMod.MOD_ID, "dark_world"));
    public static final ResourceKey<DimensionType> DARK_WORLD_TYPE = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(DecertiumMod.MOD_ID, "dark_world"));
}

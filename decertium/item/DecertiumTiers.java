package com.coenmooney.decertium.item;

import com.coenmooney.decertium.init.DecertiumItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class DecertiumTiers {
    public static final Tier DECERTIUM = new ForgeTier(
            4, // harvest level
            3072, // uses
            12.0F, // speed
            4.0F, // attack damage bonus
            22, // enchantment value
            BlockTags.NEEDS_DIAMOND_TOOL,
            () -> Ingredient.of(DecertiumItems.DECERTIUM_INGOT.get()));

    private DecertiumTiers() {
    }
}

package com.coenmooney.decertium.init;

import com.coenmooney.decertium.DecertiumMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class DecertiumCreativeTab {
    public static final CreativeModeTab TAB = new CreativeModeTab(DecertiumMod.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(DecertiumItems.DECERTIUM_INGOT.get());
        }
    };

    private DecertiumCreativeTab() {
    }
}

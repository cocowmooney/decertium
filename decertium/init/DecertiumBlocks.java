package com.coenmooney.decertium.init;

import com.coenmooney.decertium.DecertiumMod;
import com.coenmooney.decertium.block.DecertiumPortalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DecertiumBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DecertiumMod.MOD_ID);

    public static final RegistryObject<Block> DECERTIUM_BLOCK = registerWithItem("decertium_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(6.5F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> DECERTIUM_ORE = registerWithItem("decertium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), UniformInt.of(8, 12)));

    public static final RegistryObject<Block> DEEPSLATE_DECERTIUM_ORE = registerWithItem("deepslate_decertium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE), UniformInt.of(8, 12)));

    public static final RegistryObject<Block> NETHER_DECERTIUM_ORE = registerWithItem("nether_decertium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(5.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHER_ORE), UniformInt.of(8, 12)));

    public static final RegistryObject<Block> END_DECERTIUM_ORE = registerWithItem("end_decertium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(5.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), UniformInt.of(8, 12)));

    public static final RegistryObject<Block> DARK_SAND = registerWithItem("dark_sand",
            () -> new SandBlock(0x2D1C38, BlockBehaviour.Properties.of(Material.SAND)
                    .strength(0.6F)
                    .sound(SoundType.SAND)));

    public static final RegistryObject<Block> DARK_WORLD_PORTAL = BLOCKS.register("dark_world_portal",
            () -> new DecertiumPortalBlock(BlockBehaviour.Properties.of(Material.PORTAL)
                    .noCollission()
                    .strength(-1.0F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 10)));

    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> block) {
        RegistryObject<T> registered = BLOCKS.register(name, block);
        DecertiumItems.registerBlockItem(name, registered);
        return registered;
    }

    @OnlyIn(Dist.CLIENT)
    public static void setRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(DARK_WORLD_PORTAL.get(), RenderType.translucent());
    }
}

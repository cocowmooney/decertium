package com.coenmooney.decertium.init;

import com.coenmooney.decertium.DecertiumMod;
import com.coenmooney.decertium.item.DecertiumArmorMaterial;
import com.coenmooney.decertium.item.DecertiumTiers;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DecertiumItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DecertiumMod.MOD_ID);

    public static final RegistryObject<Item> RAW_DECERTIUM = ITEMS.register("raw_decertium", () -> new Item(defaultProps()));
    public static final RegistryObject<Item> DECERTIUM_INGOT = ITEMS.register("decertium_ingot", () -> new Item(defaultProps()));
    public static final RegistryObject<Item> DECERTIUM_SHARD = ITEMS.register("decertium_shard", () -> new Item(defaultProps()));
    public static final RegistryObject<Item> DARK_WORLD_KEY = ITEMS.register("dark_world_key", () -> new Item(defaultProps().stacksTo(1).durability(128)));
    public static final RegistryObject<Item> DECERTUS_HEART = ITEMS.register("decertus_heart", () -> new Item(defaultProps().stacksTo(1)));

    public static final RegistryObject<SwordItem> DECERTIUM_SWORD = ITEMS.register("decertium_sword",
            () -> new SwordItem(DecertiumTiers.DECERTIUM, 6, -2.2F, defaultProps()));
    public static final RegistryObject<PickaxeItem> DECERTIUM_PICKAXE = ITEMS.register("decertium_pickaxe",
            () -> new PickaxeItem(DecertiumTiers.DECERTIUM, 3, -2.6F, defaultProps()));
    public static final RegistryObject<ShovelItem> DECERTIUM_SHOVEL = ITEMS.register("decertium_shovel",
            () -> new ShovelItem(DecertiumTiers.DECERTIUM, 2.5F, -3.0F, defaultProps()));
    public static final RegistryObject<AxeItem> DECERTIUM_AXE = ITEMS.register("decertium_axe",
            () -> new AxeItem(DecertiumTiers.DECERTIUM, 6.5F, -3.0F, defaultProps()));
    public static final RegistryObject<HoeItem> DECERTIUM_HOE = ITEMS.register("decertium_hoe",
            () -> new HoeItem(DecertiumTiers.DECERTIUM, -1, 0.0F, defaultProps()));

    public static final RegistryObject<ArmorItem> DECERTIUM_HELMET = ITEMS.register("decertium_helmet",
            () -> new ArmorItem(DecertiumArmorMaterial.INSTANCE, EquipmentSlot.HEAD, defaultProps()));
    public static final RegistryObject<ArmorItem> DECERTIUM_CHESTPLATE = ITEMS.register("decertium_chestplate",
            () -> new ArmorItem(DecertiumArmorMaterial.INSTANCE, EquipmentSlot.CHEST, defaultProps()));
    public static final RegistryObject<ArmorItem> DECERTIUM_LEGGINGS = ITEMS.register("decertium_leggings",
            () -> new ArmorItem(DecertiumArmorMaterial.INSTANCE, EquipmentSlot.LEGS, defaultProps()));
    public static final RegistryObject<ArmorItem> DECERTIUM_BOOTS = ITEMS.register("decertium_boots",
            () -> new ArmorItem(DecertiumArmorMaterial.INSTANCE, EquipmentSlot.FEET, defaultProps()));

    public static void registerBlockItem(String name, Supplier<? extends net.minecraft.world.level.block.Block> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), defaultProps()));
    }

    private static Item.Properties defaultProps() {
        return new Item.Properties().tab(DecertiumCreativeTab.TAB);
    }
}

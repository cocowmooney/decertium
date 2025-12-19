package com.coenmooney.decertium.item;

import com.coenmooney.decertium.DecertiumMod;
import com.coenmooney.decertium.init.DecertiumItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class DecertiumArmorMaterial implements ArmorMaterial {
    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final int[] DEFENSE_PER_SLOT = new int[]{4, 9, 11, 4};
    public static final DecertiumArmorMaterial INSTANCE = new DecertiumArmorMaterial();

    private DecertiumArmorMaterial() {
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return HEALTH_PER_SLOT[slotIndex(slot)] * 45;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return DEFENSE_PER_SLOT[slotIndex(slot)];
    }

    @Override
    public int getEnchantmentValue() {
        return 22;
    }

    @Override
    public net.minecraft.sounds.SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(DecertiumItems.DECERTIUM_INGOT.get());
    }

    @Override
    public String getName() {
        return DecertiumMod.MOD_ID + ":decertium";
    }

    @Override
    public float getToughness() {
        return 4.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.2F;
    }

    private int slotIndex(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            default -> throw new IllegalArgumentException("Unsupported slot: " + slot);
        };
    }
}

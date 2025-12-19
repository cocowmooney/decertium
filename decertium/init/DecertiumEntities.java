package com.coenmooney.decertium.init;

import com.coenmooney.decertium.DecertiumMod;
import com.coenmooney.decertium.entity.Decertus;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DecertiumEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DecertiumMod.MOD_ID);

    public static final RegistryObject<EntityType<Decertus>> DECERTUS = ENTITY_TYPES.register("decertus", () ->
            EntityType.Builder.<Decertus>of(Decertus::new, MobCategory.MONSTER)
                    .sized(0.72F, 2.4F)
                    .fireImmune()
                    .build(DecertiumMod.MOD_ID + ":decertus"));

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DECERTUS.get(), net.minecraft.client.renderer.entity.SkeletonRenderer::new);
    }

    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DECERTUS.get(), Decertus.createAttributes().build());
    }

    public static void setupSpawns(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> SpawnPlacements.register(
                DECERTUS.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules));
    }
}

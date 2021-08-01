package toadsworthlp.funkykart;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import toadsworthlp.funkykart.entity.KartEntity;

import static net.minecraft.util.registry.Registry.ENTITY_TYPE;
import static net.minecraft.util.registry.Registry.register;

public class FunkyKart implements ModInitializer {
    public static final String MODID = "funkykart";

    public static EntityType<KartEntity> KART;

    @Override
    public void onInitialize() {
        initializeEntities();
    }

    private void initializeEntities() {
        KART = register(
                ENTITY_TYPE,
                new Identifier(MODID, "kart"),
                FabricEntityTypeBuilder.create(
                SpawnGroup.CREATURE, (EntityType.EntityFactory<KartEntity>) KartEntity::new)
                        .dimensions(EntityDimensions.fixed(1.375F, 0.5625F))
                        .trackRangeChunks(10)
                        .build()
        );
    }
}

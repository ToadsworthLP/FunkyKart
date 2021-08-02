package toadsworthlp.funkykart;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import toadsworthlp.funkykart.entity.KartEntity;
import toadsworthlp.funkykart.network.VehicleUpdater;

public class FunkyKart implements ModInitializer {
    public static final String MODID = "funkykart";
    public static final Identifier VEHICLE_UPDATE_CHANNEL = new Identifier(FunkyKart.MODID, "vehicle_update");

    public static EntityType<KartEntity> KART_ENTITY;

    @Override
    public void onInitialize() {
        initializeEntities();

        ServerPlayNetworking.registerGlobalReceiver(FunkyKart.VEHICLE_UPDATE_CHANNEL, VehicleUpdater::receiveUpdate);
    }

    private void initializeEntities() {
        KART_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MODID, "kart"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, KartEntity::new)
                        .dimensions(KartEntity.DIMENSIONS)
                        .build()
        );

        FabricDefaultAttributeRegistry.register(KART_ENTITY, KartEntity.createLivingAttributes());
    }
}

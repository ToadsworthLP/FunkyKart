package toadsworthlp.funkykart;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import toadsworthlp.funkykart.entity.KartEntity;
import toadsworthlp.funkykart.item.VehicleSpawnerItem;
import toadsworthlp.funkykart.network.VehicleUpdater;

public class FunkyKart implements ModInitializer {
    public static final String MODID = "funkykart";
    public static final Identifier VEHICLE_UPDATE_CHANNEL = new Identifier(FunkyKart.MODID, "vehicle_update");

    public static EntityType<KartEntity> KART_ENTITY;

    public static Item KART_SPAWNER_ITEM;

    @Override
    public void onInitialize() {
        initializeEntities();
        initializeItems();

        ServerPlayNetworking.registerGlobalReceiver(FunkyKart.VEHICLE_UPDATE_CHANNEL, VehicleUpdater::receiveUpdate);
    }

    private void initializeItems() {
        KART_SPAWNER_ITEM = new VehicleSpawnerItem<>(new Item.Settings().group(ItemGroup.MISC), KART_ENTITY);
        Registry.register(Registry.ITEM, new Identifier(MODID, "kart_spawner_item"), KART_SPAWNER_ITEM);
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

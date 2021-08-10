package toadsworthlp.funkykart.client.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.network.EffectEventType;

public class EffectEventReceiver {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int targetId = packetByteBuf.readInt();
        EffectEventType type = packetByteBuf.readEnumConstant(EffectEventType.class);

        client.execute(() -> {
            Entity target = client.world.getEntityById(targetId);

            switch (type) {
                case TRICK -> {
                    if(target instanceof AbstractVehicleEntity vehicle) {
                        vehicle.spawnTrickEffect();
                    }
                }
            }
        });
    }
}

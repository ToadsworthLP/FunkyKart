package toadsworthlp.funkykart.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

public class VehicleUpdater {
    public static void receiveUpdate(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int targetId = buf.readInt();

        Vec3d velocity = new Vec3d(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );

        server.execute(() -> {
            Entity vehicle = player.getVehicle();
            if(vehicle instanceof AbstractVehicleEntity && vehicle.getId() == targetId) {
                vehicle.setVelocity(velocity);
            }
        });
    }
}

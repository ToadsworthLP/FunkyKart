package toadsworthlp.funkykart.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.InputAxis;

public class VehicleUpdateReceiver {
    public static void receiveUpdate(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int targetId = buf.readInt();

        Entity target = player.getServerWorld().getEntityById(targetId);
        if(target instanceof AbstractVehicleEntity vehicle) {
            for (InputAxis input : InputAxis.values()) {
                vehicle.inputs.get(input).readFromBuffer(buf);
            }
        }
    }
}

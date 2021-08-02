package toadsworthlp.funkykart.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

public class InputNetworking {
    public static final Identifier INPUT_CHANNEL = new Identifier(FunkyKart.MODID, "input");

    public InputNetworking() {
        ServerPlayNetworking.registerGlobalReceiver(INPUT_CHANNEL, this::receiveInput);
    }

    private void receiveInput(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        System.out.println("Received input update from player " + player.getEntityName());
        Entity vehicle = player.getVehicle();
        if(vehicle instanceof AbstractVehicleEntity) {
            ((AbstractVehicleEntity) vehicle).gas = buf.readBoolean();
        }
    }
}

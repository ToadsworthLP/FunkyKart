package toadsworthlp.funkykart.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import toadsworthlp.funkykart.FunkyKart;

import java.util.Collection;

public class EffectEventSender {
    public static void send(EffectEventType type, Entity target) {
        if(target.world.isClient()) return; // Don't proceed on the client

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(target.getId());
        buf.writeEnumConstant(type);

        Collection<ServerPlayerEntity> receivers = PlayerLookup.around((ServerWorld) target.getEntityWorld(), target.getPos(), 10.0);
        for (ServerPlayerEntity player : receivers) {
            ServerPlayNetworking.send(player, FunkyKart.EFFECTS_EVENT_CHANNEL, buf);
        }
    }
}

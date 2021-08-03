package toadsworthlp.funkykart.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class VehicleController {
    public VehicleController() {
        ClientTickEvents.END_CLIENT_TICK.register(this::clientTick);
    }

    private void clientTick(MinecraftClient client) {
        if(client.world != null) { // If the player is in a world...
            Entity vehicle = client.player.getVehicle();
            if(vehicle instanceof AbstractVehicleEntity) { // ...and is currently riding a FunkyKart vehicle...
                updateVehicle((AbstractVehicleEntity) vehicle, client);
            }
        }
    }

    private void updateVehicle(AbstractVehicleEntity vehicle, MinecraftClient client) {
        Vec3d steerDirection = client.cameraEntity.getRotationVector();
        double speed = FunkyKartClient.GAS_KEY.isPressed() ? vehicle.getVehicleSpeed() : 0;
        System.out.println(speed);

        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d planarSteerDirection = steerDirection.subtract(up.multiply(up.dotProduct(steerDirection))).normalize();
        Vec3d velocity = planarSteerDirection.multiply(speed);

        vehicle.setVelocity(velocity);
        sendUpdatePacket(vehicle.getId(), velocity);
    }

    private void sendUpdatePacket(int id, Vec3d velocity) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(id);
        buf.writeDouble(velocity.x);
        buf.writeDouble(velocity.y);
        buf.writeDouble(velocity.z);

        ClientPlayNetworking.send(FunkyKart.VEHICLE_UPDATE_CHANNEL, buf);
    }
}

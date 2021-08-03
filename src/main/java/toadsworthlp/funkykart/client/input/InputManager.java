package toadsworthlp.funkykart.client.input;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.InputAxis;

import java.util.HashMap;
import java.util.Map;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class InputManager {
    private final Map<InputAxis, IClientInputAxis> inputs = new HashMap<>();

    public InputManager() {
        inputs.put(InputAxis.STEER, new LookClientInputAxis(Vec3d.ZERO)); // Needs to be set when joining world
        ClientTickEvents.END_CLIENT_TICK.register(this::clientTick);
    }

    public void registerInput(InputAxis type, KeyBinding keyBinding) {
        inputs.put(type, new ButtonClientInputAxis(false, keyBinding));
    }

    private void clientTick(MinecraftClient client) {
        if(client.world == null) return;

        if(!(client.player.getVehicle() instanceof AbstractVehicleEntity)) return;

        for (IClientInputAxis entry : inputs.values()) {
            entry.updateInput();
        }

        sendUpdatePacket(client.player.getVehicle());
    }

    private void sendUpdatePacket(Entity target) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(target.getId());

        for (InputAxis input : InputAxis.values()) {
            if(inputs.containsKey(input)) {
                inputs.get(input).writeToBuffer(buf);
            } else {
                throw new IllegalStateException("Input axis " + input.name() + " not registered in FunkyKart client InputManager!");
            }
        }

        ClientPlayNetworking.send(FunkyKart.VEHICLE_UPDATE_CHANNEL, buf);

        // Give the client the same data as the server
        AbstractVehicleEntity vehicle = (AbstractVehicleEntity) target;
        for (InputAxis input : InputAxis.values()) {
            inputs.get(input).copyTo(vehicle.inputs.get(input));
        }
    }
}

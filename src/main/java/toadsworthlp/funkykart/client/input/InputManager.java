package toadsworthlp.funkykart.client.input;

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
import toadsworthlp.funkykart.input.Vec3dInputAxis;

import java.util.HashMap;
import java.util.Map;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class InputManager {
    private final Map<InputAxis, IClientInputAxis> inputs = new HashMap<>();
    private boolean ridingLastFrame = false;

    public InputManager() {
        inputs.put(InputAxis.STEER, new SteerClientInputAxis(Vec3d.ZERO)); // Needs to be set when joining world
        ClientTickEvents.END_CLIENT_TICK.register(this::clientTick);
    }

    public void registerInput(InputAxis type, KeyBinding keyBinding) {
        inputs.put(type, new ButtonClientInputAxis(false, keyBinding));
    }

    public Vec3d getSteeringInput() {
        return ((Vec3dInputAxis)inputs.get(InputAxis.STEER)).getCurrentState();
    }

    private void clientTick(MinecraftClient client) {
        if(client.world == null) return;

        if(!(client.player.getVehicle() instanceof AbstractVehicleEntity)) {
            if(ridingLastFrame) { // Just dismounted, lock the mouse cursor
                client.mouse.lockCursor();
                client.gameRenderer.setRenderHand(true);
            }

            ridingLastFrame = false;
            return;
        }

        AbstractVehicleEntity vehicle = (AbstractVehicleEntity) client.player.getVehicle();

        ridingLastFrame = true;

        if(client.mouse.isCursorLocked()) {
            client.mouse.unlockCursor();
            client.gameRenderer.setRenderHand(false);
        };

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

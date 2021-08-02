package toadsworthlp.funkykart.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.networking.InputType;
import toadsworthlp.funkykart.networking.InputNetworking;

import java.util.HashMap;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class InputManager {

    public final HashMap<InputType, InputState> inputs = new HashMap<>();

    public InputManager() {
        ClientTickEvents.END_CLIENT_TICK.register(this::clientTick);
    }

    public void registerInput(InputType input, KeyBinding keyBinding) {
        inputs.put(input, new InputState(keyBinding));
    }

    private void clientTick(MinecraftClient client) {
        if(client.world == null) return;
        if(!(client.player.getVehicle() instanceof AbstractVehicleEntity)) return;

        boolean dirty = false;

        for (InputState entry : inputs.values()) {
            dirty |= entry.update();
        }

        if(dirty) sendUpdatePacket();
    }

    private void sendUpdatePacket() {
        PacketByteBuf buf = PacketByteBufs.create();

        for (InputType input : InputType.values()) {
            if(inputs.containsKey(input)) {
                buf.writeBoolean(inputs.get(input).getState());
                buf.writeBoolean(inputs.get(input).getPreviousState());
            } else {
                buf.writeBoolean(false);
                buf.writeBoolean(false);
            }
        }

        ClientPlayNetworking.send(InputNetworking.INPUT_CHANNEL, buf);
        System.out.println("Sent input update package");
    }
}

package toadsworthlp.funkykart.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.render.KartEntityRenderer;
import toadsworthlp.funkykart.entity.KartEntity;

import java.io.Console;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class FunkyKartClient implements ClientModInitializer {
    public static KeyBinding GAS_KEY;

    @Override
    public void onInitializeClient() {
        initializeRenderers();
        initializeKeybinds();
    }

    private void initializeRenderers() {
        EntityRendererRegistry.INSTANCE.register(FunkyKart.KART, KartEntityRenderer::new);
    }

    private void initializeKeybinds() {
        GAS_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + FunkyKart.MODID + ".gas",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_SPACE,
                "category." + FunkyKart.MODID + ".kart"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client.player != null) {
                Entity vehicle = client.player.getVehicle();
                if(vehicle instanceof KartEntity) {
                    ((KartEntity) vehicle).setInputs(false, false, GAS_KEY.wasPressed(), false);
                }
            }
        });
    }
}

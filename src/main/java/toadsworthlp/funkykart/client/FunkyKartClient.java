package toadsworthlp.funkykart.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.model.KartEntityModel;
import toadsworthlp.funkykart.client.render.KartEntityRenderer;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class FunkyKartClient implements ClientModInitializer {
    public static EntityModelLayer KART_MODEL_LAYER;

    public static final String KEYBIND_CATEGORY = "category." + FunkyKart.MODID + ".controls";
    public static final String KEYBIND_PREFIX = "key." + FunkyKart.MODID + ".";

    public static KeyBinding GAS_KEY;
    public static KeyBinding BRAKE_KEY;

    @Override
    public void onInitializeClient() {
        initializeRendering();
        initializeKeybinds();

        ClientTickEvents.END_CLIENT_TICK.register(this::clientTick);
    }

    private void clientTick(MinecraftClient client) {
        if(client.world != null) { // Runs while the player is in a world
            Entity vehicle = client.player.getVehicle();
            if(vehicle instanceof AbstractVehicleEntity) { // Runs while the player is on a FunkyKart vehicle
                if(GAS_KEY.isPressed()) {

                } else if (BRAKE_KEY.isPressed()) {

                }
            }
        }
    }

    private void initializeKeybinds() {
        GAS_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEYBIND_PREFIX + "gas",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEYBIND_CATEGORY
        ));

        BRAKE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEYBIND_PREFIX + "brake",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                KEYBIND_CATEGORY
        ));
    }

    private void initializeRendering() {
        KART_MODEL_LAYER = new EntityModelLayer(new Identifier(FunkyKart.MODID, "kart"), "main");
        EntityRendererRegistry.INSTANCE.register(FunkyKart.KART_ENTITY, KartEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(KART_MODEL_LAYER, KartEntityModel::getTexturedModelData);
    }
}

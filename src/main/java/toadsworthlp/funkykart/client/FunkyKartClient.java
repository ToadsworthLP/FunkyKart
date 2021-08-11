package toadsworthlp.funkykart.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.compat.LambdaControlsCompat;
import toadsworthlp.funkykart.client.gui.RidingHud;
import toadsworthlp.funkykart.client.input.InputManager;
import toadsworthlp.funkykart.client.model.CameraEntityModel;
import toadsworthlp.funkykart.client.model.KartEntityModel;
import toadsworthlp.funkykart.client.network.EffectEventReceiver;
import toadsworthlp.funkykart.client.render.CameraEntityRenderer;
import toadsworthlp.funkykart.client.render.KartEntityRenderer;
import toadsworthlp.funkykart.input.InputAxis;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class FunkyKartClient implements ClientModInitializer {
    public static EntityModelLayer CAMERA_MODEL_LAYER;
    public static EntityModelLayer KART_MODEL_LAYER;

    public static RidingHud RIDING_HUD;

    public static final String KEYBIND_CATEGORY = "category." + FunkyKart.MODID + ".controls";
    public static final String KEYBIND_PREFIX = "key." + FunkyKart.MODID + ".";

    public static InputManager VEHICLE_INPUT;
    public static KeyBinding GAS_KEY;
    public static KeyBinding BRAKE_KEY;
    public static KeyBinding JUMP_KEY;

    public static double TARGET_FOV_MULTIPLIER = 1;
    public static SmoothUtil FOV_SMOOTHER = new SmoothUtil();

    @Override
    public void onInitializeClient() {
        VEHICLE_INPUT = new InputManager();
        RIDING_HUD = new RidingHud(MinecraftClient.getInstance());

        initializeRendering();
        initializeKeybinds();
        initializeCompat();

        ClientPlayNetworking.registerGlobalReceiver(FunkyKart.EFFECTS_EVENT_CHANNEL, EffectEventReceiver::receive);
    }

    private void initializeKeybinds() {
        GAS_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEYBIND_PREFIX + "gas",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEYBIND_CATEGORY
        ));
        VEHICLE_INPUT.registerInput(InputAxis.GAS, GAS_KEY);

        BRAKE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEYBIND_PREFIX + "brake",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                KEYBIND_CATEGORY
        ));
        VEHICLE_INPUT.registerInput(InputAxis.BRAKE, BRAKE_KEY);

        JUMP_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEYBIND_PREFIX + "jump",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_D,
                KEYBIND_CATEGORY
        ));
        VEHICLE_INPUT.registerInput(InputAxis.JUMP, JUMP_KEY);
    }

    private void initializeRendering() {
        KART_MODEL_LAYER = new EntityModelLayer(new Identifier(FunkyKart.MODID, "kart"), "main");
        EntityRendererRegistry.INSTANCE.register(FunkyKart.KART_ENTITY, KartEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(KART_MODEL_LAYER, KartEntityModel::getTexturedModelData);

        CAMERA_MODEL_LAYER = new EntityModelLayer(new Identifier(FunkyKart.MODID, "camera"), "main");
        EntityRendererRegistry.INSTANCE.register(FunkyKart.CAMERA_ENTITY, CameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraEntityModel::getTexturedModelData);
    }

    private void initializeCompat() {
        if(FabricLoader.getInstance().isModLoaded("lambdacontrols")) {
            new LambdaControlsCompat();
        }
    }
}

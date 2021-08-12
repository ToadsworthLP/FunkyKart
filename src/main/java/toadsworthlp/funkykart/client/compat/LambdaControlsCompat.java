package toadsworthlp.funkykart.client.compat;

import dev.lambdaurora.lambdacontrols.ControlsMode;
import dev.lambdaurora.lambdacontrols.client.ButtonState;
import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.compat.CompatHandler;
import dev.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import dev.lambdaurora.lambdacontrols.client.controller.InputManager;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.FunkyKartClient;

public class LambdaControlsCompat implements CompatHandler {
    private static ButtonBinding steerXPosBinding;
    private static ButtonBinding steerYNegBinding;
    private static ButtonBinding steerXNegBinding;
    private static ButtonBinding steerYPosBinding;
    private static LambdaControlsClient lambdaControls;

    public LambdaControlsCompat() {
        handle(LambdaControlsClient.get());
        System.out.println("Loaded LambdaControls compatibility.");
    }

    @Override
    public void handle(@NotNull LambdaControlsClient mod) {
        lambdaControls = mod;

        steerXPosBinding = new ButtonBinding.Builder(new Identifier(FunkyKart.MODID, "steer_x+"))
                .onlyInGame()
                .category(ButtonBinding.MOVEMENT_CATEGORY)
                .register();

        steerYNegBinding = new ButtonBinding.Builder(new Identifier(FunkyKart.MODID, "steer_y-"))
                .onlyInGame()
                .category(ButtonBinding.MOVEMENT_CATEGORY)
                .register();

        steerXNegBinding = new ButtonBinding.Builder(new Identifier(FunkyKart.MODID, "steer_x-"))
                .onlyInGame()
                .category(ButtonBinding.MOVEMENT_CATEGORY)
                .register();

        steerYPosBinding = new ButtonBinding.Builder(new Identifier(FunkyKart.MODID, "steer_y+"))
                .onlyInGame()
                .category(ButtonBinding.MOVEMENT_CATEGORY)
                .register();

        new ButtonBinding.Builder(new Identifier(FunkyKart.MODID, "gas"))
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_A)
                .onlyInGame()
                .category(ButtonBinding.MOVEMENT_CATEGORY)
                .linkKeybind(FunkyKartClient.GAS_KEY)
                .register();

        new ButtonBinding.Builder(new Identifier(FunkyKart.MODID, "brake"))
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_B)
                .onlyInGame()
                .category(ButtonBinding.MOVEMENT_CATEGORY)
                .linkKeybind(FunkyKartClient.BRAKE_KEY)
                .register();

        new ButtonBinding.Builder(new Identifier(FunkyKart.MODID, "jump"))
                .buttons(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)
                .onlyInGame()
                .category(ButtonBinding.MOVEMENT_CATEGORY)
                .linkKeybind(FunkyKartClient.JUMP_KEY)
                .register();
    }

    public static float getJoystickSteerX() {
        return (InputManager.getBindingValue(LambdaControlsCompat.steerXPosBinding, InputManager.getBindingState(LambdaControlsCompat.steerXPosBinding)) -
                InputManager.getBindingValue(LambdaControlsCompat.steerXNegBinding, InputManager.getBindingState(LambdaControlsCompat.steerXNegBinding)));
    }

    public static float getJoystickSteerY() {
        return (InputManager.getBindingValue(LambdaControlsCompat.steerYPosBinding, InputManager.getBindingState(LambdaControlsCompat.steerYPosBinding)) -
                InputManager.getBindingValue(LambdaControlsCompat.steerYNegBinding, InputManager.getBindingState(LambdaControlsCompat.steerYNegBinding)));
    }

    public static boolean isGamepadInput() {
        return lambdaControls.config.getControlsMode() == ControlsMode.CONTROLLER;
    }
}

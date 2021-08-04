package toadsworthlp.funkykart.client.compat;

import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.compat.CompatHandler;
import dev.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import dev.lambdaurora.lambdacontrols.client.controller.ButtonCategory;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.FunkyKartClient;

public class LambdaControlsCompat implements CompatHandler {
    public LambdaControlsCompat() {
        handle(LambdaControlsClient.get());
        System.out.println("Loaded LambdaControls compatibility.");
    }

    @Override
    public void handle(@NotNull LambdaControlsClient mod) {
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
    }
}

package toadsworthlp.funkykart.client.input;

import dev.lambdaurora.lambdacontrols.client.ButtonState;
import dev.lambdaurora.lambdacontrols.client.controller.InputManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.client.compat.LambdaControlsCompat;
import toadsworthlp.funkykart.input.Vec3dInputAxis;

public class SteerClientInputAxis extends Vec3dInputAxis implements IClientInputAxis<Vec3d, Vec3dInputAxis>{
    private static final double ROUNDING_DELTA = 0.01;
    private static final double DEADZONE = 0.05;

    public SteerClientInputAxis(Vec3d initialState) {
        super(initialState);
    }

    @Override
    public void updateInput() {
        // Gamepad support if lambdacontrols is loaded
        if(FabricLoader.getInstance().isModLoaded("lambdacontrols") && LambdaControlsCompat.isGamepadInput()) {
            setState(new Vec3d(LambdaControlsCompat.getJoystickSteerX(), LambdaControlsCompat.getJoystickSteerY(), 0));
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if(client.mouse.wasLeftButtonClicked()) {
            setState(Vec3d.ZERO);
        } else {
            double height = client.getWindow().getHeight();
            double width = client.getWindow().getWidth();

            double mouseX = (1 - (client.mouse.getX() * 2 / width)) * -1;
            double mouseY = 1 - (client.mouse.getY() * 2 / height);

            if(client.getWindow().getWidth() > height) {
                mouseX /= height / width;
            } else if(width < height) {
                mouseY /= width / height;
            }

            mouseX = MathHelper.clamp(mouseX, -1, 1);
            mouseY = MathHelper.clamp(mouseY, -1, 1);

            if(Math.abs(mouseX) < DEADZONE) mouseX = 0;
            if(Math.abs(mouseY) < DEADZONE) mouseY = 0;

            double radius = Math.sqrt(Math.pow(mouseX, 2) + Math.pow(mouseY, 2));
            double angle = Math.atan2(mouseY, mouseX);

            radius = MathHelper.clamp(radius, 0, 0.5) * 2;

            double outX = radius * Math.cos(angle);
            double outY = radius * Math.sin(angle);

            if(Math.abs(outX) < ROUNDING_DELTA) outX = 0;
            if(Math.abs(outY) < ROUNDING_DELTA) outY = 0;

            if(1 - Math.abs(outX) < ROUNDING_DELTA) outX = 1 * Math.signum(outX);
            if(1 - Math.abs(outY) < ROUNDING_DELTA) outY = 1 * Math.signum(outY);

            setState(new Vec3d(outX, outY, 0));
            client.player.sendMessage(new LiteralText("R " + radius + ", A " + angle + " out ( " + outX + " / " + outY + " )"), true);
        }
    }

    @Override
    public void writeToBuffer(PacketByteBuf buffer) {
        buffer.writeDouble(getCurrentState().x);
        buffer.writeDouble(getCurrentState().y);
        buffer.writeDouble(0);
    }

    @Override
    public void copyTo(Vec3dInputAxis target) {
        target.setState(getCurrentState());
    }
}

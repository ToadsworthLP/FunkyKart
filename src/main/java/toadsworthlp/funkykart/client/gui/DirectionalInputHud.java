package toadsworthlp.funkykart.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.FunkyKartClient;
import toadsworthlp.funkykart.client.input.InputManager;

@Environment(EnvType.CLIENT)
public class DirectionalInputHud extends DrawableHelper {
    private static final Identifier STICK_TEX = new Identifier(FunkyKart.MODID, "textures/gui/control_stick.png");

    private final InputManager inputManager;
    private MinecraftClient client;

    public DirectionalInputHud(MinecraftClient client) {
        inputManager = FunkyKartClient.VEHICLE_INPUT;
        this.client = client;
    }

    public void render(MatrixStack matrices, float delta) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, STICK_TEX);

        Vec3d steerDirection = inputManager.getSteeringInput().multiply(16);
        int steerX = (int) Math.round(steerDirection.x);
        int steerY = (int) Math.round(steerDirection.y) * -1;

        int height = client.getWindow().getScaledHeight();
        int width = client.getWindow().getScaledWidth();
        int x = width / 2 + steerX;
        int y = height / 2 + steerY;

        x -= 8;
        y -= 8;

        drawTexture(matrices, x, y, 0, 0, 15, 15, 15, 15);
    }
}

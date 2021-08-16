package toadsworthlp.funkykart.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

@Environment(EnvType.CLIENT)
public class SpeedometerHud extends DrawableHelper {
    private MinecraftClient client;

    public SpeedometerHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrices, float delta, AbstractVehicleEntity vehicle) {
        Text speedText = new LiteralText(String.valueOf(Math.round(vehicle.currentSpeed * 20 * 3.6)))
                .append(new TranslatableText("gui." + FunkyKart.MODID + ".kph_text"));

        int x = client.getWindow().getScaledWidth() / 2 - client.textRenderer.getWidth(speedText) / 2;
        int y = client.getWindow().getScaledHeight() - 20;

        drawTextWithShadow(matrices, client.textRenderer, speedText, x, y, 0xffffffff);
    }
}

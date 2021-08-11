package toadsworthlp.funkykart.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

public class RidingHud implements HudRenderCallback {
    private MinecraftClient client;
    public DirectionalInputHud inputHud;

    public RidingHud(MinecraftClient client) {
        this.client = client;
        inputHud = new DirectionalInputHud(client);
        EVENT.register(this);
    }

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        if(client.player != null && client.player.getVehicle() instanceof AbstractVehicleEntity) {
            inputHud.render(matrixStack, tickDelta);
        }
    }
}

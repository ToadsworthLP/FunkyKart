package toadsworthlp.funkykart.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.render.KartEntityRenderer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class FunkyKartClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(FunkyKart.KART, KartEntityRenderer::new);
    }
}

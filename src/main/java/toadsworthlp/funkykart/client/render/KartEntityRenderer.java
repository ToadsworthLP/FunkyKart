package toadsworthlp.funkykart.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.FunkyKartClient;
import toadsworthlp.funkykart.client.model.KartEntityModel;
import toadsworthlp.funkykart.entity.KartEntity;

public class KartEntityRenderer extends LivingEntityRenderer<KartEntity, KartEntityModel> {
    public KartEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new KartEntityModel(ctx.getPart(FunkyKartClient.KART_MODEL_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(KartEntity entity) {
        return new Identifier(FunkyKart.MODID, "textures/entity/kart/kart.png");
    }
}

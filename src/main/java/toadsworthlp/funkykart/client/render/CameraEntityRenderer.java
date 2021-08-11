package toadsworthlp.funkykart.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.FunkyKartClient;
import toadsworthlp.funkykart.client.model.CameraEntityModel;
import toadsworthlp.funkykart.entity.CameraEntity;

public class CameraEntityRenderer extends LivingEntityRenderer<CameraEntity, CameraEntityModel> {
    public CameraEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CameraEntityModel(ctx.getPart(FunkyKartClient.CAMERA_MODEL_LAYER)), 0);
    }

    @Override
    public Identifier getTexture(CameraEntity entity) {
        return new Identifier(FunkyKart.MODID, "textures/entity/camera/camera.png");
    }
}

package toadsworthlp.funkykart.client.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import toadsworthlp.funkykart.client.model.KartEntityModel;
import toadsworthlp.funkykart.entity.KartEntity;

import java.util.Map;
import java.util.stream.Stream;

public class KartEntityRenderer extends EntityRenderer<KartEntity> {
    private final Map<KartEntity.Type, Pair<Identifier, KartEntityModel>> texturesAndModels;

    public KartEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
        this.texturesAndModels = (Map)Stream.of(KartEntity.Type.values()).collect(ImmutableMap.toImmutableMap((type) -> {
            return type;
        }, (type) -> {
            return Pair.of(new Identifier("textures/entity/boat/" + type.getName() + ".png"), new KartEntityModel(context.getPart(createKart(type))));
        }));
    }

    private static EntityModelLayer createKart(KartEntity.Type type) {
        return new EntityModelLayer(new Identifier("minecraft", "boat/" + type.getName()), "main");
    }

    public void render(KartEntity kartEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0D, 0.375D, 0.0D);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - f));
        float h = (float)kartEntity.getDamageWobbleTicks() - g;
        float j = kartEntity.getDamageWobbleStrength() - g;
        if (j < 0.0F) {
            j = 0.0F;
        }

        if (h > 0.0F) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(h) * h * j / 10.0F * (float)kartEntity.getDamageWobbleSide()));
        }

        float k = kartEntity.interpolateBubbleWobble(g);
        if (!MathHelper.approximatelyEquals(k, 0.0F)) {
            matrixStack.multiply(new Quaternion(new Vec3f(1.0F, 0.0F, 1.0F), kartEntity.interpolateBubbleWobble(g), true));
        }

        Pair<Identifier, KartEntityModel> pair = (Pair)this.texturesAndModels.get(kartEntity.getBoatType());
        Identifier identifier = (Identifier)pair.getFirst();
        KartEntityModel kartEntityModel = (KartEntityModel)pair.getSecond();
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
        kartEntityModel.setAngles(kartEntity, g, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(kartEntityModel.getLayer(identifier));
        kartEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!kartEntity.isSubmergedInWater()) {
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getWaterMask());
            kartEntityModel.getWaterPatch().render(matrixStack, vertexConsumer2, i, OverlayTexture.DEFAULT_UV);
        }

        matrixStack.pop();
        super.render(kartEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(KartEntity kartEntity) {
        return (Identifier)((Pair)this.texturesAndModels.get(kartEntity.getBoatType())).getFirst();
    }
}

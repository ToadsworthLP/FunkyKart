package toadsworthlp.funkykart.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import toadsworthlp.funkykart.client.FunkyKartClient;

import static toadsworthlp.funkykart.client.FunkyKartClient.TARGET_FOV_MULTIPLIER;

@Mixin(GameRenderer.class)
public class GameRendererFovMixin {
    @ModifyVariable(at = @At(value = "LOAD", ordinal = 3), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", name = "d")
    public double applyFovMultiplier(double fov) {
        float effectScale = MinecraftClient.getInstance().options.fovEffectScale;
        return fov * FunkyKartClient.FOV_SMOOTHER.smooth(TARGET_FOV_MULTIPLIER, 0.1) * effectScale;
    }
}

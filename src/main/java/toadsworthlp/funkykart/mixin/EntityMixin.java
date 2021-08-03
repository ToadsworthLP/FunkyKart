package toadsworthlp.funkykart.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityMixin {
    @Invoker Vec3d callAdjustMovementForCollisions(Vec3d movement);
}

package toadsworthlp.funkykart.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

@SuppressWarnings("EntityConstructor")
public class KartEntity extends AbstractVehicleEntity{
    public static final EntityDimensions DIMENSIONS = EntityDimensions.fixed(0.75f, 0.75f);

    public KartEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public double getVehicleSpeedLimit() {
        return 2;
    }

    @Override
    public double getVehicleTraction() {
        return 0.2;
    }

    @Override
    public double getVehicleAcceleration() {
        return 0.1;
    }
}

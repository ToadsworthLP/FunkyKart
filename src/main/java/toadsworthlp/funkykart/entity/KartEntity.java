package toadsworthlp.funkykart.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import toadsworthlp.funkykart.FunkyKart;

@SuppressWarnings("EntityConstructor")
public class KartEntity extends AbstractVehicleEntity{
    public static final EntityDimensions DIMENSIONS = EntityDimensions.fixed(0.75f, 0.75f);

    public KartEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Text getVehicleName() {
        return new TranslatableText(FunkyKart.KART_ENTITY.getTranslationKey());
    }

    @Override
    public double getVehicleSpeed() {
        return 0.75;
    }

    @Override
    public double getVehicleTraction() {
        return 0.1;
    }

    @Override
    public double getVehicleAcceleration() {
        return currentSpeed < 0.5 * getVehicleSpeed() ? 0.02 : 0.022 * (1-(currentSpeed/getVehicleSpeed()));
    }

    @Override
    public double getVehicleDeceleration() {
        return 0.03;
    }

    @Override
    public double getVehicleBrakeForce() {
        return 0.10;
    }

    @Override
    public double getVehicleOffroadMultiplier() {
        return 0.2;
    }

    @Override
    public double getVehicleSteeringSpeed() {
        return getVehicleSpeed() * 0.75;
    }

    @Override
    public double getVehicleSteeringDeceleration() {
        return 0.02;
    }
}

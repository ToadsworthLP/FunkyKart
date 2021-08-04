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
        return 0.05;
    }

    @Override
    public double getVehicleAcceleration() {
        return 0.04;
    }

    @Override
    public double getVehicleDeceleration() {
        return 0.05;
    }

    @Override
    public double getVehicleBrakeForce() {
        return 0.125;
    }

    @Override
    public double getVehicleOffroadMultiplier() {
        return 0.4;
    }
}

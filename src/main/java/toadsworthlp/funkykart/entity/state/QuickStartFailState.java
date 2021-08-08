package toadsworthlp.funkykart.entity.state;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.explosion.Explosion;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.util.IState;

public class QuickStartFailState extends DriveState {
    public static final int DELAY = 30;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = 0;
        spawnExplosionEffect(target);
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        target.setYaw(target.getYaw() + MathHelper.lerp((((float)DELAY) - target.stateMachine.getStateChangeTime()) / DELAY, 0, 45));

        if(target.stateMachine.getStateChangeTime() >= DELAY) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
        }
    }

    private void spawnExplosionEffect(AbstractVehicleEntity target) {
        target.world.addParticle(
                ParticleTypes.EXPLOSION,
                target.getX(),
                target.getY(),
                target.getZ(),
                1.0D,
                0.0D,
                0.0D);
    }
}

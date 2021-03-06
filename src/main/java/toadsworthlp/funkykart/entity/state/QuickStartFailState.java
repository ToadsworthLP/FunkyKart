package toadsworthlp.funkykart.entity.state;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.util.IState;

public class QuickStartFailState extends DriveState {
    public static final int DELAY = 30;

    public float initialYaw;
    public float initialHeadYaw;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = 0;
        spawnExplosionEffect(target);

        initialYaw = target.getYaw();
        initialHeadYaw = target.getHeadYaw();
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(!target.world.isClient()) {
            target.setYaw(target.getYaw() + MathHelper.lerp((((float)DELAY) - target.stateMachine.getStateChangeTime()) / DELAY, 0, 45));
        }

        if(target.stateMachine.getStateChangeTime() >= DELAY) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
        }

        airborneCheck(target);
    }

    @Override
    public void exit(AbstractVehicleEntity target, IState<AbstractVehicleEntity> next) {
        target.setYaw(initialYaw);
        target.setHeadYaw(initialHeadYaw);
    }

    private void spawnExplosionEffect(AbstractVehicleEntity target) {
        target.world.addParticle(
                ParticleTypes.EXPLOSION,
                target.getX(),
                target.getY() + 0.5,
                target.getZ(),
                0,
                0,
                0);
    }
}

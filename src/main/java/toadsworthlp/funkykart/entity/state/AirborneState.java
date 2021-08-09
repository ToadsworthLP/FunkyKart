package toadsworthlp.funkykart.entity.state;

import net.minecraft.particle.ParticleTypes;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;

public class AirborneState implements IState<AbstractVehicleEntity> {
    public static final int COYOTE_TIME = 3;

    private boolean justJumped = false;
    private boolean trick = false;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        justJumped = previous instanceof JumpState;
        trick = false;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(target.isOnGround()) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
        }

        if(target.stateMachine.getStateChangeTime() < COYOTE_TIME &&
            ((BooleanInputAxis) target.inputs.get(InputAxis.JUMP)).getCurrentState() &&
            !justJumped &&
            !trick
        ) {
            spawnTrickEffect(target);
            trick = true;
            target.boostTime += 10;
            System.out.println("Trick");
        }

        if(!target.world.isClient()) {
            target.setVelocity(target.currentDirection.multiply(target.currentSpeed));
        }
    }

    @Override
    public void exit(AbstractVehicleEntity target, IState<AbstractVehicleEntity> next) {

    }

    private void spawnTrickEffect(AbstractVehicleEntity target) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                double xOffset = Math.sin(i) * Math.cos(j);
                double yOffset = Math.sin(i) * Math.sin(j);
                double zOffset = Math.cos(i);

                target.world.addParticle(
                        ParticleTypes.CRIT,
                        target.getX() + xOffset,
                        target.getY() + 0.5 + yOffset,
                        target.getZ() + zOffset,
                        0,
                        0,
                        0);
            }
        }
    }
}

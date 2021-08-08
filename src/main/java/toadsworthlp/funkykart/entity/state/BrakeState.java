package toadsworthlp.funkykart.entity.state;

import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;

public class BrakeState extends DriveState {
    public static final double STEER_VECTOR_RESET_SPEED = 0.1;
    public static final int REVERSE_DELAY = 10;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = 0;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(!target.world.isClient()) {
            if(!((BooleanInputAxis) target.inputs.get(InputAxis.BRAKE)).getCurrentState()) {
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
                return;
            } else if(target.stateMachine.getStateChangeTime() > REVERSE_DELAY && target.currentSpeed < STEER_VECTOR_RESET_SPEED) {
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.REVERSE));
                return;
            }

            if(((BooleanInputAxis) target.inputs.get(InputAxis.GAS)).getCurrentState() && target.currentSpeed < STEER_VECTOR_RESET_SPEED) {
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.QUICK_START_CHARGE));
                return;
            }

            if(target.currentSpeed > target.targetSpeed) target.currentSpeed -= target.getVehicleBrakeForce() * target.getTractionMultiplier();
            if(target.currentSpeed < target.targetSpeed) target.currentSpeed = target.targetSpeed;

            if(target.currentSpeed < STEER_VECTOR_RESET_SPEED) target.currentDirection = Vec3d.ZERO;
        }

        target.spawnExhaustParticles(target, 5);
        super.tick(target);
    }
}

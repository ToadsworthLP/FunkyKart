package toadsworthlp.funkykart.entity.state;

import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;

public class BrakeState extends DriveState {
    public static final double STATIONARY_MAX_DELTA = 0.05;
    public static final int REVERSE_DELAY = 10;

    public int ticksWhenZeroSpeedReached = -1;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = 0;
        ticksWhenZeroSpeedReached = -1;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(target.currentSpeed < STATIONARY_MAX_DELTA && ticksWhenZeroSpeedReached == -1) {
            ticksWhenZeroSpeedReached = target.stateMachine.getStateChangeTime();
        }

        if(!((BooleanInputAxis) target.inputs.get(InputAxis.BRAKE)).getCurrentState()) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
            return;
        } else if(target.currentSpeed < STATIONARY_MAX_DELTA && target.stateMachine.getStateChangeTime() - ticksWhenZeroSpeedReached > REVERSE_DELAY
        ) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.REVERSE));
            return;
        }

        if(((BooleanInputAxis) target.inputs.get(InputAxis.GAS)).getCurrentState() && target.currentSpeed < STATIONARY_MAX_DELTA) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.QUICK_START_CHARGE));
            return;
        }

        if(((BooleanInputAxis) target.inputs.get(InputAxis.JUMP)).getCurrentState()) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.JUMP));
            return;
        }

        if(target.currentSpeed > target.targetSpeed) target.currentSpeed -= target.getVehicleBrakeForce() * target.getTractionMultiplier();
        if(target.currentSpeed < target.targetSpeed) target.currentSpeed = target.targetSpeed;

        if(target.currentSpeed < STATIONARY_MAX_DELTA) target.currentDirection = Vec3d.ZERO;

        target.spawnExhaustParticles(5);
        airborneCheck(target);
        super.tick(target);
    }
}

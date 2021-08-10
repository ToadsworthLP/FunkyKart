package toadsworthlp.funkykart.entity.state;

import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;

public class AirborneState implements IState<AbstractVehicleEntity> {
    public static final int COYOTE_TIME = 3;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        // Only process state change to trick state on server to prevent the client from showing a wrong trick effect
        if(!target.world.isClient()) {
            if(target.stateMachine.getStateChangeTime() < COYOTE_TIME &&
                    ((BooleanInputAxis) target.inputs.get(InputAxis.JUMP)).getCurrentState()) {
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.TRICK));
            }
        }

        if(target.isOnGround() && target.stateMachine.getStateChangeTime() >= COYOTE_TIME) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
            return;
        }

        if(!target.world.isClient()) {
            target.setVelocity(target.currentDirection.multiply(target.currentSpeed));
        }
    }

    @Override
    public void exit(AbstractVehicleEntity target, IState<AbstractVehicleEntity> next) {

    }
}

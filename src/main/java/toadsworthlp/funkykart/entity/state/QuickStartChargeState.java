package toadsworthlp.funkykart.entity.state;

import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;

public class QuickStartChargeState extends DriveState {
    public static final int SUCCESS_DELAY = 35;
    public static final int FAIL_DELAY = 40;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        super.enter(target, previous);
        target.targetSpeed = 0;
        target.currentSpeed = 0;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(!(((BooleanInputAxis) target.inputs.get(InputAxis.BRAKE)).getCurrentState()
        && ((BooleanInputAxis) target.inputs.get(InputAxis.GAS)).getCurrentState())) {
            // TODO set to correct states and apply boost
            if(target.stateMachine.getStateChangeTime() > SUCCESS_DELAY) {
                if(target.stateMachine.getStateChangeTime() < FAIL_DELAY) {
                    System.out.println("SUCESS");
                    target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
                } else {
                    System.out.println("TOO LONG");
                    target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
                }
            } else {
                System.out.println("TOO SHORT");
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
            }

            return;
        }

        super.tick(target);
    }
}

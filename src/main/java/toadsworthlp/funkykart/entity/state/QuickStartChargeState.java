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
        if(target.stateMachine.getStateChangeTime() > FAIL_DELAY) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.QUICK_START_FAIL));
        }

        if(!(((BooleanInputAxis) target.inputs.get(InputAxis.BRAKE)).getCurrentState()
                && ((BooleanInputAxis) target.inputs.get(InputAxis.GAS)).getCurrentState())) {
            if(target.stateMachine.getStateChangeTime() > SUCCESS_DELAY) {
                target.boostTime += 20;
                target.currentSpeed = target.targetSpeed * target.getTargetSpeedMultiplier();
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
            } else {
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
            }

            return;
        }

        target.spawnExhaustParticles(target, 1);
        super.tick(target);
    }
}

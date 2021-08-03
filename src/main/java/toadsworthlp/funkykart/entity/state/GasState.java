package toadsworthlp.funkykart.entity.state;

import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;

public class GasState extends DriveState {
    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = target.getVehicleSpeed();
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(((BooleanInputAxis) target.inputs.get(InputAxis.BRAKE)).getCurrentState()) {
            target.stateMachine.setState(target.brakeState);
            return;
        }

        if(!((BooleanInputAxis) target.inputs.get(InputAxis.GAS)).getCurrentState()) {
            target.stateMachine.setState(target.standState);
            return;
        }

        if(target.currentSpeed < target.targetSpeed) target.currentSpeed += target.getVehicleAcceleration();
        if(target.currentSpeed > target.targetSpeed) target.currentSpeed = target.targetSpeed;

        super.tick(target);
    }
}

package toadsworthlp.funkykart.entity.state;

import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;

public class ReverseState extends DriveState {
    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = target.getVehicleSpeed() * -0.3;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(!((BooleanInputAxis) target.inputs.get(InputAxis.BRAKE)).getCurrentState()) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
            return;
        }

        if(target.boostTime > 0) target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));

        if(target.currentSpeed > target.targetSpeed * target.getTargetSpeedMultiplier()) target.currentSpeed -= target.getVehicleAcceleration();
        if(target.currentSpeed < target.targetSpeed * target.getTargetSpeedMultiplier()) target.currentSpeed = target.targetSpeed * target.getTargetSpeedMultiplier();

        target.spawnExhaustParticles(target, 2);
        super.tick(target);
    }
}

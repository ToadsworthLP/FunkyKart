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
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.BRAKE));
            return;
        }

        if(!((BooleanInputAxis) target.inputs.get(InputAxis.GAS)).getCurrentState()) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
            return;
        }

        if(((BooleanInputAxis) target.inputs.get(InputAxis.JUMP)).getCurrentState() && target.stateMachine.getStateChangeTime() > JumpState.JUMP_COOLDOWN) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.JUMP));
            return;
        }

        if(target.currentSpeed < target.targetSpeed * target.getTargetSpeedMultiplier()) target.currentSpeed += target.getVehicleAcceleration();
        if(target.currentSpeed > target.targetSpeed * target.getTargetSpeedMultiplier()) target.currentSpeed = target.targetSpeed * target.getTargetSpeedMultiplier();

        target.spawnExhaustParticles(1);
        airborneCheck(target);
        super.tick(target);
    }
}

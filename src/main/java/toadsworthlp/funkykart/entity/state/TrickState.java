package toadsworthlp.funkykart.entity.state;

import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.network.EffectEventSender;
import toadsworthlp.funkykart.network.EffectEventType;
import toadsworthlp.funkykart.util.IState;

public class TrickState implements IState<AbstractVehicleEntity> {
    public static double TRICK_MIN_SPEED = 0.2;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.boostTime += 10;

        if(target.hasPassengers()) {
            EffectEventSender.send(EffectEventType.TRICK, target);
        }
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(target.isOnGround()) {
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

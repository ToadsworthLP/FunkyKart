package toadsworthlp.funkykart.entity.state;

import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;
import toadsworthlp.funkykart.util.Vec3dUtil;

public class DriveState implements IState<AbstractVehicleEntity> {
    protected boolean isSteering = false;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = 0;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        Vec3d steerDirection = (Vec3d)target.inputs.get(InputAxis.STEER).getCurrentState();
        double steerX = steerDirection.x * (target.currentSpeed == 0 ? 0 : 1);
        isSteering = Math.abs(steerX) > 0.01;

        float yaw = (float) (target.getYaw() + steerX * 5);

        yaw = yaw % 360;

        target.setYaw(yaw);
        target.setBodyYaw(yaw);
        target.setHeadYaw(yaw);

        target.targetDirection = Vec3dUtil.projectOnPlane(target.getRotationVector(), new Vec3d(0, 1, 0));

        double currentTargetDirDifferenceLength = target.currentDirection.subtract(target.targetDirection).length();
        if(currentTargetDirDifferenceLength > 0.05) {
            target.currentDirection = target.currentDirection.lerp(target.targetDirection, (target.getVehicleTraction() * target.getTractionMultiplier()) / currentTargetDirDifferenceLength);
        } else {
            target.currentDirection = target.targetDirection;
        }

        if(!target.world.isClient()) target.setVelocity(target.currentDirection.multiply(target.currentSpeed));
    }

    @Override
    public void exit(AbstractVehicleEntity target, IState<AbstractVehicleEntity> next) {

    }

    protected void airborneCheck(AbstractVehicleEntity target) {
        if(!target.isOnGround()) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.AIRBORNE));
        }
    }
}

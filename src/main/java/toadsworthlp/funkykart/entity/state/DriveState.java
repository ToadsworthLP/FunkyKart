package toadsworthlp.funkykart.entity.state;

import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.util.IState;
import toadsworthlp.funkykart.util.Vec3dUtil;

public class DriveState implements IState<AbstractVehicleEntity> {
    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        target.targetSpeed = 0;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        Vec3d steerDirection = (Vec3d)target.inputs.get(InputAxis.STEER).getCurrentState();
        target.targetDirection = Vec3dUtil.projectOnPlane(steerDirection, target.up).normalize();

        double currentTargetDirDifferenceLength = target.currentDirection.subtract(target.targetDirection).length();
        if(currentTargetDirDifferenceLength > 0.05) {
            target.currentDirection = target.currentDirection.lerp(target.targetDirection, (target.getVehicleTraction() * target.getTractionMultiplier()) / currentTargetDirDifferenceLength);
        } else {
            target.currentDirection = target.targetDirection;
        }

        if(!target.world.isClient()) target.setVelocity(target.currentDirection.multiply(target.currentSpeed));

        if(target.currentDirection != Vec3d.ZERO && !target.world.isClient()) {
            Vec3d entityForward = new Vec3d(0, 0, 1);
            Vec3d entityLeft = new Vec3d(1, 0, 0);
            int sign = Vec3dUtil.angleBetween(target.currentDirection, entityLeft) > (Math.PI/2) ? 1 : -1; // TODO optimize this

            float yaw = (float)(Vec3dUtil.angleBetween(target.currentDirection, entityForward) / Math.PI * 180.0) * sign;
            target.setYaw(yaw);
            target.setBodyYaw(yaw);
            target.setHeadYaw(yaw);
        }
    }

    @Override
    public void exit(AbstractVehicleEntity target, IState<AbstractVehicleEntity> next) {

    }
}

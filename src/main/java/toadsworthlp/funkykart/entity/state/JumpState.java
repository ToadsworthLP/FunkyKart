package toadsworthlp.funkykart.entity.state;

import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;
import toadsworthlp.funkykart.util.IState;
import toadsworthlp.funkykart.util.Vec3dUtil;

public class JumpState extends DriveState {
    public static final int JUMP_COOLDOWN = 5;
    public static final double JUMP_FORCE = 1.5;
    public static final int MIN_JUMP_DURATION = 5;
    public static final int MAX_JUMP_DURATION = 10;

    private Vec3d startDirection;

    @Override
    public void enter(AbstractVehicleEntity target, IState<AbstractVehicleEntity> previous) {
        // Keep previous horizontal target speed
        target.verticalSpeed += JUMP_FORCE;
        startDirection = target.currentDirection;
    }

    @Override
    public void tick(AbstractVehicleEntity target) {
        if(target.isOnGround()) {
            if(target.stateMachine.getStateChangeTime() > MIN_JUMP_DURATION) {
                target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.STAND));
                return;
            }
        } else if(target.stateMachine.getStateChangeTime() >= MAX_JUMP_DURATION) {
            target.stateMachine.setState(target.states.get(AbstractVehicleEntity.VehicleState.AIRBORNE));
            return;
        }

        target.spawnExhaustParticles(2);
        super.tick(target);
    }

    @Override
    public void exit(AbstractVehicleEntity target, IState<AbstractVehicleEntity> next) {
        if(!target.world.isClient()) {
            Vec3d right = target.currentDirection.rotateY((float) (-0.5f * Math.PI));
            double angle = Vec3dUtil.angleBetween(target.currentDirection, startDirection) / Math.PI;
            boolean left = Vec3dUtil.angleBetween(target.targetDirection, right) / Math.PI > 0.5;

            if(angle > 0.01) {
                if(left) { // TODO implement state change to drift
                    System.out.println("Drift left");
                } else {
                    System.out.println("Drift right");
                }
            }
        }

        if(next instanceof StandState) {

        }
    }
}

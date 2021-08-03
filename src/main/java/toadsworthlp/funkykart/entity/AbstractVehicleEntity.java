package toadsworthlp.funkykart.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import toadsworthlp.funkykart.entity.state.BrakeState;
import toadsworthlp.funkykart.entity.state.GasState;
import toadsworthlp.funkykart.entity.state.StandState;
import toadsworthlp.funkykart.input.BaseInputAxis;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.input.Vec3dInputAxis;
import toadsworthlp.funkykart.util.IState;
import toadsworthlp.funkykart.util.StateMachine;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("EntityConstructor")
public abstract class AbstractVehicleEntity extends LivingEntity {
    public final Map<InputAxis, BaseInputAxis> inputs = new HashMap<>();

    public StateMachine<AbstractVehicleEntity> stateMachine;
    public IState<AbstractVehicleEntity> standState = new StandState();
    public IState<AbstractVehicleEntity> gasState = new GasState();
    public IState<AbstractVehicleEntity> brakeState = new BrakeState();

    public double currentSpeed = 0;
    public double targetSpeed = 0;
    public double gravityStrength = 1.5;

    public Vec3d currentDirection = Vec3d.ZERO;
    public Vec3d targetDirection = Vec3d.ZERO;

    public final Vec3d up = new Vec3d(0, 1, 0);
    public final Vec3d gravityDir = up.multiply(-1);

    public AbstractVehicleEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

        stateMachine = new StateMachine<>(this, standState);
        inputs.put(InputAxis.STEER, new Vec3dInputAxis(Vec3d.ZERO));
        inputs.put(InputAxis.GAS, new BooleanInputAxis(false));
        inputs.put(InputAxis.BRAKE, new BooleanInputAxis(false));
    }

    public abstract double getVehicleSpeed();

    public abstract double getVehicleTraction();

    public abstract double getVehicleAcceleration();

    public abstract double getVehicleDeceleration();

    public abstract double getVehicleBrakeForce();

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.world.isClient()) {
            return player.startRiding(this) ? ActionResult.SUCCESS : ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();

        if(!hasPassengers() && !stateMachine.getState().equals(standState)) stateMachine.setState(standState);
        stateMachine.tick();

        setVelocity(getVelocity().add(gravityDir.multiply(gravityStrength)));

        //setVelocity(getVelocity().add(steerDirection.multiply(getVehicleTraction())).normalize().multiply(currentSpeed));
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return DefaultedList.of();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.LEFT;
    }
}

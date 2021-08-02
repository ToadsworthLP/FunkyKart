package toadsworthlp.funkykart.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import toadsworthlp.funkykart.input.BaseInputAxis;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.input.Vec3dInputAxis;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("EntityConstructor")
public abstract class AbstractVehicleEntity extends LivingEntity {
    public final Map<InputAxis, BaseInputAxis> inputs = new HashMap<>();

    public AbstractVehicleEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

        inputs.put(InputAxis.STEER, new Vec3dInputAxis(Vec3d.ZERO));
        inputs.put(InputAxis.GAS, new BooleanInputAxis(false));
        inputs.put(InputAxis.BRAKE, new BooleanInputAxis(false));
    }

    public void processUpdate() {
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d steerDirection = (Vec3d)inputs.get(InputAxis.STEER).getCurrentState();
        Vec3d planarSteerDirection = steerDirection.subtract(up.multiply(up.dotProduct(steerDirection))).normalize();
        Vec3d velocity = planarSteerDirection.multiply(getVehicleSpeedLimit());

        setVelocity(velocity);
    }

    public abstract double getVehicleSpeedLimit();

    public abstract double getVehicleTraction();

    public abstract double getVehicleAcceleration();

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

        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d steerDirection = (Vec3d)inputs.get(InputAxis.STEER).getCurrentState();
        Vec3d planarSteerDirection = steerDirection.subtract(up.multiply(up.dotProduct(steerDirection))).normalize();
        Vec3d velocity = planarSteerDirection.multiply((boolean)inputs.get(InputAxis.GAS).getCurrentState() ? getVehicleSpeedLimit() : 0).add(new Vec3d(0, -1.5, 0));

        setVelocity(velocity);

        if(hasPassengers()) {
            setYaw(getFirstPassenger().getHeadYaw());
        }

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

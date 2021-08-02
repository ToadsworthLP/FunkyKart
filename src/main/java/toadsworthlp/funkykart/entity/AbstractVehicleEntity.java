package toadsworthlp.funkykart.entity;

import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import toadsworthlp.funkykart.network.ExtraTrackedDataHandlers;

@SuppressWarnings("EntityConstructor")
public abstract class AbstractVehicleEntity extends LivingEntity {

    private static final TrackedData<Double> SPEED_LIMIT = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.DOUBLE);
    private static final String SPEED_LIMIT_KEY = "speedLimit";
    private double speedLimit;

    private static final TrackedData<Double> CURRENT_SPEED = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.DOUBLE);
    private static final String CURRENT_SPEED_KEY = "currentSpeed";
    private double currentSpeed;

    private static final TrackedData<Vec3d> STEER_DIRECTION = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.VECTOR3);
    private static final String STEER_DIRECTION_KEY = "steerDirection";
    private Vec3d steerDirection;

    private static final TrackedData<Boolean> TEST = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public AbstractVehicleEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        setInvulnerable(true);

        speedLimit = getVehicleSpeedLimit();
        steerDirection = Vec3d.ZERO;
    }

    public double getVehicleSpeedLimit() {
        return 2;
    }

    public double getVehicleTraction() {
        return 0.5;
    }

    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
        this.dataTracker.set(SPEED_LIMIT, speedLimit);
    }

    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
        this.dataTracker.set(CURRENT_SPEED, speed);
    }

    public void setSteerDirection(Vec3d direction) {
        this.steerDirection = direction;
        this.dataTracker.set(STEER_DIRECTION, direction);
    }

    public void gas(double intensity) {
        setCurrentSpeed(currentSpeed + intensity);
        System.out.println("Client gas" + currentSpeed);
    }

    public void brake(double intensity) {
        setCurrentSpeed(currentSpeed - intensity);
    }

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

        if(world.isClient) {
            this.dataTracker.set(TEST, false);
        } else {
            this.dataTracker.set(TEST, true);
        }

        speedLimit = this.dataTracker.get(SPEED_LIMIT);
        currentSpeed = this.dataTracker.get(CURRENT_SPEED);
        steerDirection = this.dataTracker.get(STEER_DIRECTION);

        if(!world.isClient()) {
            setCurrentSpeed(1);

            if(currentSpeed > speedLimit) {
                setCurrentSpeed(speedLimit);
            }

            if(currentSpeed < 0) setCurrentSpeed(0);

            //setVelocity(getVelocity().add(steerDirection.multiply(getVehicleTraction())).normalize().multiply(currentSpeed));
            setVelocity(new Vec3d(steerDirection.x, 0, steerDirection.y));
        }

        //System.out.println(Thread.currentThread().getName() + " L" + speedLimit + " S" + currentSpeed + " D" + steerDirection);
        System.out.println(this.dataTracker.get(TEST));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPEED_LIMIT, getVehicleSpeedLimit());
        this.dataTracker.startTracking(CURRENT_SPEED, 0.0);
        this.dataTracker.startTracking(STEER_DIRECTION, Vec3d.ZERO);
        this.dataTracker.startTracking(TEST, false);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);

        if (tag.contains(SPEED_LIMIT_KEY))
            this.dataTracker.set(SPEED_LIMIT, tag.getDouble(SPEED_LIMIT_KEY));

        if (tag.contains(CURRENT_SPEED_KEY))
            this.dataTracker.set(CURRENT_SPEED, tag.getDouble(CURRENT_SPEED_KEY));

        if (tag.contains(STEER_DIRECTION_KEY + "X"))
            this.dataTracker.set(STEER_DIRECTION, new Vec3d(
                    tag.getDouble(STEER_DIRECTION_KEY + "X"),
                    tag.getDouble(STEER_DIRECTION_KEY + "Y"),
                    tag.getDouble(STEER_DIRECTION_KEY + "Z")
            ));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        if(speedLimit != getVehicleSpeedLimit())
            tag.putDouble(SPEED_LIMIT_KEY, speedLimit);

        if(currentSpeed != 0)
            tag.putDouble(CURRENT_SPEED_KEY, currentSpeed);

        if(steerDirection != null && !steerDirection.equals(Vec3d.ZERO)) {
            tag.putDouble(STEER_DIRECTION_KEY + "X", steerDirection.x);
            tag.putDouble(STEER_DIRECTION_KEY + "Y", steerDirection.y);
            tag.putDouble(STEER_DIRECTION_KEY + "Z", steerDirection.z);
        }
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

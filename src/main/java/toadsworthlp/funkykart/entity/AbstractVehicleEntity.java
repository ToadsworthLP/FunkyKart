package toadsworthlp.funkykart.entity;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import toadsworthlp.funkykart.entity.state.BrakeState;
import toadsworthlp.funkykart.entity.state.GasState;
import toadsworthlp.funkykart.entity.state.StandState;
import toadsworthlp.funkykart.input.BaseInputAxis;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.input.Vec3dInputAxis;
import toadsworthlp.funkykart.mixin.EntityMixin;
import toadsworthlp.funkykart.network.ExtraTrackedDataHandlers;
import toadsworthlp.funkykart.util.IState;
import toadsworthlp.funkykart.util.StateMachine;

import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("EntityConstructor")
public abstract class AbstractVehicleEntity extends LivingEntity {
    public final Map<InputAxis, BaseInputAxis> inputs = new HashMap<>();

    public StateMachine<AbstractVehicleEntity> stateMachine;
    public IState<AbstractVehicleEntity> standState = new StandState();
    public IState<AbstractVehicleEntity> gasState = new GasState();
    public IState<AbstractVehicleEntity> brakeState = new BrakeState();

    public double currentSpeed = 0;
    public double targetSpeed = 0;
    public double gravityStrength = 1;

    public Vec3d currentDirection = Vec3d.ZERO;
    public Vec3d targetDirection = Vec3d.ZERO;

    public final Vec3d up = new Vec3d(0, 1, 0);
    public final Vec3d gravityDir = up.multiply(-1.25);

    private static final String ROAD_BLOCKS_KEY = "RoadBlocks";
    private static final TrackedData<Set<Block>> ROAD_BLOCKS = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.BLOCK_SET);
    private Set<Block> roadBlocks = new HashSet<>();

    private boolean hadPassengerLastTick = false;

    public AbstractVehicleEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

        stateMachine = new StateMachine<>(this, standState);
        inputs.put(InputAxis.STEER, new Vec3dInputAxis(Vec3d.ZERO));
        inputs.put(InputAxis.GAS, new BooleanInputAxis(false));
        inputs.put(InputAxis.BRAKE, new BooleanInputAxis(false));

        setCustomName(getVehicleName());
        stepHeight = 1;
    }

    public abstract Text getVehicleName();

    public abstract double getVehicleSpeed();

    public abstract double getVehicleTraction();

    public abstract double getVehicleAcceleration();

    public abstract double getVehicleDeceleration();

    public abstract double getVehicleBrakeForce();

    public abstract double getVehicleOffroadMultiplier();

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

        roadBlocks = this.dataTracker.get(ROAD_BLOCKS);

        if(hasPassengers()) {
            getFirstPassenger().setAir(getFirstPassenger().getMaxAir()); // For underwater driving
            hadPassengerLastTick = true;
        } else {
            if(hadPassengerLastTick) {
                onPlayerDismounted();
                hadPassengerLastTick = false;
            }
        }

        stateMachine.tick();
        checkHitWall();

        setVelocity(getVelocity().add(gravityDir.multiply(gravityStrength)));
    }

    public void onPlayerDismounted() {
        // Reset input states
        inputs.forEach((InputAxis key, BaseInputAxis axis) -> {
            axis.resetState();
        });

        stateMachine.setState(standState);
    }

    public void setRoadBlocks(Set<Block> roadBlocks) {
        this.roadBlocks = roadBlocks;
        this.dataTracker.set(ROAD_BLOCKS, roadBlocks);
    }

    public double getTargetSpeedMultiplier() {
        Block blockBelow = this.world.getBlockState(this.getBlockPos().subtract(new Vec3i(0, 1, 0))).getBlock();
        if(blockBelow.equals(Blocks.AIR) || roadBlocks.contains(blockBelow)) {
            return 1;
        } else {
            return getVehicleOffroadMultiplier();
        }
    }

    // Checks if a wall was hit and stops the player if needed
    private void checkHitWall() {
        Vec3d preAdjVelocity = getVelocity();
        Vec3d postAdjVelocity = ((EntityMixin)this).callAdjustMovementForCollisions(preAdjVelocity);

        boolean collision = !MathHelper.approximatelyEquals(preAdjVelocity.x, postAdjVelocity.x) || !MathHelper.approximatelyEquals(preAdjVelocity.z, postAdjVelocity.z);

        if(collision) {
            if(currentSpeed > 0.175) {
                currentDirection = postAdjVelocity.add(postAdjVelocity.subtract(preAdjVelocity)).normalize();
                currentSpeed /= 4;
            } else {
                currentSpeed /= 4;
            }
        }
    }

    // NBT handling
    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);

        // Read road data
        if(tag.contains(ROAD_BLOCKS_KEY)) {
            Set<Block> set = new HashSet<>();
            NbtList list = (NbtList) tag.get(ROAD_BLOCKS_KEY);

            list.iterator().forEachRemaining((NbtElement element) -> {
                NbtCompound compound = (NbtCompound) element;
                set.add(Registry.BLOCK.get(new Identifier(compound.getString("Id"))));
            });

            this.dataTracker.set(ROAD_BLOCKS, set);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        // Clean up existing road data
        if(tag.contains(ROAD_BLOCKS_KEY)) tag.remove(ROAD_BLOCKS_KEY);

        // Write new data
        NbtList list = new NbtList();
        roadBlocks.iterator().forEachRemaining((Block block) -> {
            NbtCompound compound = new NbtCompound();
            compound.putString("Id", Registry.BLOCK.getId(block).toString());
            list.add(compound);
        });

        tag.put(ROAD_BLOCKS_KEY, list);
    }

    // Data tracking
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ROAD_BLOCKS, roadBlocks);
    }

    // Make it rideable underwater
    @Override
    public boolean canBeRiddenInWater() {
        return true;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    // Completely disable fall damage
    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    // Only display the vehicle name when it isn't being ridden
    @Override
    public boolean shouldRenderName() {
        return !hasPassengers();
    }

    // Some other stuff
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

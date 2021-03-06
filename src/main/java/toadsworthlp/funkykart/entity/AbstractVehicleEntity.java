package toadsworthlp.funkykart.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.client.FunkyKartClient;
import toadsworthlp.funkykart.entity.state.*;
import toadsworthlp.funkykart.input.BaseInputAxis;
import toadsworthlp.funkykart.input.BooleanInputAxis;
import toadsworthlp.funkykart.input.InputAxis;
import toadsworthlp.funkykart.input.Vec3dInputAxis;
import toadsworthlp.funkykart.mixin.EntityMixin;
import toadsworthlp.funkykart.network.ExtraTrackedDataHandlers;
import toadsworthlp.funkykart.util.IState;
import toadsworthlp.funkykart.util.StateMachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("EntityConstructor")
public abstract class AbstractVehicleEntity extends LivingEntity {
    public final Map<InputAxis, BaseInputAxis> inputs = new HashMap<>();

    // State machine variables

    public StateMachine<AbstractVehicleEntity> stateMachine;
    public BiMap<VehicleState, IState<AbstractVehicleEntity>> states = HashBiMap.create();
    public BiMap<IState<AbstractVehicleEntity>, VehicleState> inverseStates = states.inverse();
    public enum VehicleState { STAND, GAS, BRAKE, QUICK_START_CHARGE, QUICK_START_FAIL, REVERSE, AIRBORNE, JUMP, DRIFT, TRICK }

    // Movement variables

    public double currentSpeed = 0;
    public double targetSpeed = 0;
    public double gravityStrength = 0.75;
    public Vec3d currentDirection = Vec3d.ZERO;
    public Vec3d targetDirection = Vec3d.ZERO;
    public double boostTime = 0;
    public double boostStrength = 0.5;
    public double verticalSpeed = 0;

    public final Vec3d up = new Vec3d(0, 1, 0);
    public final Vec3d gravityDir = up.multiply(-1.25);

    private Set<Block> roadBlocks = new HashSet<>();
    private boolean hadPassengerLastTick = false;

    // Data trackers

    private static final TrackedData<String> TRACKED_STATE_NAME = DataTracker.registerData(AbstractVehicleEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Double> CURRENT_SPEED = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.DOUBLE);
    private static final TrackedData<Double> TARGET_SPEED = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.DOUBLE);
    private static final TrackedData<Double> GRAVITY_STRENGTH = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.DOUBLE);
    private static final TrackedData<Vec3d> CURRENT_DIRECTION = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.VECTOR3);
    private static final TrackedData<Vec3d> TARGET_DIRECTION = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.VECTOR3);
    private static final TrackedData<Double> BOOST_TIME = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.DOUBLE);
    private static final TrackedData<Double> VERTICAL_SPEED = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.DOUBLE);
    private static final TrackedData<Set<Block>> ROAD_BLOCKS = DataTracker.registerData(AbstractVehicleEntity.class, ExtraTrackedDataHandlers.BLOCK_SET);

    private static final String ROAD_BLOCKS_KEY = "RoadBlocks";


    public AbstractVehicleEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

        states.put(VehicleState.STAND, new StandState());
        states.put(VehicleState.GAS, new GasState());
        states.put(VehicleState.BRAKE, new BrakeState());
        states.put(VehicleState.QUICK_START_CHARGE, new QuickStartChargeState());
        states.put(VehicleState.QUICK_START_FAIL, new QuickStartFailState());
        states.put(VehicleState.REVERSE, new ReverseState());
        states.put(VehicleState.AIRBORNE, new AirborneState());
        states.put(VehicleState.JUMP, new JumpState());
        states.put(VehicleState.DRIFT, new DriftState());
        states.put(VehicleState.TRICK, new TrickState());

        stateMachine = new StateMachine<>(this, states.get(VehicleState.STAND), (IState<AbstractVehicleEntity> previous, IState<AbstractVehicleEntity> next) -> {
            VehicleState stateEnum = inverseStates.get(next);
            this.dataTracker.set(TRACKED_STATE_NAME, stateEnum.name());
        });

        inputs.put(InputAxis.STEER, new Vec3dInputAxis(Vec3d.ZERO));
        inputs.put(InputAxis.GAS, new BooleanInputAxis(false));
        inputs.put(InputAxis.BRAKE, new BooleanInputAxis(false));
        inputs.put(InputAxis.JUMP, new BooleanInputAxis(false));

        setCustomName(getVehicleName());
        stepHeight = 1.0f;
    }

    public abstract Text getVehicleName();

    public abstract double getVehicleSpeed();

    public abstract double getVehicleTraction();

    public abstract double getVehicleAcceleration();

    public abstract double getVehicleDeceleration();

    public abstract double getVehicleBrakeForce();

    public abstract double getVehicleOffroadMultiplier();

    public abstract double getVehicleSteeringSpeed();

    public abstract double getVehicleSteeringDeceleration();

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

        if(world.isClient()) {
            // Client state synchronization

            currentSpeed = this.dataTracker.get(CURRENT_SPEED);
            targetSpeed = this.dataTracker.get(TARGET_SPEED);
            gravityStrength = this.dataTracker.get(GRAVITY_STRENGTH);
            currentDirection = this.dataTracker.get(CURRENT_DIRECTION);
            targetDirection = this.dataTracker.get(TARGET_DIRECTION);
            boostTime = this.dataTracker.get(BOOST_TIME);
            verticalSpeed = this.dataTracker.get(VERTICAL_SPEED);

            VehicleState localState = inverseStates.get(stateMachine.getState());
            VehicleState remoteState = VehicleState.valueOf(dataTracker.get(TRACKED_STATE_NAME));
            if(localState != remoteState) {
                stateMachine.setState(states.get(remoteState));
            }

            stateMachine.tick();
            updateFov();
        } else {
            // Possibly state-changing actions

            // Underwater driving
            if(hasPassengers()) {
                if(isSubmergedInWater()) {
                    getFirstPassenger().setAir(getFirstPassenger().getMaxAir());
                    gravityStrength = 0.15;
                } else {
                    gravityStrength = 0.75;
                }
            }

            stateMachine.tick();
            setVelocity(getVelocity().add(gravityDir.multiply(gravityStrength)));

            if(verticalSpeed > 0) {
                setVelocity(getVelocity().add(up.multiply(verticalSpeed)));
                verticalSpeed -= gravityStrength;
                if(verticalSpeed <= 0) verticalSpeed = 0;
            }

            boolean hitWall = checkHitWall();
            if(hitWall) currentSpeed /= 2;

            if(boostTime > 0 && isOnGround()) {
                setVelocity(getVelocity().add(currentDirection.multiply(boostStrength)));
                currentSpeed = targetSpeed * getTargetSpeedMultiplier();
                boostTime--;
            }
        }

        if(hasPassengers()) {
            hadPassengerLastTick = true;
        } else {
            if(hadPassengerLastTick) {
                onPlayerDismounted();
                hadPassengerLastTick = false;
            }
        }

        if(!this.world.isClient()) {
            dataTracker.set(CURRENT_SPEED, currentSpeed);
            dataTracker.set(TARGET_SPEED, targetSpeed);
            dataTracker.set(GRAVITY_STRENGTH, gravityStrength);
            dataTracker.set(CURRENT_DIRECTION, currentDirection);
            dataTracker.set(TARGET_DIRECTION, targetDirection);
            dataTracker.set(BOOST_TIME, boostTime);
            dataTracker.set(VERTICAL_SPEED, verticalSpeed);
        }
    }

    public void onPlayerDismounted() {
        // Reset input states
        inputs.forEach((InputAxis key, BaseInputAxis axis) -> {
            axis.resetState();
        });

        stateMachine.setState(states.get(VehicleState.STAND));
    }

    public void setRoadBlocks(Set<Block> roadBlocks) {
        this.roadBlocks = roadBlocks;
        this.dataTracker.set(ROAD_BLOCKS, roadBlocks);
    }

    // Environmental stat multipliers

    public double getTargetSpeedMultiplier() {
        BlockPos blockPos = new BlockPos(getPos().subtract(0, 0.5, 0));
        Block blockBelow = this.world.getBlockState(blockPos).getBlock();
        if(blockBelow.equals(Blocks.AIR) || roadBlocks.contains(blockBelow)) {
            return 1;
        } else {
            return getVehicleOffroadMultiplier();
        }
    }

    public double getTractionMultiplier() {
        Block blockBelow = this.world.getBlockState(this.getBlockPos().subtract(new Vec3i(0, 1, 0))).getBlock();
        if(blockBelow.equals(Blocks.ICE)
                || blockBelow.equals(Blocks.BLUE_ICE)
                || blockBelow.equals(Blocks.FROSTED_ICE)
                || blockBelow.equals(Blocks.PACKED_ICE)
        ) {
            return 0.6;
        } else {
            return 1;
        }
    }

    // Checks if a wall was hit

    private boolean checkHitWall() {
        Vec3d preAdjVelocity = getVelocity();
        Vec3d postAdjVelocity = ((EntityMixin)this).callAdjustMovementForCollisions(preAdjVelocity);

        return !MathHelper.approximatelyEquals(preAdjVelocity.x, postAdjVelocity.x) || !MathHelper.approximatelyEquals(preAdjVelocity.z, postAdjVelocity.z);
    }


    // Particle effects

    public void spawnExhaustParticles(int pauseTicks) {
        ParticleEffect type = boostTime > 0 ? ParticleTypes.FLAME : ParticleTypes.SMOKE;

        if(this.hasPassengers() && stateMachine.getStateChangeTime() % pauseTicks == 0) {
            Vec3d vel = getRotationVector().multiply(-0.1);
            world.addParticle(
                    type,
                    getX(),
                    getY() + 0.5,
                    getZ(),
                    vel.x,
                    vel.y,
                    vel.z);
        }
    }

    public void spawnTrickEffect() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                double xVel = Math.sin(i) * Math.cos(j);
                double yVel = Math.sin(i) * Math.sin(j);
                double zVel = Math.cos(i);

                world.addParticle(
                        ParticleTypes.CRIT,
                        getX(),
                        getY() + 0.5,
                        getZ(),
                        xVel,
                        yVel,
                        zVel);
            }
        }
    }

    // FOV change effect

    @Environment(EnvType.CLIENT)
    private void updateFov() {
        Entity player = MinecraftClient.getInstance().player;
        if(player.hasVehicle() && player.getVehicle() == this) {
            FunkyKartClient.TARGET_FOV_MULTIPLIER = 1 + (currentSpeed/4);
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
        if(roadBlocks != null) {
            NbtList list = new NbtList();
            roadBlocks.iterator().forEachRemaining((Block block) -> {
                NbtCompound compound = new NbtCompound();
                compound.putString("Id", Registry.BLOCK.getId(block).toString());
                list.add(compound);
            });

            tag.put(ROAD_BLOCKS_KEY, list);
        }
    }

    // Data tracking
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ROAD_BLOCKS, roadBlocks);
        this.dataTracker.startTracking(TRACKED_STATE_NAME, VehicleState.STAND.toString());
        this.dataTracker.startTracking(CURRENT_SPEED, currentSpeed);
        this.dataTracker.startTracking(TARGET_SPEED, targetSpeed);
        this.dataTracker.startTracking(GRAVITY_STRENGTH, gravityStrength);
        this.dataTracker.startTracking(CURRENT_DIRECTION, currentDirection);
        this.dataTracker.startTracking(TARGET_DIRECTION, targetDirection);
        this.dataTracker.startTracking(BOOST_TIME, boostTime);
        this.dataTracker.startTracking(VERTICAL_SPEED, verticalSpeed);
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

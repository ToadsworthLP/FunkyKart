package toadsworthlp.funkykart.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.lwjgl.system.MathUtil;
import toadsworthlp.funkykart.util.IState;
import toadsworthlp.funkykart.util.Vec3dUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
public class CameraEntity extends LivingEntity {
    public static final EntityDimensions DIMENSIONS = EntityDimensions.fixed(0.625f, 0.625f);

    private static final Set<AbstractVehicleEntity.VehicleState> freezeCamStates = new HashSet<>() {{
        add(AbstractVehicleEntity.VehicleState.QUICK_START_FAIL);
    }};

    private static final double CAMERA_DISTANCE = 5;
    private static final double CAMERA_HEIGHT = 2;

    private static final TrackedData<Optional<UUID>> TARGET_UUID = DataTracker.registerData(CameraEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final String TARGET_UUID_KEY = "targetUuid";

    private AbstractVehicleEntity target;

    private int despawnTimer = 0;

    public CameraEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        setInvisible(true);
        setCustomNameVisible(false);
    }

    @Environment(EnvType.CLIENT)
    private void updateClientCameraEntity() {
        boolean thisPlayerRidingTarget = target != null && target.hasPassengers() && target.getFirstPassenger().equals(MinecraftClient.getInstance().player);

        if(thisPlayerRidingTarget) {
            if(!MinecraftClient.getInstance().getCameraEntity().equals(this)) {
                MinecraftClient.getInstance().setCameraEntity(this);
            }
        } else {
            MinecraftClient.getInstance().setCameraEntity(MinecraftClient.getInstance().player);
        }
    }

    @Override
    public void tick() {
        if (target != null) {
            updateCameraMovement();
            if (!target.isAlive()) {
                removeTarget();
                return;
            }
        } else if (!this.world.isClient) {
            Optional<UUID> holderId = this.dataTracker.get(TARGET_UUID);
            if (holderId.isPresent()) {
                Entity target = ((ServerWorld) this.world).getEntity(holderId.get());
                if (target instanceof AbstractVehicleEntity vehicle)
                    setTarget(vehicle);
            }
        }

        if (this.world.isClient) {
            Optional<UUID> targetId = this.dataTracker.get(TARGET_UUID);
            if (targetId.isPresent()) {
                Entity vehicle = MinecraftClient.getInstance().player.getVehicle();
                if (vehicle != null && vehicle.getUuid().equals(targetId.get())) {
                    target = (AbstractVehicleEntity) vehicle;
                }
            }

            updateClientCameraEntity();
        }

        if(hasTarget()) {
            despawnTimer = 0;
        } else {
            despawnTimer++;
            if(despawnTimer > 0) {
                removeSelf();
            }
        }
    }

    private void updateCameraMovement() {
        if(freezeCamStates.contains(target.inverseStates.get(target.stateMachine.getState()))) return;

        Vec3d targetPosition = target.getPos()
                .add(target.targetDirection.multiply(CAMERA_DISTANCE * -1))
                .add(target.up.multiply(CAMERA_HEIGHT));

        setPosition(targetPosition);

        prevYaw = getYaw();
        prevHeadYaw = getHeadYaw();

        setRotation(target.getYaw(), 0);
        setHeadYaw(target.getHeadYaw());
    }

    public void removeSelf() {
        this.remove(RemovalReason.DISCARDED);
    }

    public boolean hasTarget() {
        return this.dataTracker.get(TARGET_UUID).isPresent();
    }

    public void setTarget(AbstractVehicleEntity target) {
        this.dataTracker.set(TARGET_UUID, Optional.of(target.getUuid()));
        this.target = target;
    }

    public void removeTarget() {
        this.dataTracker.set(TARGET_UUID, Optional.empty());
        this.target = null;
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        return; // Don't take damage
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TARGET_UUID, Optional.empty());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);

        if (tag.contains(TARGET_UUID_KEY))
            this.dataTracker.set(TARGET_UUID, Optional.of(tag.getUuid(TARGET_UUID_KEY)));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        if (target != null)
            tag.putUuid(TARGET_UUID_KEY, target.getUuid());
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

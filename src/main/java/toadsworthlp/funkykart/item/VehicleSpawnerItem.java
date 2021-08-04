package toadsworthlp.funkykart.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class VehicleSpawnerItem<T extends Entity> extends Item {

    private static final Set<Block> defaultRoad = new HashSet<>() {{
        add(Blocks.STONE);
        add(Blocks.STONE_SLAB);
        add(Blocks.STONE_STAIRS);
    }};

    private final EntityType<T> type;

    public VehicleSpawnerItem(Settings settings, EntityType<T> type) {
        super(settings);
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);

            BlockPos spawnPosition;
            if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                spawnPosition = blockPos;
            } else {
                spawnPosition = blockPos.offset(direction);
            }

            Entity newEntity = type.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), spawnPosition, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, spawnPosition) && direction == Direction.UP);
            if (newEntity instanceof AbstractVehicleEntity vehicle) {
                vehicle.setRoadBlocks(defaultRoad);
                itemStack.decrement(1);
                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
            }

            return ActionResult.SUCCESS;
        }
    }
}

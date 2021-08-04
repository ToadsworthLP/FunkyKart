package toadsworthlp.funkykart.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// TODO set road blocks and give them to vehicle entity
public class VehicleSpawnerItem<T extends Entity> extends Item {
    private final EntityType<T> type;

    public VehicleSpawnerItem(Settings settings, EntityType<T> type) {
        super(settings);
        this.type = type;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()) {
            NbtCompound nbtCompound = stack.getNbt();
            tooltip.add((new TranslatableText("Test: " + nbtCompound.getInt("test"))).formatted(Formatting.GRAY));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        NbtCompound nbt = itemStack.getOrCreateNbt();

        int testVal = 0;
        if(nbt.contains("test")) {
            testVal = nbt.getInt("test");
        }

        testVal++;
        nbt.putInt("test", testVal);

        itemStack.writeNbt(nbt);

        return TypedActionResult.success(playerEntity.getStackInHand(hand));
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
                Set<Block> defaultRoad = new HashSet<>() {{
                    add(Blocks.STONE);
                    add(Blocks.STONE_SLAB);
                    add(Blocks.STONE_STAIRS);
                }};

                vehicle.setRoadBlocks(defaultRoad);
                itemStack.decrement(1);
                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
            }

            return ActionResult.SUCCESS;
        }
    }
}

package toadsworthlp.funkykart.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import toadsworthlp.funkykart.FunkyKart;
import toadsworthlp.funkykart.entity.AbstractVehicleEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoadConfiguratorItem extends Item {
    private static final String ROAD_BLOCKS_KEY = "RoadBlocks";

    public RoadConfiguratorItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Set<Block> roadBlocks = loadRoadBlocks(stack);

        if(roadBlocks.size() > 0) {
            tooltip.add(new TranslatableText("tooltip." + FunkyKart.MODID + ".road_block_list_header").formatted(Formatting.GRAY));
            roadBlocks.iterator().forEachRemaining((Block block) -> {
                tooltip.add(new TranslatableText(block.getTranslationKey()).formatted(Formatting.GRAY));
            });
        } else {
            tooltip.add(new TranslatableText("tooltip." + FunkyKart.MODID + ".no_road_blocks_set").formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        boolean sneaking = context.getPlayer().isSneaking();
        Block clickedBlock = context.getWorld().getBlockState(context.getBlockPos()).getBlock();

        if(sneaking) {
            if(removeRoadBlock(clickedBlock, context.getStack())) {
                context.getPlayer().sendMessage(
                        new TranslatableText("message." + FunkyKart.MODID + ".remove_road_block")
                                .append(new TranslatableText(clickedBlock.getTranslationKey())),
                        true);
            }
        } else {
            if(addRoadBlock(clickedBlock, context.getStack())) {
                context.getPlayer().sendMessage(
                        new TranslatableText("message." + FunkyKart.MODID + ".add_road_block")
                                .append(new TranslatableText(clickedBlock.getTranslationKey())),
                        true);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        boolean sneaking = user.isSneaking();

        if(entity instanceof AbstractVehicleEntity vehicle) {
            if(sneaking) {
                vehicle.setRoadBlocks(loadRoadBlocks(stack));
                user.sendMessage(new TranslatableText("message." + FunkyKart.MODID + ".apply_road_blocks_success"), true);
            } else {
                user.sendMessage(new TranslatableText("message." + FunkyKart.MODID + ".apply_road_blocks_sneak"), false);
            }
        } else {
            user.sendMessage(new TranslatableText("message." + FunkyKart.MODID + ".apply_road_blocks_no_vehicle"), true);
        }

        return ActionResult.SUCCESS;
    }

    private boolean addRoadBlock(Block block, ItemStack itemStack) {
        Set<Block> roadBlocks = loadRoadBlocks(itemStack);
        boolean success = !roadBlocks.contains(block);
        roadBlocks.add(block);
        saveRoadBlocks(roadBlocks, itemStack);
        return success;
    }

    private boolean removeRoadBlock(Block block, ItemStack itemStack) {
        Set<Block> roadBlocks = loadRoadBlocks(itemStack);
        boolean success = roadBlocks.contains(block);
        roadBlocks.remove(block);
        saveRoadBlocks(roadBlocks, itemStack);
        return success;
    }

    private Set<Block> loadRoadBlocks(ItemStack itemStack) {
        Set<Block> roadBlocks = new HashSet<>();
        NbtCompound tag = itemStack.getOrCreateNbt();

        // Read road data
        if(tag.contains(ROAD_BLOCKS_KEY)) {
            NbtList list = (NbtList) tag.get(ROAD_BLOCKS_KEY);

            list.iterator().forEachRemaining((NbtElement element) -> {
                NbtCompound compound = (NbtCompound) element;
                roadBlocks.add(Registry.BLOCK.get(new Identifier(compound.getString("Id"))));
            });
        }

        return roadBlocks;
    }

    private void saveRoadBlocks(Set<Block> roadBlocks, ItemStack itemStack) {
        NbtCompound tag = itemStack.getOrCreateNbt();

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
        itemStack.writeNbt(tag);
    }
}

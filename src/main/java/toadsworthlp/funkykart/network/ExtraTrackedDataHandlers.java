package toadsworthlp.funkykart.network;

import net.minecraft.block.Block;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class ExtraTrackedDataHandlers {
    public static final TrackedDataHandler<Double> DOUBLE = new TrackedDataHandler<Double>() {
        public void write(PacketByteBuf packetByteBuf, Double double_) {
            packetByteBuf.writeDouble(double_);
        }

        public Double read(PacketByteBuf packetByteBuf) {
            return packetByteBuf.readDouble();
        }

        public Double copy(Double double_) {
            return double_;
        }
    };

    public static final TrackedDataHandler<Vec3d> VECTOR3 = new TrackedDataHandler<Vec3d>() {
        public void write(PacketByteBuf packetByteBuf, Vec3d vector3d_) {
            packetByteBuf.writeDouble(vector3d_.x);
            packetByteBuf.writeDouble(vector3d_.y);
            packetByteBuf.writeDouble(vector3d_.z);
        }

        public Vec3d read(PacketByteBuf packetByteBuf) {
            return new Vec3d(
                    packetByteBuf.readDouble(),
                    packetByteBuf.readDouble(),
                    packetByteBuf.readDouble()
            );
        }

        public Vec3d copy(Vec3d vector3d) {
            if(vector3d == null) {
                return Vec3d.ZERO;
            } else {
                return new Vec3d(
                        vector3d.x,
                        vector3d.y,
                        vector3d.z
                );
            }
        }
    };

    public static final TrackedDataHandler<Set<Block>> BLOCK_SET = new TrackedDataHandler<Set<Block>>() {
        public void write(PacketByteBuf packetByteBuf, Set<Block> blockSet) {
            packetByteBuf.writeInt(blockSet.size());
            blockSet.iterator().forEachRemaining((Block block) -> {
                packetByteBuf.writeIdentifier(Registry.BLOCK.getId(block));
            });
        }

        public Set<Block> read(PacketByteBuf packetByteBuf) {
            int size = packetByteBuf.readInt();
            Set<Block> blockSet = new HashSet<>(size);
            for (int i = 0; i < size; i++) {
                blockSet.add(Registry.BLOCK.get(packetByteBuf.readIdentifier()));
            }

            return blockSet;
        }

        public Set<Block> copy(Set<Block> blockSet) {
            if(blockSet == null) {
                return new HashSet<>();
            } else {
                return new HashSet<>(blockSet);
            }
        }
    };

    static {
        TrackedDataHandlerRegistry.register(DOUBLE);
        TrackedDataHandlerRegistry.register(VECTOR3);
        TrackedDataHandlerRegistry.register(BLOCK_SET);
    }
}
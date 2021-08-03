package toadsworthlp.funkykart.network;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

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
            return new Vec3d(
                    vector3d.x,
                    vector3d.y,
                    vector3d.z
            );
        }
    };

    static {
        TrackedDataHandlerRegistry.register(DOUBLE);
        TrackedDataHandlerRegistry.register(VECTOR3);
    }
}
package toadsworthlp.funkykart.network;

import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

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

    public static final TrackedDataHandler<Vector3d> VECTOR3 = new TrackedDataHandler<Vector3d>() {
        public void write(PacketByteBuf packetByteBuf, Vector3d vector3d_) {
            packetByteBuf.writeDouble(vector3d_.x);
            packetByteBuf.writeDouble(vector3d_.y);
            packetByteBuf.writeDouble(vector3d_.z);
        }

        public Vector3d read(PacketByteBuf packetByteBuf) {
            return new Vector3d(
                    packetByteBuf.readDouble(),
                    packetByteBuf.readDouble(),
                    packetByteBuf.readDouble()
            );
        }

        public Vector3d copy(Vector3d vector3d) {
            return new Vector3d(
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

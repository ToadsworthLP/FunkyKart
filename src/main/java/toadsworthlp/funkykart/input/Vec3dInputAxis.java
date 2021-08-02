package toadsworthlp.funkykart.input;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class Vec3dInputAxis extends BaseInputAxis<Vec3d> {
    public Vec3dInputAxis(Vec3d initialState) {
        super(initialState);
    }

    @Override
    public void readFromBuffer(PacketByteBuf buffer) {
        setState(new Vec3d(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        ));
    }
}

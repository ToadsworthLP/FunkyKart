package toadsworthlp.funkykart.client.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.input.Vec3dInputAxis;

public class LookClientInputAxis extends Vec3dInputAxis implements IClientInputAxis<Vec3d, Vec3dInputAxis> {
    public LookClientInputAxis(Vec3d initialState) {
        super(initialState);
    }

    @Override
    public void updateInput() {
        Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
        setState(cameraEntity == null ? Vec3d.ZERO : cameraEntity.getRotationVector());
    }

    @Override
    public void writeToBuffer(PacketByteBuf buffer) {
        buffer.writeDouble(getCurrentState().x);
        buffer.writeDouble(getCurrentState().y);
        buffer.writeDouble(getCurrentState().z);
    }

    @Override
    public void copyTo(Vec3dInputAxis target) {
        target.setState(getCurrentState());
    }
}

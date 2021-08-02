package toadsworthlp.funkykart.client.input;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import toadsworthlp.funkykart.input.Vec3dInputAxis;

public class LookClientInputAxis extends Vec3dInputAxis implements IClientInputAxis<Vec3d, Vec3dInputAxis> {
    public Entity cameraEntity;

    public LookClientInputAxis(Vec3d initialState, Entity cameraEntity) {
        super(initialState);
        this.cameraEntity = cameraEntity;
    }

    @Override
    public void updateInput() {
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

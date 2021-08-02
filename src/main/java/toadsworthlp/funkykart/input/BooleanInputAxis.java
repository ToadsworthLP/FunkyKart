package toadsworthlp.funkykart.input;

import net.minecraft.network.PacketByteBuf;

public class BooleanInputAxis extends BaseInputAxis<Boolean> {
    public BooleanInputAxis(Boolean initialState) {
        super(initialState);
    }

    @Override
    public void readFromBuffer(PacketByteBuf buffer) {
        setState(buffer.readBoolean());
    }
}

package toadsworthlp.funkykart.client.input;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import toadsworthlp.funkykart.input.BooleanInputAxis;

public class ButtonClientInputAxis extends BooleanInputAxis implements IClientInputAxis<Boolean, BooleanInputAxis> {
    private final KeyBinding keyBinding;

    public ButtonClientInputAxis(Boolean initialState, KeyBinding keyBinding) {
        super(initialState);
        this.keyBinding = keyBinding;
    }

    @Override
    public void updateInput() {
        setState(keyBinding.isPressed());
    }

    @Override
    public void writeToBuffer(PacketByteBuf buffer) {
        buffer.writeBoolean(getCurrentState());
    }

    @Override
    public void copyTo(BooleanInputAxis target) {
        target.setState(getCurrentState());
    }
}

package toadsworthlp.funkykart.input;

import net.minecraft.network.PacketByteBuf;

public abstract class BaseInputAxis<T> {
    private T currentState;

    public BaseInputAxis(T initialState) {
        currentState = initialState;
    }

    public T getCurrentState() {
        return currentState;
    }

    public void setState(T state) {
        currentState = state;
    }

    public abstract void readFromBuffer(PacketByteBuf buffer);
}

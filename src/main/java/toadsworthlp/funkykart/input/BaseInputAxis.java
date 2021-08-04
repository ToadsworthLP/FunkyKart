package toadsworthlp.funkykart.input;

import net.minecraft.network.PacketByteBuf;

public abstract class BaseInputAxis<T> {
    private T currentState;
    private T initialState;

    public BaseInputAxis(T initialState) {
        this.initialState = initialState;
        this.currentState = initialState;
    }

    public T getCurrentState() {
        return currentState;
    }

    public void setState(T state) {
        currentState = state;
    }

    public void resetState() {
        currentState = initialState;
    }

    public abstract void readFromBuffer(PacketByteBuf buffer);
}

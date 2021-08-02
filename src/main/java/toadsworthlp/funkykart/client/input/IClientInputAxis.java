package toadsworthlp.funkykart.client.input;

import net.minecraft.network.PacketByteBuf;

public interface IClientInputAxis<T, TBase> {
    void updateInput();
    void writeToBuffer(PacketByteBuf buffer);
    void copyTo(TBase target);
}

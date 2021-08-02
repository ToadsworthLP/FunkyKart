package toadsworthlp.funkykart.client.input;

import net.minecraft.client.option.KeyBinding;

public class InputState {
    private final KeyBinding binding;
    private boolean previousPressed = false;
    private boolean pressed = false;

    public InputState(KeyBinding binding) {
        this.binding = binding;
    }

    public boolean update() {
        previousPressed = pressed;
        pressed = binding.isPressed();

        return pressed != previousPressed;
    }

    public boolean getState() {
        return pressed;
    }

    public boolean getPreviousState() {
        return previousPressed;
    }
}

package toadsworthlp.funkykart.input;

import java.lang.reflect.Type;

public class InputAxisType {
    public static Type of(InputAxis axis) {
        switch (axis) {
            case STEER -> {
                return Vec3dInputAxis.class;
            }
            default -> {
                return BooleanInputAxis.class;
            }
        }
    }
}

package toadsworthlp.funkykart.util;

import net.minecraft.util.math.Vec3d;

public class Vec3dUtil {
    public static final Vec3d UP = new Vec3d(0, 1, 0);
    public static final Vec3d FORWARD = new Vec3d(0, 0, 1);
    public static final Vec3d LEFT = new Vec3d(1, 0, 0);

    public static double angleBetween(Vec3d a, Vec3d b) {
        double result = Math.acos((a.dotProduct(b)) / (a.length() * b.length()));
        return Double.isNaN(result) ? 0.0 : result;
    }

    public static Vec3d projectOnPlane(Vec3d vector, Vec3d normal) {
        return vector.subtract(normal.multiply(normal.dotProduct(vector)));
    }
}

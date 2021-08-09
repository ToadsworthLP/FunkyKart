package toadsworthlp.funkykart.util;

import net.minecraft.util.math.Vec3d;

public class Vec3dUtil {
    public static double angleBetween(Vec3d a, Vec3d b) {
        double result = Math.acos((a.dotProduct(b)) / (a.length() * b.length()));
        return Double.isNaN(result) ? 0.0 : result;
    }

    public static Vec3d projectOnPlane(Vec3d vector, Vec3d normal) {
        return vector.subtract(normal.multiply(normal.dotProduct(vector)));
    }
}

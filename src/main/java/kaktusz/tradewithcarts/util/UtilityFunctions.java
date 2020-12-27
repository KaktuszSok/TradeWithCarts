package kaktusz.tradewithcarts.util;

import java.util.Random;


public class UtilityFunctions {
    public static class Maths
    {
        static final Random rand = new Random();

        public static float clamp(float current, float min, float max)
        {
            return Math.max(min, Math.min(max, current));
        }

        public static float lerp(float a, float b, float t)
        {
            t = clamp(t, 0f, 1f);
            return lerpUnclamped(a, b, t);
        }
        public static float lerpUnclamped(float a, float b, float t)
        {
            return (a * (1.0f - t)) + (b * t);
        }

        public static float randomRange(float min, float max)
        {
            return lerp(min, max, rand.nextFloat());
        }

        public static int randomRange(int min, int max)
        {
            return min + rand.nextInt((max-min)+1);
        }
    }
}

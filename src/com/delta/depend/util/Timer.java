package com.delta.depend.util;

/**
 * It is just a timer.<br/>
 * It will start when first used.
 *
 * @author Jim Zhang
 * @since Delta1.0
 */
@SuppressWarnings("ALL")
public final class Timer {
    private static long current = System.currentTimeMillis();
    private static final long start = current;

    private Timer() {
    }

    /**
     * Distance to checkpoint.
     */
    public static long getTime() {
        long now = System.currentTimeMillis();
        long time = now - current;

        // new current point
        current = now;
        return time;
    }

    /**
     * Distance to start point.
     */
    public static long getTotalTime() {
        return System.currentTimeMillis() - start;
    }

}

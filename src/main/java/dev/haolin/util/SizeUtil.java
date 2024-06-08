package dev.haolin.util;

import org.github.jamm.MemoryMeter;

public class SizeUtil {

    private static final MemoryMeter meter = MemoryMeter.builder().build();

    private SizeUtil() {}

    public static long byteSizeOf(Object object) {
        return meter.measureDeep(object);
    }

    public static long byteSizeOf(Object... objects) {
        return meter.measureArray(objects);
    }
}

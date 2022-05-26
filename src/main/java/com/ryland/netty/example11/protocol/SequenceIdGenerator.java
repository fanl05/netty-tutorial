package com.ryland.netty.example11.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryland
 */
public abstract class SequenceIdGenerator {

    private static final AtomicInteger ID = new AtomicInteger();

    private SequenceIdGenerator() {
    }

    public static int nextId() {
        return ID.incrementAndGet();
    }
}

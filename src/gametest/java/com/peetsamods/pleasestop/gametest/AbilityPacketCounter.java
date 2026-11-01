package com.peetsamods.pleasestop.gametest;
import java.util.concurrent.atomic.AtomicLong;
public final class AbilityPacketCounter {
 private static final AtomicLong FLYING_TRUE = new AtomicLong();
 private static final AtomicLong FLYING_FALSE = new AtomicLong();
 private AbilityPacketCounter() {}
 public static void record(boolean flying) { (flying ? FLYING_TRUE : FLYING_FALSE).incrementAndGet(); }
 public static long[] snapshot() { return new long[]{FLYING_TRUE.get(), FLYING_FALSE.get()}; }
}

package dev.kir.netherchest.inventory;

import net.minecraft.util.Util;

import java.util.function.Function;
import java.util.stream.IntStream;

final class NetherChestInventoryViewSlots {
    private static final Function<Integer, NetherChestInventoryViewSlots> FACTORY = Util.memoize(NetherChestInventoryViewSlots::new);

    private final int[] horizontalSlots;
    private final int[] verticalSlots;

    private NetherChestInventoryViewSlots(int channelSize) {
        this.horizontalSlots = new int[] { channelSize };
        this.verticalSlots = IntStream.range(0, channelSize).toArray();
    }

    public static NetherChestInventoryViewSlots forChannelSize(int channelSize) {
        return FACTORY.apply(channelSize);
    }

    public int[] getHorizontalSlots() {
        return this.horizontalSlots;
    }

    public int[] getVerticalSlots() {
        return this.verticalSlots;
    }
}
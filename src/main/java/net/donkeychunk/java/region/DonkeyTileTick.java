package net.donkeychunk.java.region;

/**
 * Represents a pending tile tick.
 */
public class DonkeyTileTick {

    private final int x;
    private final int y;
    private final int z;
    private final int block;
    private final int ticksLeft;
    private final int priority;

    public DonkeyTileTick(int x, int y, int z, int block, int ticksLeft, int priority) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.ticksLeft = ticksLeft;
        this.priority = priority;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getBlock() {
        return block;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public int getPriority() {
        return priority;
    }

}

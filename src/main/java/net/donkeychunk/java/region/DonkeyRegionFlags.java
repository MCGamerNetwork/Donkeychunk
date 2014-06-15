package net.donkeychunk.java.region;

/**
 * Specifies the flags for a {@link DonkeyRegion}.
 */
public class DonkeyRegionFlags {

    private boolean hasChunkTimestamps = false;
    private boolean hasChunkCompression = true;

    public DonkeyRegionFlags() {
    }

    public DonkeyRegionFlags(int flags) {
        hasChunkTimestamps = (flags & 15) > 7;
        hasChunkCompression = (flags >> 4 & 15) > 7;
    }

    public boolean hasChunkTimestamps() {
        return hasChunkTimestamps;
    }

    public void setChunkTimestamps(boolean hasChunkTimestamps) {
        this.hasChunkTimestamps = hasChunkTimestamps;
    }

    public boolean hasChunkCompression() {
        return hasChunkCompression;
    }

    public void setChunkCompression(boolean hasChunkCompression) {
        this.hasChunkCompression = hasChunkCompression;
    }

    public int getFlags() {
        int flags = 0;
        flags |= (hasChunkTimestamps ? 0xF : 0x0);
        flags |= ((hasChunkCompression ? 0xF : 0x0) << 4);
        return flags;
    }
}

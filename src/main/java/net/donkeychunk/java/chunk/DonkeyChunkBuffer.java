package net.donkeychunk.java.chunk;

import java.io.ByteArrayOutputStream;
import net.donkeychunk.java.region.DonkeyRegion;

/**
 *
 */
public class DonkeyChunkBuffer extends ByteArrayOutputStream {

    private final DonkeyRegion region;
    private final int x;
    private final int z;

    public DonkeyChunkBuffer(DonkeyRegion region, int x, int z) {
        this.region = region;
        this.x = x;
        this.z = z;
    }

    @Override
    public void close() {
        region.writeChunkDataToRegion(x, z, buf, count);
    }
}

package net.donkeychunk.java.chunk;

/**
 * Specifies the flags for a {@link DonkeyChunk}.
 */
public class DonkeyChunkFlags {

    private boolean lastUpdatePresent = false;
    private boolean biomeArrayPresent = false;
    private boolean addBlockArrayPresent = false;
    private boolean blockLightPresent = false;
    private boolean skyLightPresent = false;
    private boolean tileTicksPresent = false;
    private boolean entitiesPresent = false;
    private boolean tileEntitiesPresent = false;

    public DonkeyChunkFlags() {
    }

    public DonkeyChunkFlags(int flags) {
        lastUpdatePresent = (flags & 1) > 0;
        biomeArrayPresent = (flags >> 1 & 1) > 0;
        addBlockArrayPresent = (flags >> 2 & 1) > 0;
        blockLightPresent = (flags >> 3 & 1) > 0;
        skyLightPresent = (flags >> 4 & 1) > 0;
        tileTicksPresent = (flags >> 5 & 1) > 0;
        entitiesPresent = (flags >> 6 & 1) > 0;
        tileEntitiesPresent = (flags >> 7 & 1) > 0;
    }

    public boolean isLastUpdatePresent() {
        return lastUpdatePresent;
    }

    public void setLastUpdatePresent(boolean lastUpdatePresent) {
        this.lastUpdatePresent = lastUpdatePresent;
    }

    public boolean isBiomeArrayPresent() {
        return biomeArrayPresent;
    }

    public void setBiomeArrayPresent(boolean biomeArrayPresent) {
        this.biomeArrayPresent = biomeArrayPresent;
    }

    public boolean isAddBlockArrayPresent() {
        return addBlockArrayPresent;
    }

    public void setAddBlockArrayPresent(boolean addBlockArrayPresent) {
        this.addBlockArrayPresent = addBlockArrayPresent;
    }

    public boolean isBlockLightPresent() {
        return blockLightPresent;
    }

    public void setBlockLightPresent(boolean blockLightPresent) {
        this.blockLightPresent = blockLightPresent;
    }

    public boolean isSkyLightPresent() {
        return skyLightPresent;
    }

    public void setSkyLightPresent(boolean skyLightPresent) {
        this.skyLightPresent = skyLightPresent;
    }

    public boolean isTileTickListPresent() {
        return tileTicksPresent;
    }

    public void setTileTickListPresent(boolean tileTicksPresent) {
        this.tileTicksPresent = tileTicksPresent;
    }

    public boolean isEntityListPresent() {
        return entitiesPresent;
    }

    public void setEntityListPresent(boolean entitiesPresent) {
        this.entitiesPresent = entitiesPresent;
    }

    public boolean isTileEntityListPresent() {
        return tileEntitiesPresent;
    }

    public void setTileEntityListPresent(boolean tileEntitiesPresent) {
        this.tileEntitiesPresent = tileEntitiesPresent;
    }

    public int getFlags() {
        int flags = 0;
        flags |= lastUpdatePresent ? 1 : 0;
        flags |= (biomeArrayPresent ? 1 : 0) << 1;
        flags |= (addBlockArrayPresent ? 1 : 0) << 2;
        flags |= (blockLightPresent ? 1 : 0) << 3;
        flags |= (skyLightPresent ? 1 : 0) << 4;
        flags |= (tileTicksPresent ? 1 : 0) << 5;
        flags |= (entitiesPresent ? 1 : 0) << 6;
        flags |= (tileEntitiesPresent ? 1 : 0) << 7;

        return flags;
    }
}

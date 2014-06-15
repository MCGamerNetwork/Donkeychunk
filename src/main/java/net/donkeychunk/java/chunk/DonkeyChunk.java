package net.donkeychunk.java.chunk;

import com.evilco.mc.nbt.tag.ITag;
import java.util.LinkedList;
import java.util.List;
import net.donkeychunk.java.region.DonkeyTileTick;

/**
 * Represents a chunk handled by Donkeychunk.
 */
public class DonkeyChunk {

    private final int x;
    private final int z;
    private final DonkeyChunkFlags flags;
    private boolean terrainPopulated;
    private boolean lightPopulated;
    private long inhabitedTime;
    private byte[] biomeMap;
    private int[] heightMap;
    private DonkeyChunkSection[] sections = new DonkeyChunkSection[16];
    private List<DonkeyTileTick> tileTicks = new LinkedList<DonkeyTileTick>();
    private List<ITag> entityList = new LinkedList<ITag>();
    private List<ITag> tileEntityList = new LinkedList<ITag>();

    public DonkeyChunk(int x, int z, DonkeyChunkFlags flags) {
        this.x = x;
        this.z = z;
        this.flags = flags;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public DonkeyChunkFlags getFlags() {
        return flags;
    }

    public boolean isTerrainPopulated() {
        return terrainPopulated;
    }

    public void setTerrainPopulated(boolean terrainPopulated) {
        this.terrainPopulated = terrainPopulated;
    }

    public boolean isLightPopulated() {
        return lightPopulated;
    }

    public void setLightPopulated(boolean lightPopulated) {
        this.lightPopulated = lightPopulated;
    }

    public long getInhabitedTime() {
        return inhabitedTime;
    }

    public void setInhabitedTime(long inhabitedTime) {
        this.inhabitedTime = inhabitedTime;
    }

    public byte[] getBiomeMap() {
        return biomeMap;
    }

    public void setBiomeMap(byte[] biomeMap) {
        this.biomeMap = biomeMap;
    }

    public int[] getHeightMap() {
        return heightMap;
    }

    public void setHeightMap(int[] heightMap) {
        this.heightMap = heightMap;
    }

    public DonkeyChunkSection[] getSections() {
        return sections;
    }

    public void setSections(DonkeyChunkSection[] sections) {
        this.sections = sections;
    }

    public List<DonkeyTileTick> getTileTicks() {
        return tileTicks;
    }

    public List<ITag> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<ITag> entityList) {
        this.entityList = entityList;
    }

    public List<ITag> getTileEntityList() {
        return tileEntityList;
    }

    public void setTileEntityList(List<ITag> tileEntityList) {
        this.tileEntityList = tileEntityList;
    }

}

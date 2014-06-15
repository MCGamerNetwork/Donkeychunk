package net.donkeychunk.java.chunk;

/**
 * Represents a section of a {@link DonkeyChunk}.
 */
public class DonkeyChunkSection {

    private final int y;
    private byte[] blockArray;
    private byte[] blockAddArray;
    private byte[] blockDataArray;
    private byte[] lightArray;
    private byte[] skyLightArray;

    public DonkeyChunkSection(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public byte[] getBlockArray() {
        return blockArray;
    }

    public void setBlockArray(byte[] blockArray) {
        this.blockArray = blockArray;
    }

    public byte[] getBlockAddArray() {
        return blockAddArray;
    }

    public void setBlockAddArray(byte[] blockAddArray) {
        this.blockAddArray = blockAddArray;
    }

    public byte[] getBlockDataArray() {
        return blockDataArray;
    }

    public void setBlockDataArray(byte[] blockDataArray) {
        this.blockDataArray = blockDataArray;
    }

    public byte[] getLightArray() {
        return lightArray;
    }

    public void setLightArray(byte[] lightArray) {
        this.lightArray = lightArray;
    }

    public byte[] getSkyLightArray() {
        return skyLightArray;
    }

    public void setSkyLightArray(byte[] skyLightArray) {
        this.skyLightArray = skyLightArray;
    }

}

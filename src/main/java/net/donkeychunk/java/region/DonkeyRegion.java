package net.donkeychunk.java.region;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.donkeychunk.java.DonkeyConstants;
import net.donkeychunk.java.chunk.DonkeyChunkBuffer;
import net.donkeychunk.java.error.NotADonkeyException;
import net.donkeychunk.java.error.UnsupportedDonkeyException;

/**
 * Handler for a Donkeychunk region file (.dnk).
 */
public class DonkeyRegion {

    private final File hardFile;
    private final RandomAccessFile file;
    private long lastModification;
    private byte version = DonkeyConstants.DONKEY_VERSION;
    // Flags
    private boolean hasChunkTimestamps = false;
    private boolean hasChunkCompression = true;

    public DonkeyRegion(File hardFile) {
        this.hardFile = hardFile;

        try {
            if (hardFile.exists()) {
                lastModification = hardFile.lastModified();
            }

            file = new RandomAccessFile(hardFile, "rw");
            if (file.length() < 5) {
                // Write the Donkeychunk magic
                file.writeByte(0x44);
                file.writeByte(0x4e);
                file.writeByte(0x4b);
                // Write the version
                file.writeByte(version);
                // Write the default flags
                file.writeByte(getFlags());
            }

            if (file.length() < 7173) {
                // Write a blank location map
                file.write(new byte[7168]);
            }

            file.seek(0L);

            if (!(file.readByte() == 0x44 && file.readByte() == 0x4e && file.readByte() == 0x4b)) {
                throw new NotADonkeyException("Donkeychunk signature invalid");
            }

            version = file.readByte();
            if (version > DonkeyConstants.DONKEY_VERSION) {
                throw new UnsupportedDonkeyException("Unsupported Donkeychunk version (region: " + version + ", library: " + DonkeyConstants.DONKEY_VERSION + ")");
            }

            byte flags = file.readByte();
            hasChunkTimestamps = (flags & 15) > 7;
            hasChunkCompression = (flags >> 4 & 15) > 7;
        } catch (IOException ex) {
            throw new NotADonkeyException(ex);
        }
    }

    public NbtInputStream getChunkInputStream(int x, int z) {
        try {
            byte[] data;
            int read;
            int offset;
            int length;

            synchronized (file) {
                file.seek((7 * (x + (z * 32))) + 5);
                offset = file.readInt();
                length = ((file.readByte() << 16) & 0x00FF0000) | ((file.readByte() << 8) & 0x0000FF00) | (file.readByte() & 0x000000FF);
                if (offset < 7173 || length <= 0) {
                    // Either way, it's invalid
                    return null;
                }

                file.seek(offset);
                data = new byte[length];
                read = file.read(data);
            }

            if (read < length) {
                throw new DonkeyRegionException("(" + hardFile.getName() + ":" + x + ", " + z + ") Did not read all bytes for chunk! Expected " + length + " bytes, read " + read + " bytes");
            }

            if (hasChunkCompression) {
                return new NbtInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))));
            } else {
                return new NbtInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public NbtOutputStream getChunkOutputStream(int x, int z) {
        if (isOutOfBounds(x, z)) {
            return null;
        }

        try {
            if (hasChunkCompression) {
                return new NbtOutputStream(new GZIPOutputStream(new DonkeyChunkBuffer(this, x, z)));
            } else {
                return new NbtOutputStream(new DonkeyChunkBuffer(this, x, z));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void writeChunkDataToRegion(int x, int z, byte[] buffer, int length) {
        try {
            synchronized (file) {
                file.seek((7 * (x + (z * 32))) + 5);
                int offset = file.readInt();
                int oldLength = ((file.readByte() << 16) & 0x00FF0000) | ((file.readByte() << 8) & 0x0000FF00) | (file.readByte() & 0x000000FF);
                if (offset < 0 || oldLength < 0) {
                    offset = 0;
                    oldLength = 0;
                }

                if (oldLength == length) {
                    // Our job is easy here, we don't need to modify offset / length
                    file.seek(offset);
                    file.write(buffer, 0, length);
                } else if (oldLength == 0 || offset < 7173) {
                    // Chunk doesn't exist, claim a new spot for it
                    int newOffset = (int) file.length();

                    file.seek(newOffset);
                    file.write(buffer, 0, length);

                    // Go update the offset and size in the location map
                    file.seek((7 * (x + (z * 32))) + 5);
                    file.writeInt(newOffset);
                    file.writeByte((length >> 16) & 0xFF);
                    file.writeByte((length >> 8) & 0xFF);
                    file.writeByte(length & 0xFF);
                } else {
                    // Modify the file size
                    // Copy the rest to the byte array
                    byte[] restOfFile = new byte[(int) file.length() - (offset + oldLength)];
                    file.seek(offset + oldLength);
                    file.readFully(restOfFile);
                    
                    // Copy to another position
                    file.seek(offset + length);
                    file.write(restOfFile);

                    // And write the new data
                    file.seek(offset);
                    file.write(buffer, 0, length);

                    // If needed, reduce the file size
                    if (oldLength > length) {
                        file.setLength((int) file.length() - (oldLength - length));
                    }

                    // Go update the size in the location map
                    file.seek((7 * (x + (z * 32))) + 5 + 4);
                    file.writeByte((length >> 16) & 0xFF);
                    file.writeByte((length >> 8) & 0xFF);
                    file.writeByte(length & 0xFF);

                    // Now go through the location map and modify offsets, starting at the beginning
                    int difference = length - oldLength;
                    file.seek(5);
                    while (file.getFilePointer() <= 7166) {
                        long oldPointer = file.getFilePointer();
                        int otherOffset = file.readInt();

                        if (otherOffset > offset) {
                            otherOffset += difference;

                            file.seek(oldPointer);
                            file.writeInt(otherOffset);
                        }

                        file.seek(oldPointer + 7);
                    }
                }

                // Update the chunk timestamp, if necessary
                if (hasChunkTimestamps) {
                    file.seek((7 * (x + (z * 32))) + 5 + 7168);
                    file.writeInt((int) (System.currentTimeMillis() / 1000L));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void close() throws IOException {
        if (file != null) {
            synchronized (file) {
                file.close();
            }
        }
    }

    private byte getFlags() {
        int flags = 0;
        flags = flags | (hasChunkTimestamps ? 0xF : 0x0);
        flags = flags | ((hasChunkCompression ? 0xF : 0x0) << 4);
        return (byte) flags;
    }

    private boolean isOutOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }
}

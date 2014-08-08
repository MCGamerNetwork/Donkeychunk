package net.donkeychunk.java.chunk;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.donkeychunk.java.nbt.NBTParser;
import net.donkeychunk.java.nbt.NBTTagCompound;
import net.donkeychunk.java.nbt.NBTTagList;
import net.donkeychunk.java.nbt.NBTWriter;
import net.donkeychunk.java.region.DonkeyRegionCache;
import net.donkeychunk.java.region.DonkeyTileTick;

/**
 * Handles loading and saving chunks.
 */
public class DonkeyChunkHandler {

    private final File worldFile;

    public DonkeyChunkHandler(File world) {
        this.worldFile = world;
    }

    /**
     * Loads a chunk at the given X and Z coordinates and returns it.
     *
     * @param x
     * @param z
     * @return
     */
    public DonkeyChunk getChunk(int x, int z) {
        try {
            DataInputStream in = DonkeyRegionCache.getChunkInputStream(worldFile, x, z);

            if (in == null) {
                return null;
            }

            // Read the chunk
            DonkeyChunkFlags flags = new DonkeyChunkFlags(in.readByte());

            byte readX = in.readByte();
            byte readZ = in.readByte();

            if (flags.isLastUpdatePresent()) {
                in.readLong();
            }

            DonkeyChunk chunk = new DonkeyChunk(x, z, flags);
            int[] heightmap = new int[256];
            chunk.setLightPopulated(in.readByte() > 0 ? true : false);
            chunk.setTerrainPopulated(in.readByte() > 0 ? true : false);
            chunk.setInhabitedTime(in.readInt());

            if (flags.isBiomeArrayPresent()) {
                byte[] biomeMap = new byte[256];
                in.read(biomeMap);
                chunk.setBiomeMap(biomeMap);
            }

            for (int i = 0; i < 256; i++) {
                heightmap[i] = in.readByte();
            }
            chunk.setHeightMap(heightmap);

            byte sectionCount = in.readByte();
            DonkeyChunkSection[] sections = new DonkeyChunkSection[16];
            for (int i = 0; i < sectionCount; i++) {
                byte y = in.readByte();
                DonkeyChunkSection section = new DonkeyChunkSection(y);

                byte[] blocks = new byte[4096];
                in.read(blocks);
                section.setBlockArray(blocks);

                if (flags.isAddBlockArrayPresent()) {
                    byte[] add = new byte[2048];
                    in.read(add);
                    section.setBlockAddArray(add);
                }

                byte[] blockData = new byte[2048];
                in.read(blockData);
                section.setBlockDataArray(blockData);

                if (flags.isBlockLightPresent()) {
                    byte[] blockLight = new byte[2048];
                    in.read(blockLight);
                    section.setLightArray(blockLight);
                }

                if (flags.isSkyLightPresent()) {
                    byte[] skyLight = new byte[2048];
                    in.read(skyLight);
                    section.setSkyLightArray(skyLight);
                }

                sections[y & 15] = section;
            }

            chunk.setSections(sections);

            // Tile Ticks
            if (flags.isTileEntityListPresent()) {
                int pendingTileTicks = in.readUnsignedShort();
                if (pendingTileTicks > 0) {
                    for (int i = 0; i < pendingTileTicks; i++) {
                        int blockId = in.readByte();
                        int blockX = in.readByte();
                        int blockY = in.readByte();
                        int blockZ = in.readByte();
                        int ticksLeft = in.readUnsignedShort();
                        int priority = in.readUnsignedByte();

                        chunk.getTileTicks().add(new DonkeyTileTick(blockX, blockY, blockZ, blockId, ticksLeft, priority));
                    }
                }
            }

            // Read the entity / tile entity list
            if (flags.isEntityListPresent() || flags.isTileEntityListPresent()) {
                NBTTagCompound compound = NBTParser.parseNBTStream(in);

                if (compound != null) {
                    if (flags.isEntityListPresent()) {
                        NBTTagList entityList = (NBTTagList) compound.get("Entities");
                        if (entityList != null) {
                            chunk.setEntityList(entityList.getItems());
                        }
                    }

                    if (flags.isTileEntityListPresent()) {
                        NBTTagList tileEntityList = (NBTTagList) compound.get("TileEntities");
                        if (tileEntityList != null) {
                            chunk.setTileEntityList(tileEntityList.getItems());
                        }
                    }
                }
            }

            return chunk;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Saves a single chunk.
     *
     * @param chunk the chunk to save
     */
    public void saveChunk(DonkeyChunk chunk) {
        try {
            DataOutputStream dataOut = DonkeyRegionCache.getChunkOutputStream(worldFile, chunk.getX(), chunk.getZ());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            DonkeyChunkSection[] sections = chunk.getSections();

            // Get the flags
            DonkeyChunkFlags flags = chunk.getFlags();
            flags.setBiomeArrayPresent(false);
            flags.setAddBlockArrayPresent(false);

            // Do any flags need to be updated?
            // Biome map
            if (chunk.getBiomeMap() != null) {
                for (byte b : chunk.getBiomeMap()) {
                    if (b != 0) {
                        flags.setBiomeArrayPresent(true);
                        break;
                    }
                }
            }

            // Add block array
            for (DonkeyChunkSection section : sections) {
                if (section != null && section.getBlockAddArray() != null) {
                    for (byte b : section.getBlockAddArray()) {
                        if (b != 0) {
                            flags.setAddBlockArrayPresent(true);
                            break;
                        }
                    }
                }
            }

            // Start assembling the basics
            out.writeByte(chunk.getX());
            out.writeByte(chunk.getZ());

            if (flags.isLastUpdatePresent()) {
                out.writeLong(0L);
            }

            out.writeByte(chunk.isLightPopulated() ? 1 : 0);
            out.writeByte(chunk.isTerrainPopulated() ? 1 : 0);
            out.writeInt((int) chunk.getInhabitedTime());

            if (flags.isBiomeArrayPresent()) {
                out.write(chunk.getBiomeMap());
            }

            for (int height : chunk.getHeightMap()) {
                out.writeByte((byte) height);
            }

            // Are there any empty sections? Additional check that Anvil doesn't do.
            int fullSectionCount = 0;
            for (int i = 0; i < sections.length; i++) {
                DonkeyChunkSection section = sections[i];
                boolean sectionEmpty = true;

                if (section != null) {
                    // Is the section completely empty?
                    for (byte b : section.getBlockArray()) {
                        if (b != 0) {
                            sectionEmpty = false;
                            break;
                        }
                    }

                    if (section.getBlockAddArray() != null) {
                        for (byte b : section.getBlockAddArray()) {
                            if (b != 0) {
                                sectionEmpty = false;
                                break;
                            }
                        }
                    }

                    if (sectionEmpty) {
                        sections[i] = null;
                    } else {
                        fullSectionCount++;
                    }
                }
            }

            out.writeByte((byte) fullSectionCount);

            // Start assembling the sections
            for (int i = 0; i < sections.length; i++) {
                DonkeyChunkSection section = sections[i];

                if (section != null) {
                    out.writeByte(section.getY());
                    out.write(section.getBlockArray());

                    if (flags.isAddBlockArrayPresent()) {
                        if (section.getBlockAddArray() != null) {
                            out.write(section.getBlockAddArray());
                        }
                    }

                    out.write(section.getBlockDataArray());

                    if (flags.isBlockLightPresent()) {
                        out.write(section.getLightArray());
                    }

                    if (flags.isSkyLightPresent()) {
                        out.write(section.getSkyLightArray());
                    }
                }
            }

            // Tile Ticks
            List<DonkeyTileTick> tickList = chunk.getTileTicks();
            if (tickList == null) {
                flags.setTileTickListPresent(false);
            } else {
                flags.setTileTickListPresent(true);
                int tickListLength = tickList.size();
                out.writeShort(tickListLength);

                for (DonkeyTileTick entry : tickList) {
                    out.writeByte(entry.getBlock());
                    out.writeByte(entry.getX());
                    out.writeByte(entry.getY());
                    out.writeByte(entry.getZ());
                    out.writeShort(entry.getTicksLeft());
                    out.writeByte(entry.getPriority());
                }
            }

            // Entities / Tile Entities
            NBTTagList entityList = null;
            NBTTagList tileEntityList = null;

            if (chunk.getEntityList() != null && !chunk.getEntityList().isEmpty()) {
                flags.setEntityListPresent(true);
                entityList = new NBTTagList(10);
                entityList.getItems().addAll(chunk.getEntityList());
            } else {
                flags.setEntityListPresent(false);
            }

            if (chunk.getTileEntityList() != null && !chunk.getTileEntityList().isEmpty()) {
                flags.setTileEntityListPresent(true);
                tileEntityList = new NBTTagList(10);
                tileEntityList.getItems().addAll(chunk.getTileEntityList());
            } else {
                flags.setTileEntityListPresent(false);
            }

            if (flags.isEntityListPresent() || flags.isTileEntityListPresent()) {
                NBTTagCompound compound = new NBTTagCompound("NBTAdd");

                if (entityList != null) {
                    compound.set("Entities", entityList);
                }

                if (tileEntityList != null) {
                    compound.set("TileEntities", tileEntityList);
                }

                NBTWriter.writeNBTStream(compound, out);
                File dir = new File("debug");
                if (!dir.isDirectory()) {
                    dir.mkdir();
                }
                DataOutputStream debugOut = new DataOutputStream(new FileOutputStream(new File(dir, "chunk." + chunk.getX() + "." + chunk.getZ() + ".nbtdebug.dat")));
                NBTWriter.writeNBTStream(compound, debugOut);
            }

            // Write flags
            dataOut.writeByte((byte) flags.getFlags());
            out.close();
            dataOut.write(baos.toByteArray());

            // And finally, trigger an update in the region file
            dataOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

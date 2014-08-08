package net.donkeychunk.java.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a reusable cache for {@link DonkeyRegion} objects.
 */
public class DonkeyRegionCache {

    private static final Map<File, DonkeyRegion> cache = new HashMap<File, DonkeyRegion>();

    /**
     * Loads a DonkeyRegion with the specified coordinates.
     *
     * @param world
     * @param x
     * @param z
     * @return
     */
    public static synchronized DonkeyRegion getRegion(File world, int x, int z) {
        File regionDir = new File(world, "region");
        File regionFile = new File(regionDir, "r." + (x >> 5) + "." + (z >> 5) + ".dnk");
        DonkeyRegion region = cache.get(regionFile);
        
        if (region != null) {
            return region;
        } else {
            if (!regionDir.exists()) {
                regionDir.mkdirs();
            }
            
            if (cache.size() >= 256) {
                pruneRegions();
            }
            
            region = new DonkeyRegion(regionFile);
            cache.put(regionFile, region);
            
            return region;
        }
    }
    
    public static synchronized void pruneRegions() {
        
    }
    
    /**
     * Gets a chunk's input stream.
     * 
     * @param world
     * @param x
     * @param z
     * @return 
     */
    public static DataInputStream getChunkInputStream(File world, int x, int z) {
        return getRegion(world, x, z).getChunkInputStream(x & 31, z & 31);
    }
    
    /**
     * Gets a chunk's output stream.
     * 
     * @param world
     * @param x
     * @param z
     * @return 
     */
    public static DataOutputStream getChunkOutputStream(File world, int x, int z) {
        return getRegion(world, x, z).getChunkOutputStream(x & 31, z & 31);
    }
}

package net.donkeychunk.java.nbt;

import java.util.HashMap;
import java.util.Map;

/**
 * A standard NBT tag compound.
 */
public class NBTTagCompound {

    private String name;
    private Map<String, Object> data = new HashMap<String, Object>();

    public NBTTagCompound(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set(String key, Object value) {
        if (data.containsKey(key)) {
            data.remove(key);
        }

        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public NBTTagCompound getCompound(String key) {
        return (NBTTagCompound) data.get(key);
    }

    public Map<String, Object> getAll() {
        return data;
    }
}
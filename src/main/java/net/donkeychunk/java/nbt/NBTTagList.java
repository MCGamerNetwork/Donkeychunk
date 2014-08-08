package net.donkeychunk.java.nbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NBT list.
 */
public class NBTTagList {

    private final int type;
    private final List items;

    public NBTTagList(int type) {
        this.type = type;
        this.items = new ArrayList();
    }

    public NBTTagList(int type, Object... items) {
        this.type = type;
        this.items = Arrays.asList(items);
    }

    public int getType() {
        return type;
    }

    public List getItems() {
        return items;
    }

}

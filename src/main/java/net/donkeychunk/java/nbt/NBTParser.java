package net.donkeychunk.java.nbt;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * NBT parsing class.
 */
public class NBTParser {

    public static NBTTagCompound parseNBTStream(InputStream is) throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("Input stream must not be null");
        }

        DataInputStream in = null;

        if (is instanceof DataInputStream) {
            in = (DataInputStream) is;
        } else {
            in = new DataInputStream(is);
        }

        NBTKeyValuePair tag = getTagUnknown(in, true);
        if (tag != null && tag.getValue() instanceof NBTTagCompound) {
            return (NBTTagCompound) tag.getValue();
        } else {
            return null;
        }
    }

    private static NBTKeyValuePair getTagUnknown(DataInputStream in, boolean namedTags) throws IOException {
        try {
            int type = in.readUnsignedByte();
            if (type == 0) {
                return null;
            } else {
                String name = namedTags ? readString(in) : null;
                return getTag(in, name, type);
            }
        } catch (EOFException ex) {
            // We've reached the end of the file, give up hopelessly
            return null;
        }
    }

    private static NBTKeyValuePair getTag(DataInputStream in, String name, int type) throws IOException {
        switch (type) {
            case 0:
                return null;
            case 1:
                return new NBTKeyValuePair(name, in.readByte());
            case 2:
                return new NBTKeyValuePair(name, in.readShort());
            case 3:
                return new NBTKeyValuePair(name, in.readInt());
            case 4:
                return new NBTKeyValuePair(name, in.readLong());
            case 5:
                return new NBTKeyValuePair(name, in.readFloat());
            case 6:
                return new NBTKeyValuePair(name, in.readDouble());
            case 7:
                int byteArrayLength = in.readInt();
                byte[] byteArray = new byte[byteArrayLength];

                for (int i = 0; i < byteArrayLength; i++) {
                    byteArray[i] = in.readByte();
                }

                return new NBTKeyValuePair(name, byteArray);
            case 8:
                return new NBTKeyValuePair(name, readString(in));
            case 9:
                int childType = in.readUnsignedByte();
                int listLength = in.readInt();
                NBTTagList list = new NBTTagList(childType);
                for (int i = 0; i < listLength; i++) {
                    NBTKeyValuePair childPair = getTag(in, null, childType);
                    if (childPair == null || childPair.getValue() == null) {
                        break;
                    }

                    list.getItems().add(childPair.getValue());
                }

                return new NBTKeyValuePair(name, list);
            case 10:
                NBTTagCompound tagCompound = new NBTTagCompound(name);
                NBTKeyValuePair tag;
                while ((tag = getTagUnknown(in, true)) != null) {
                    if (tag.getKey() != null) {
                        tagCompound.set(tag.getKey(), tag.getValue());
                    }
                }

                return new NBTKeyValuePair(name, tagCompound);
            case 11:
                int intArrayLength = in.readInt();
                int[] intArray = new int[intArrayLength];

                for (int i = 0; i < intArrayLength; i++) {
                    intArray[i] = in.readInt();
                }

                return new NBTKeyValuePair(name, intArray);
            default:
                return null;
        }
    }

    /**
     * Reads a UTF-8 string.
     *
     * @param in
     * @return String read
     * @throws IOException
     */
    private static String readString(DataInputStream in) throws IOException {
        int len = in.readUnsignedShort();
        byte[] bytes = new byte[len];
        in.read(bytes);
        return new String(bytes, "UTF-8");
    }

    private static class NBTKeyValuePair {

        private final String key;
        private final Object value;

        public NBTKeyValuePair(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

    }
}

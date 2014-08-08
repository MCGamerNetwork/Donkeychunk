package net.donkeychunk.java.nbt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;

/**
 * NBT writing class.
 */
public class NBTWriter {

    public static void writeNBTStream(NBTTagCompound compound, OutputStream os) throws IOException {
        if (compound == null) {
            throw new IllegalArgumentException("Compound tag must not be null");
        }

        if (os == null) {
            throw new IllegalArgumentException("Output stream must not be null");
        }

        DataOutputStream out = null;

        if (os instanceof DataOutputStream) {
            out = (DataOutputStream) os;
        } else {
            out = new DataOutputStream(os);
        }

        writeTag(compound.getName(), compound, true, false, out);
    }

    private static void writeTag(String name, Object o, boolean named, boolean inList, DataOutputStream out) throws IOException {
        if (o instanceof Byte) {
            if (!inList) {
                out.writeByte(1);
            }

            if (named) {
                writeString(name, out);
            }

            out.writeByte((Byte) o);
        } else if (o instanceof Short) {
            if (!inList) {
                out.writeByte(2);
            }

            if (named) {
                writeString(name, out);
            }

            out.writeShort((Short) o);
        } else if (o instanceof Integer) {
            if (!inList) {
                out.writeByte(3);
            }

            if (named) {
                writeString(name, out);
            }

            out.writeInt((Integer) o);
        } else if (o instanceof Long) {
            if (!inList) {
                out.writeByte(4);
            }

            if (named) {
                writeString(name, out);
            }

            out.writeLong((Long) o);
        } else if (o instanceof Float) {
            if (!inList) {
                out.writeByte(5);
            }

            if (named) {
                writeString(name, out);
            }

            out.writeFloat((Float) o);
        } else if (o instanceof Double) {
            if (!inList) {
                out.writeByte(6);
            }

            if (named) {
                writeString(name, out);
            }

            out.writeDouble((Double) o);
        } else if (o instanceof Byte[]) {
            if (!inList) {
                out.writeByte(7);
            }

            if (named) {
                writeString(name, out);
            }

            byte[] arr = (byte[]) o;
            out.writeInt(arr.length);
            out.write(arr);
        } else if (o instanceof String) {
            if (!inList) {
                out.writeByte(8);
            }

            if (named) {
                writeString(name, out);
            }

            writeString((String) o, out);
        } else if (o instanceof NBTTagList) {
            if (!inList) {
                out.writeByte(9);
            }
            
            if (named) {
                writeString(name, out);
            }

            NBTTagList list = (NBTTagList) o;
            out.writeByte(list.getType());
            out.writeInt(list.getItems().size());

            for (Object listObject : list.getItems()) {
                writeTag(null, listObject, false, true, out);
            }
        } else if (o instanceof NBTTagCompound) {
            if (!inList) {
                out.writeByte(10);
            }

            if (named) {
                writeString(name, out);
            }

            for (Entry<String, Object> entry : ((NBTTagCompound) o).getAll().entrySet()) {
                writeTag(entry.getKey(), entry.getValue(), true, false, out);
            }
            
            out.writeByte(0);
        } else if (o instanceof Integer[]) {
            if (!inList) {
                out.writeByte(11);
            }

            if (named) {
                writeString(name, out);
            }

            Integer[] arr = (Integer[]) o;
            out.writeInt(arr.length);
            for (int i = 0; i < arr.length; i++) {
                out.writeInt(arr[i]);
            }
        }
    }

    private static void writeString(String s, DataOutputStream out) throws IOException {
        byte[] bytes = s.getBytes("UTF-8");
        out.writeShort(bytes.length);
        out.write(bytes);
    }
}

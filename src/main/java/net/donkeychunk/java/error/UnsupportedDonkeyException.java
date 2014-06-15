package net.donkeychunk.java.error;

/**
 * Thrown when this Donkeychunk library is too old to support the Donkeychunk
 * version specified.
 */
public class UnsupportedDonkeyException extends RuntimeException {

    public UnsupportedDonkeyException() {
    }

    public UnsupportedDonkeyException(String string) {
        super(string);
    }

    public UnsupportedDonkeyException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public UnsupportedDonkeyException(Throwable thrwbl) {
        super(thrwbl);
    }

    public UnsupportedDonkeyException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }

}

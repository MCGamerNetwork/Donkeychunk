package net.donkeychunk.java.error;

/**
 *
 */
public class DonkeyException extends RuntimeException {

    public DonkeyException() {
    }

    public DonkeyException(String string) {
        super(string);
    }

    public DonkeyException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public DonkeyException(Throwable thrwbl) {
        super(thrwbl);
    }

    public DonkeyException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }

}

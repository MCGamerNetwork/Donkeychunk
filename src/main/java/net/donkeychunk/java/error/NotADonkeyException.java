package net.donkeychunk.java.error;

/**
 * Exception thrown when the chunk isn't a donkey.
 */
public class NotADonkeyException extends RuntimeException {

    public NotADonkeyException() {
    }

    public NotADonkeyException(String string) {
        super(string);
    }

    public NotADonkeyException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public NotADonkeyException(Throwable thrwbl) {
        super(thrwbl);
    }

    public NotADonkeyException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }

}

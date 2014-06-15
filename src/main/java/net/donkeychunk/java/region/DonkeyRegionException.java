package net.donkeychunk.java.region;

/**
 *
 */
public class DonkeyRegionException extends RuntimeException {

    public DonkeyRegionException() {
    }

    public DonkeyRegionException(String message) {
        super(message);
    }

    public DonkeyRegionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DonkeyRegionException(Throwable cause) {
        super(cause);
    }

    public DonkeyRegionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

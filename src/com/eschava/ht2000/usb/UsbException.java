package com.eschava.ht2000.usb;

import org.usb4java.LibUsb;

/**
 * USB exception
 *
 * @author Eugene Schava
 */
public class UsbException extends Exception {
    private final int errorCode;

    public UsbException(final String message, final int errorCode)
    {
        super(String.format("USB error %d: %s: %s", -errorCode, message,
            LibUsb.strError(errorCode)));
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code.
     *
     * @return The error code
     */
    public int getErrorCode()
    {
        return this.errorCode;
    }

}

package com.eschava.ht2000.usb;

import org.usb4java.*;

import java.nio.ByteBuffer;

/**
 * HT-2000 USB connection
 *
 * @author Eugene Schava
 */
public class HT2000UsbConnection {
    public static final short USB_VENDOR_ID = 0x10c4;
    public static final short USB_PRODUCT_ID = (short) 0x82cd;
    public static final int   INTERFACE_NUMBER = 0;

    private static final int LOG_LEVEL = Integer.getInteger("LibUsb.LOG_LEVEL", LibUsb.LOG_LEVEL_NONE);

    private final static Context context;
    private final static UsbException contextException;
    static
    {
        context = new Context();
        int result = LibUsb.init(context);
        contextException = result != LibUsb.SUCCESS
                ? new UsbException("Unable to initialize libusb.", result)
                : null;
        LibUsb.setDebug(context, LOG_LEVEL);
    }

    private final Device device;
    private DeviceHandle handle;
    private boolean detach = false;

    public HT2000UsbConnection() throws Exception {
        if (contextException != null)
            throw contextException;

        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) throw new UsbException("Unable to get device list", result);

        try {
            device = findDevice(list);
            if (device == null)
                throw new Exception("HT2000 is not found among connected devices");
        } finally {
            LibUsb.freeDeviceList(list, false); // false to do not unref found device
        }
    }

    public static void shutdown() {
        if (contextException == null)
            LibUsb.exit(context);
    }

    private Device findDevice(DeviceList list) throws UsbException {
        // Iterate over all devices and scan for the right one
        for (Device device: list) {
            DeviceDescriptor descriptor = new DeviceDescriptor();
            int result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result != LibUsb.SUCCESS)
                throw new UsbException("Unable to read device descriptor", result);
            if (descriptor.idVendor() == USB_VENDOR_ID && descriptor.idProduct() == USB_PRODUCT_ID)
                return device;
        }
        return null;
    }

    public void open() throws UsbException {
        // open
        handle = new DeviceHandle();
        int result = LibUsb.open(device, handle);
        if (result != LibUsb.SUCCESS)
            throw new UsbException("Unable to open USB device", result);

        // detach from kernel driver
        // have to check LibUsb.hasCapability(LibUsb.CAP_SUPPORTS_DETACH_KERNEL_DRIVER) but it returns incorrect result for ARM
        detach = LibUsb.kernelDriverActive(handle, INTERFACE_NUMBER) == 1;
        if (detach)
        {
            result = LibUsb.detachKernelDriver(handle, INTERFACE_NUMBER);
            if (result != LibUsb.SUCCESS)
                throw new UsbException("Unable to detach kernel driver", result);
        }

        // claim
        result = LibUsb.claimInterface(handle, INTERFACE_NUMBER);
        if (result != LibUsb.SUCCESS)
            throw new UsbException("Unable to claim interface", result);
    }

    public void close() {
        // release
        int result = LibUsb.releaseInterface(handle, INTERFACE_NUMBER);
        if (result != LibUsb.SUCCESS)
//            throw new UsbException("Unable to release interface", result);
            new UsbException("Unable to release interface", result).printStackTrace();

        // re-attach kernel driver if needed
        if (detach)
        {
            result = LibUsb.attachKernelDriver(handle, INTERFACE_NUMBER);
            if (result != LibUsb.SUCCESS)
//                throw new UsbException("Unable to re-attach kernel driver", result);
                new UsbException("Unable to re-attach kernel driver", result).printStackTrace();
        }

        // close
        if (handle != null && handle.getPointer() != 0)
            LibUsb.close(handle);
        handle = null;
    }

    public HT2000State readState() throws UsbException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(32);
        int transfered = LibUsb.controlTransfer(
                handle,
                (byte) (LibUsb.ENDPOINT_IN |
                        LibUsb.REQUEST_TYPE_CLASS |
                        LibUsb.RECIPIENT_INTERFACE),
                (byte) 0x01,
                (short) 0x0105,
                (short) 0,
                buffer, 5000);
        if (transfered < 0)
            throw new UsbException("Control transfer failed", transfered);

        return new HT2000State(buffer);
    }
}

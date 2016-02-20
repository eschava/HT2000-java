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

    private final Context context;
    private final Device device;
    private DeviceHandle handle;

    public HT2000UsbConnection() throws Exception {

        context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS)
            throw new UsbException("Unable to initialize libusb.", result);

        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(null, list);
        if (result < 0) throw new UsbException("Unable to get device list", result);

        device = findDevice(list);
        if (device == null)
            throw new Exception("HT2000 is not found among connected devices");
    }

    private Device findDevice(DeviceList list) throws UsbException {
        try
        {
            // Iterate over all devices and scan for the right one
            for (Device device: list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                int result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS)
                    throw new UsbException("Unable to read device descriptor", result);
                if (descriptor.idVendor() == USB_VENDOR_ID && descriptor.idProduct() == USB_PRODUCT_ID)
                    return device;
            }
        }
        finally
        {
            LibUsb.freeDeviceList(list, false); // false to do not unref found device
        }
        return null;
    }

    public void open() throws UsbException {
        handle = new DeviceHandle();
        int result = LibUsb.open(device, handle);
        if (result != LibUsb.SUCCESS)
            throw new UsbException("Unable to open USB device", result);
    }

    public void close() {
        if (handle != null && handle.getPointer() != 0)
            LibUsb.close(handle);
        handle = null;
    }

    public void shutdown() {
        LibUsb.exit(context);
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

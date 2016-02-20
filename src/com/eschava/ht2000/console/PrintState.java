package com.eschava.ht2000.console;

import com.eschava.ht2000.usb.HT2000State;
import com.eschava.ht2000.usb.HT2000UsbConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Prints current state of monitor to console
 *
 * @author Eugene Schava
 */
public class PrintState {
    private static final DateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        HT2000UsbConnection usbConnection = new HT2000UsbConnection();
        try {
            usbConnection.open();

            HT2000State state = usbConnection.readState();

            System.out.println("Date/Time: " + DATE_TIME_FORMATTER.format(state.getTime()));
            System.out.println("Temperature: " + state.getTemperature());
            System.out.println("Humidity: " + state.getHumidity());
            System.out.println("CO2: " + state.getCo2());

        } finally {
            usbConnection.close();
            HT2000UsbConnection.shutdown();
        }
    }
}

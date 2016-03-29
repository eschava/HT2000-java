package com.eschava.ht2000.usb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

/**
 * Current state of HT-2000 device
 *
 * @author Eugene Schava
 */
public class HT2000State {
    private static final long TIMESTAMP_SHIFT = 0x77797DA0; // TODO: magic constant
    private static final int TEMPERATURE_SHIFT = 112; // TODO: magic constant

    private final Date time;
    private final double temperature;
    private final double humidity;
    private final int co2;

    public HT2000State(ByteBuffer buffer) {
        buffer.order(ByteOrder.BIG_ENDIAN); // to guarantee correct order

        long timestamp = /*Integer.*/toUnsignedLong(buffer.getInt(1));
        if (timestamp > TIMESTAMP_SHIFT)
            timestamp -= TIMESTAMP_SHIFT;
        time = new Date(timestamp * 1000);
        temperature = (TEMPERATURE_SHIFT + /*Byte.*/toUnsignedInt(buffer.get(8))) / 10.0;
        humidity = buffer.getShort(9) / 10.0;
        co2 = buffer.getShort(24);
    }

    private static long toUnsignedLong(int x) {
        return ((long) x) & 0xffffffffL;
    }

    private static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    public Date getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public int getCo2() {
        return co2;
    }

    @Override
    public String toString() {
        return "HT2000State{" +
                "Time=" + time +
                ", Temperature=" + temperature +
                ", Humidity=" + humidity +
                ", CO2=" + co2 +
                '}';
    }
}

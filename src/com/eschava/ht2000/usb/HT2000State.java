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
    private final Date time;
    private final double temperature;
    private final double humidity;
    private final int co2;

    public HT2000State(ByteBuffer buffer) {
        buffer.order(ByteOrder.BIG_ENDIAN); // to guarantee correct order

        time = new Date(buffer.getInt(1) * 1000L);
        temperature = 11.2 + (buffer.get(8) & 0xFF) / 10.0; // TODO: 11.2 magic constant
        humidity = buffer.getShort(9) / 10.0;
        co2 = buffer.getShort(24);
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

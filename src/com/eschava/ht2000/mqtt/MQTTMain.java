package com.eschava.ht2000.mqtt;

import com.eschava.ht2000.usb.HT2000State;
import com.eschava.ht2000.usb.HT2000UsbConnection;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * MQTT client
 *
 * @author Eugene Schava
 */
public class MqttMain extends Thread {
    private final HT2000UsbConnection usbConnection;
    private final MqttClient mqttClient;
    // settings from -D parameters
    private String mqttUrl = System.getProperty("mqttUrl", "tcp://localhost");
    private String mqttClientId = System.getProperty("mqttClientId", "ht-2000");
    private String mqttUser = System.getProperty("mqttUser");
    private String mqttPassword = System.getProperty("mqttPassword");
    private String temperatureTopic = System.getProperty("temperatureTopic", "/ht-2000/temperature");
    private String humidityTopic = System.getProperty("humidityTopic", "/ht-2000/humidity");
    private String co2Topic = System.getProperty("co2Topic", "/ht-2000/co2");
    private long interval = Long.getLong("interval", 60) * 1000;

    public MqttMain() throws Exception {
        usbConnection = new HT2000UsbConnection();
        usbConnection.open();

        mqttClient = new MqttClient(mqttUrl, mqttClientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        if (mqttUser != null && !mqttUser.isEmpty())
            mqttConnectOptions.setUserName(mqttUser);
        if (mqttPassword != null && !mqttPassword.isEmpty())
            mqttConnectOptions.setPassword(mqttPassword.toCharArray());
        mqttClient.connect();
    }

    private static byte[] getPayload(double v) {
        return String.valueOf(v).getBytes();
    }

    private static byte[] getPayload(int v) {
        return String.valueOf(v).getBytes();
    }

    public static void main(String[] args) throws Exception {
        new MqttMain().start();
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                HT2000State state = usbConnection.readState();

                if (temperatureTopic != null && !temperatureTopic.isEmpty())
                    mqttClient.publish(temperatureTopic, getPayload(state.getTemperature()), 1, false);

                if (humidityTopic != null && !humidityTopic.isEmpty())
                    mqttClient.publish(humidityTopic, getPayload(state.getHumidity()), 1, false);

                if (co2Topic != null && !co2Topic.isEmpty())
                    mqttClient.publish(co2Topic, getPayload(state.getCo2()), 1, false);

                Thread.sleep(interval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        usbConnection.close();
        HT2000UsbConnection.shutdown();
    }
}

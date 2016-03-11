#!/bin/sh

mqttUrl="tcp://localhost"
mqttClientId="ht-2000"
mqttUser=""
mqttPassword=""

temperatureTopic="/ht-2000/temperature"
humidityTopic="/ht-2000/humidity"
co2Topic="/ht-2000/co2"

interval=60

java -classpath "lib/*" \
	 -DmqttUrl=$mqttUrl \
	 -DmqttClientId=$mqttClientId \
	 -DmqttUser=$mqttUser \
	 -DmqttPassword=$mqttPassword \
	 -DtemperatureTopic=$temperatureTopic \
	 -DhumidityTopic=$humidityTopic \
	 -Dco2Topic=$co2Topic \
	 -Dinterval=$interval \
	 com.eschava.ht2000.mqtt.MqttMain
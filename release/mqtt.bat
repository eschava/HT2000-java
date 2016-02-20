@echo off

SET mqttUrl=tcp://localhost
SET mqttClientId=ht-2000
SET mqttUser=
SET mqttPassword=

SET temperatureTopic=/ht-2000/temperature
SET humidityTopic=/ht-2000/humidity
SET co2Topic=/ht-2000/co2

SET interval=5

java -classpath lib/* ^
	 -DmqttUrl=%mqttUrl% ^
	 -DmqttClientId=%mqttClientId% ^
	 -DmqttUser=%mqttUser% ^
	 -DmqttPassword=%mqttPassword% ^
	 -DtemperatureTopic=%temperatureTopic% ^
	 -DhumidityTopic=%humidityTopic% ^
	 -Dco2Topic=%co2Topic% ^
	 -Dinterval=%interval% ^
	 com.eschava.ht2000.mqtt.MqttMain
package com.mqttSubscribe.demo.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.mqttSubscribe.demo.config.MqttConfig;
import com.mqttSubscribe.demo.entity.*;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MqttSubService {
    private final MqttConfig mqttConfig;

    @Autowired
    public MqttSubService(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    @Async
    public void subscribeToTopic(String topic) {

        try {
            MqttClient mqttClient = mqttConfig.mqttClient();
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String jsonString = new String(message.getPayload());
                    System.out.println("Received message: topic(" + topic + ")" + jsonString);

                    try {
                        DemoData demoData = new DemoData();
                        demoData.setTopic(topic);
                        JsonFactory jf = new JsonFactory();
                        JsonParser jp = jf.createParser(jsonString);
                        String fieldName = null;
                        while (!jp.isClosed()) {
                            JsonToken token = jp.nextToken();
                            if (token == JsonToken.FIELD_NAME) {
                                fieldName = jp.getCurrentName();
                            } else if (token == JsonToken.VALUE_STRING) {
                                if ("ID".equals(fieldName)) {
                                    demoData.setID(jp.getValueAsString());
                                } else if ("UTC".equals(fieldName)) {
                                    demoData.setUTC(jp.getValueAsString());
                                } else if ("thing_name".equals(fieldName)) {
                                    demoData.setThing_name(jp.getValueAsString());
                                } else if ("Alt".equals(fieldName)) {
                                    demoData.setAlt(jp.getValueAsString());
                                } else if ("Lat".equals(fieldName)) {
                                    demoData.setLat(jp.getValueAsString());
                                } else if ("Lon".equals(fieldName)) {
                                    demoData.setLon(jp.getValueAsString());
                                } else if ("Fix".equals(fieldName)) {
                                    demoData.setFix(jp.getValueAsString());
                                } else if ("Sat".equals(fieldName)) {
                                    demoData.setSat(jp.getValueAsString());
                                }
                            }
                        }
                        System.out.println("topic: " + demoData.getTopic());
                        System.out.println("ID: " + demoData.getID());
                        System.out.println("UTC: " + demoData.getUTC());
                        System.out.println("thing_name: " + demoData.getThing_name());
                        System.out.println("Alt: " + demoData.getAlt());
                        System.out.println("Lat: " + demoData.getLat());
                        System.out.println("Lon: " + demoData.getLon());
                        System.out.println("Fix: " + demoData.getFix());
                        System.out.println("Sat: " + demoData.getSat());
                    }
                    catch (Exception e) {
                        System.out.println("Exception while parsing json data: " + e);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not needed for a subscriber
                }
            });

            // Subscribe to the topic
            mqttClient.subscribe(topic, 0);
            System.out.println("Subscribed to topic: " + topic);
        } catch (Exception e) {
            System.out.println("Exception while subscribing to topic: " + e.getMessage());
        }

    }

}



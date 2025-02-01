package com.mqttSubscribe.demo.controller;

import com.mqttSubscribe.demo.service.MqttSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttReceiverController {
    private final MqttSubService mqttSubService;

    @Autowired
    public MqttReceiverController(MqttSubService mqttSubService) {
        this.mqttSubService = mqttSubService;
    }

    // HTTP endpoint to trigger MQTT subscription
    @GetMapping("/subscribe")
    public String subscribeToTopic(@RequestParam("topic") String topic) {
        mqttSubService.subscribeToTopic("properties/**/+/****/+");
        return "Subscribed to topic: " + topic;
    }
}

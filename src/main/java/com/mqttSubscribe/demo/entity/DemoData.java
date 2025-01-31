package com.mqttSubscribe.demo.entity;

public class DemoData {
    private String topic;
    private String UTC;
    private String ID;
    private String thing_name;
    private String Alt;
    private String Lat;
    private String Lon;
    private String Fix;
    private String Sat;

    public DemoData() {}

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUTC() {
        return UTC;
    }

    public void setUTC(String UTC) {
        this.UTC = UTC;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getThing_name() {
        return thing_name;
    }

    public void setThing_name(String thing_name) {
        this.thing_name = thing_name;
    }

    public String getAlt() {
        return Alt;
    }

    public void setAlt(String alt) {
        Alt = alt;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLon() {
        return Lon;
    }

    public void setLon(String lon) {
        Lon = lon;
    }

    public String getFix() {
        return Fix;
    }

    public void setFix(String fix) {
        Fix = fix;
    }

    public String getSat() {
        return Sat;
    }

    public void setSat(String sat) {
        Sat = sat;
    }
}

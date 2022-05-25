package com.ask2784.fieldmanagement.databases;

public class FieldDetails {
    private String session,crop,water,implement;

    public FieldDetails() {
    }

    public FieldDetails(String session, String crop, String water, String implement) {
        this.session = session;
        this.crop = crop;
        this.water = water;
        this.implement = implement;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getImplement() {
        return implement;
    }

    public void setImplement(String implement) {
        this.implement = implement;
    }

    @Override
    public String toString() {
        return "FieldDetails{" +
                "session='" + session + '\'' +
                ", crop='" + crop + '\'' +
                ", water='" + water + '\'' +
                ", implement='" + implement + '\'' +
                '}';
    }
}

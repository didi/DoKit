package com.didichuxing.doraemonkit.model;

import java.io.Serializable;

/**
 * Created by wanglikun on 2019-07-19
 */
public class LatLng implements Serializable {
    public double latitude;

    public double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

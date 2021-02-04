package com.didichuxing.doraemonkit.kit.fly.manager;

public class GcjPointer extends GeoPointer {

    public GcjPointer(double latitude, double longitude) {
        super(latitude, longitude);
    }

    public WgsPointer toWgsPointer() {
        if (GeoPointer.outOfChina(this.mLatitude, this.mLongitude)) {
            return new WgsPointer(this.mLatitude, this.mLongitude);
        }
        double[] delta = GeoPointer.delta(this.mLatitude, this.mLongitude);
        return new WgsPointer(this.mLatitude - delta[0], this.mLongitude - delta[1]);
    }
}
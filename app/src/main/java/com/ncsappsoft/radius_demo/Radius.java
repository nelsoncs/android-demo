package com.ncsappsoft.radius_demo;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * <h3>
 * Linear method to calculate intersection of circle and center of circle to a touch.
 * <br>
 * No trig is better...
 * <br><br>
 * Notes:
 * </h3>
 * <h4>
 * -google maps api v2 for android.<br>
 * -draw() is accessory function draws simple circle on map.<br>
 * -intersection() performs linear algebraic transform to find intersection of touch point<br>
 *  vector and circle.<br>
 * -uses only ad, sub, mul, div.
 * </h4>
 * Created by nelsoncs on 4/7/15.
 */
public class Radius {

    private Context   mContext;
    private GoogleMap mMap;
    private LatLng    mCenter;

    // this should ALWAYS be meters, spinner and editText coordinate to display other units
    private double mRadius;
    private Circle mCircle;

    /**
     *
     * @param gMap
     * @param center
     */
    public Radius(Context context, GoogleMap gMap, LatLng center ) {

        if (context != null) {
            this.mContext = context;
        } else {
            System.out.println("Radius.constructor: View is null.");
        }

        if (gMap != null ) {
            this.mMap = gMap;
        } else {
            System.out.println("Radius.constructor: gMap is null.");
        }

        this.mCenter = center;
        this.mRadius = 0.0;
        this.mCircle = null;
    }

    /**
     * LatLng vector length in meters as calculated by google api,
     * alternative is haversine or cosine law calculation
     * @param center
     * @param radius
     * @return results length only, drop any bearings data
     */
    private static double lengthVec(LatLng center, LatLng radius) {
        //distanceBetween() requires float[] to allow optional bearings data
        float[] results = new float[1];

        Location.distanceBetween(center.latitude, center.longitude,
                                 radius.latitude, radius.longitude,
                                 results
                                );

        return results[0];
    }

    /**
     * translates vector from a pair of LatLng coordinates by vector addition
     * @param vecA
     * @param vecB
     * @return translated vector as single LatLng point
     */
    private static LatLng addVec (LatLng vecA, LatLng vecB) {

        return new LatLng(vecA.latitude + vecB.latitude,
                          vecA.longitude + vecB.longitude
                         );
    }

    /**
     * translates vector determined from a pair of LatLng coordinates to the origin by vector
     * subtraction
     * @param vecA
     * @param vecB
     * @return translated vector coordinates as single LatLng from the origin
     */
    private static LatLng subtractVec (LatLng vecA, LatLng vecB) {

        return new LatLng(vecA.latitude - vecB.latitude,
                          vecA.longitude - vecB.longitude
                         );
    }

    /**
     * calculates unit vector coordinate LatLng by scalar division. The input vector must already be
     * translated to origin. See {@link #subtractVec}
     * @param vec - input vector
     * @param length - in meters
     * @return unit vector
     */
    private static LatLng unitVec (LatLng vec, double length) {

        return new LatLng(vec.latitude / length, vec.longitude / length);
    }

    /**
     * scales LatLng as vector by scalar multiplication
     * @param vec - vector to be scaled
     * @param length - scalar value in meters
     * @return rescaled vector
     */
    private static LatLng scaleVec (LatLng vec, double length) {

        return new LatLng(vec.latitude * length, vec.longitude * length);
    }

    /**
     * calculates the intersection point of circle drawn at instance radius value with the user's
     * touch point on the map
     * @param touchPoint
     * @return intersection
     */
    public LatLng intersection (LatLng touchPoint) {
        LatLng intersection = null;

        // no circle then no calculation
        if (mCircle != null) {
            double len = 0.0;

            // distance between origin and touch point
            len = lengthVec(mCenter, touchPoint);

            // translate to (0,0), calculate unit vector coordinates
            LatLng translatedVec = subtractVec(touchPoint, mCenter);

            intersection = unitVec(translatedVec, len);

            if (mRadius != 0.0 ) {
                // multiply unit vector times circle radius
                intersection = scaleVec(intersection, mRadius);

                // translate back to circle center
                intersection = addVec(mCenter, intersection);
            }
            else {
                System.out.println("Radius.intersection - null mRadius.");
            }
        }
        else {
            System.out.println("Radius.intersection - null mCircle.");
        }

        return intersection;
    }

    /**
     *
     * @return instance radius value
     */
    public double getRadius() { return mRadius; }

    /**
     *
     * @param mRadius
     */
    public void setRadius(double mRadius) { this.mRadius = mRadius; }


    /**
     * draws a circle on map at given radius
     * @param radius - in meters
     */
    public void draw (double radius) {

        this.mRadius = radius;

        // System.out.println("mCircle: enter draw() " + mCircle);
        if (mCircle != null) {
            System.out.println("mCircle: !null condition " + mCircle);
            // wipe out the current radius circle before drawing new one
            mCircle.remove();
        }

        Resources res = this.mContext.getResources();

        mCircle = this.mMap.addCircle(new CircleOptions()
                        .center(mCenter)
                        .radius(mRadius)
                        .strokeWidth(5)
                        .strokeColor(res.getColor(R.color.dark_blue))
        );
        // System.out.println("mCircle: exit draw() " + mCircle);
    }

    /**
     * draws a circle at instance radius, mRadius
     */
    public void draw () { this.draw(mRadius); }
}

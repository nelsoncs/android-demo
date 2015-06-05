package com.ncsappsoft.radius_demo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Demonstrates a very simple  and efficient linear algebraic method of finding the intersection of
 * a ray (to a touch point) and a circle drawn on a map using the google maps api for android.
 */
public class RadiusDemoActivity extends FragmentActivity {

    // map
    private GoogleMap  mMap; // Might be null if Google Play services APK is not available.

    // circle on map
    private Location   mLocation;
    private LatLng     mOrigin;
    private Radius     mRadius;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();

        // set up listener for screen touches
        this.mMap.setOnMapClickListener(onMapClickListen);

        // draw a fixed radius circle on map
        Context context = this;
        this.mRadius = new Radius(context, mMap, mOrigin);
        this.mRadius.setRadius(1000);
        this.mRadius.draw();
    }

    @Override
    protected void onResume() {
        System.out.println("OnResume()");
        super.onResume();

        setUpMapIfNeeded();

        this.mMap.setOnMapClickListener(onMapClickListen);
    }

    @Override
    protected void onStart() {
        System.out.println("OnStart()");
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onPause() {
        System.out.println("OnPause()");
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop() {
        System.out.println("OnStop()");
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        System.out.println("OnDestroy()");
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        UiSettings mUiSettings;
        System.out.println("setUpMap.entry point");
        // controls are disabled by default, so enable them
        mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);

        mMap.setBuildingsEnabled(true);

        // Move map to current user's location.
        mMap.setMyLocationEnabled(true);
        setUpCurrentLocation();
    }

    private void setUpCurrentLocation() {

        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            if (locationManager != null) {
                String provider = locationManager.getBestProvider(criteria, true);
                mLocation = locationManager.getLastKnownLocation(provider);
                System.out.println("myLocation: " + mLocation.describeContents());
            }else
            {
                System.out.println("--------->locationManager null<------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        } finally {
        }

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if ( mLocation != null ) {
            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            mOrigin = new LatLng(latitude, longitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(mOrigin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        } else
        {
            System.out.println("--------->myLocation null<------");
        }
    }

    private GoogleMap.OnMapClickListener onMapClickListen = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

            System.out.println("mRadius.getRadius: " + mRadius.getRadius());
            // check for valid radius or return doing nothing
            if (mRadius.getRadius() != 0.0) {
                LatLng intersection;

                // translate touch vector to vector at radius
                intersection = mRadius.intersection(latLng);

                mMap.addCircle(new CircleOptions()
                                .center(intersection)
                                .radius(20)
                                .fillColor(0xffff0000)
                                .strokeWidth(1)
                );
            }
        }
    };
}


package com.example.vinoth.locationoperator.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationServiceGps extends Service {
    private static final int TWO_MINUTES = 1000 * 60 * 2;     //1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    private double Latitude=0;
    private double Longitude=0;
    private DatabaseReference databaseReference;
    private DatabaseReference locationRef;
    protected SharedPreferences sharedPreferences;
    protected String vehicle;
    private DatabaseReference vehicleDBRef;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "location Started", Toast.LENGTH_LONG).show();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        locationRef=databaseReference.child("Location");
        sharedPreferences=getSharedPreferences("mydata",Context.MODE_PRIVATE);
        vehicle=sharedPreferences.getString("vehicle","temp");
        vehicleDBRef=locationRef.child(vehicle);



    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          getlocation();
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service onStartCommand Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    public void getlocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, listener);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;
        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }
        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());
        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        Log.v("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(listener);
        super.onDestroy();
    }
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged( Location loc)
        {
            Toast.makeText( getApplicationContext(), "Location changed", Toast.LENGTH_SHORT ).show();
            if(isBetterLocation(loc, previousBestLocation)) {
                Latitude = loc.getLatitude();
                Longitude= loc.getLongitude();
                updateLocation(loc);
                String data = " Latitude:" + String.valueOf(Latitude) + " Longitude:" + String.valueOf(Longitude) + "provi:" + loc.getProvider()+"   speed "+loc.getSpeed();
                Log.i("vinoth",data);
            }
        }
        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }
        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    }

    private void updateLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        DatabaseReference current_DBRef= vehicleDBRef.child("current");
        current_DBRef.child("latitude").setValue(latitude);
        current_DBRef.child("longitude").setValue(longitude);
        current_DBRef.child("Time").setValue(getCurrentTimeStamp());
        DatabaseReference oldDB=vehicleDBRef.child(getCurrentTimeStamp());
        oldDB.child("latitude").setValue(latitude);
        oldDB.child("longitude").setValue(longitude);
        oldDB.child("Time").setValue(getCurrentTimeStamp());
    }
    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
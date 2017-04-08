package com.example.vinoth.locationoperator;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.vinoth.locationoperator.services.LocationServiceGps;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1010;
    private android.support.v7.widget.SwitchCompat locationSwitch;
    private DatabaseReference databaseReference;
    private PowerManager.WakeLock screenLock;
    private SharedPreferences sharedPreferences;
    private String vehicle;
    private TextView tvResult;
    private String TAG="vinothTag";

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference vehicleDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPreferences=getSharedPreferences("mydata",Context.MODE_PRIVATE);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                String text = "Please allow the application to access the LOCATION permission";

                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                        .setDuration(Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        });
                snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        tvResult= (TextView) findViewById(R.id.tvResult);
        locationSwitch=(android.support.v7.widget.SwitchCompat)findViewById(R.id.swLocation);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        final Intent intentService = new Intent(getApplication(), LocationServiceGps.class);
        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
        screenLock =  mgr.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    startService(intentService);
                }else{
                    stopService(intentService);
                }
            }
        });




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                locationSwitch.setChecked(false);
                startActivity(new Intent(this,SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume:");


    }

    @Override
    protected void onStart() {
        super.onStart();
        locationSwitch.setChecked(true);
        vehicle=sharedPreferences.getString("vehicle","temp");
        vehicleDBRef=databaseReference.child("Location").child(vehicle);
        vehicleDBRef.child("current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String timeData = dataSnapshot.child("timeStamp").getValue(String.class);
                tvResult.setText(vehicle + " is updated at " + timeData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e(TAG, "onStart: ");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    public void deleteData(View view) {
        vehicleDBRef.removeValue();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==MY_PERMISSIONS_REQUEST_LOCATION){
            if (resultCode==RESULT_OK){
                Intent intentService = new Intent(getApplication(), LocationServiceGps.class);
                startService(intentService);

            }
            else
            {
                String text = "Please allow the application to access the LOCATION permission";
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                        .setDuration(Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        });
                snackbar.show();
            }


        }
    }
}

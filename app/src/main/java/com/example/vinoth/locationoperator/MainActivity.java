package com.example.vinoth.locationoperator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    private android.support.v7.widget.SwitchCompat locationSwitch;
    private DatabaseReference databaseReference;
    private PowerManager.WakeLock screenLock;
    private SharedPreferences sharedPreferences;
    private String vehicle;
    private TextView tvResult;
    private String TAG="vinothTag";

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPreferences=getSharedPreferences("mydata",Context.MODE_PRIVATE);
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
        ValueEventListener n = databaseReference.child("Location").child(vehicle).child("current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String timeData = dataSnapshot.child("Time").getValue(String.class);
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
}

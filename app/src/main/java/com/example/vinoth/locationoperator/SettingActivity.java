package com.example.vinoth.locationoperator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingActivity extends AppCompatActivity {
    private ListView vehicleListView;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedpreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedpreference=getSharedPreferences("mydata", Context.MODE_PRIVATE);
        vehicleListView=(ListView) findViewById(R.id.veclist);
        databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://locationtracker-586c2.firebaseio.com/vehicle");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseListAdapter<String> FireBaseList=new FirebaseListAdapter<String>(this,
                String.class,
                android.R.layout.simple_list_item_1,
                databaseReference){
            @Override
            protected void populateView(View v, String model, int position) {
                TextView tv=(TextView)v.findViewById(android.R.id.text1);
                tv.setText(model);
            }
        };
        vehicleListView.setAdapter(FireBaseList);

        vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data =(String) parent.getItemAtPosition(position);
                Toast.makeText(getApplication(), data+"  is selected ", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor prefsEditor = sharedpreference.edit();
                prefsEditor.putString("vehicle",data);
                prefsEditor.apply();
            }
        });



    }
}

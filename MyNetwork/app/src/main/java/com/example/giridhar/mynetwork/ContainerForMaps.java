package com.example.giridhar.mynetwork;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContainerForMaps extends AppCompatActivity implements View.OnClickListener,MapsFragment.activityCommunicator{
Button btsetCity,btcancel;
    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_for_maps);
        btsetCity=(Button)findViewById(R.id.button3);
        btsetCity.setOnClickListener(this);
        btcancel=(Button)findViewById(R.id.cancelbutton);
        btcancel.setOnClickListener(this);
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        MapsFragment mapObj= new MapsFragment();
        fragmentTransaction.replace(R.id.fragholderForMaps,mapObj);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button3:
                  Intent i = getIntent();
                  i.putExtra("latitude",latitude);
                  i.putExtra("longitude",longitude);
                  setResult(RESULT_OK,i);
                  finish();
                  break;
            case R.id.cancelbutton:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }


    @Override
    public void sendBackValues(double lat, double lon) {
        latitude=lat;
        longitude=lon;
    }
}

package com.example.giridhar.mynetwork;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener{

MapView viewOfMap;
    View v;
    GoogleMap gmap;
    Marker marker;
    public MapsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_maps, container, false);
        viewOfMap=(MapView)v.findViewById(R.id.mapView);
        viewOfMap.onCreate(savedInstanceState);
        viewOfMap.onResume();
        viewOfMap.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
     activityCommunicator objValues=(activityCommunicator)getActivity();
     objValues.sendBackValues(latLng.latitude,latLng.longitude);
     placeMarker(latLng.latitude,latLng.longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gmap=googleMap;
        gmap.setOnMapClickListener(this);
    }
    private void placeMarker(double latitude, double longitude)
    {
        if(marker!=null)
        {
            marker.remove();
        }
        marker = gmap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude)).title("You selected this place").draggable(true).visible(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude,longitude)).zoom(10).build();
        gmap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }
    public interface activityCommunicator
    {
        void sendBackValues(double lat,double lon);
    }
}

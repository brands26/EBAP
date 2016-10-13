package com.beliautopart.beliautopart.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.adapter.CartAdapter;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.ItemProduk;
import com.beliautopart.beliautopart.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon on 19/05/16.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private int i = 1;
    private SessionManager session;
    private View v;
    private boolean setuju=false;
    private AlertDialog alertDialog;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GPSTracker gps;
    private Marker marker;
    private LatLng markerLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.map_fragment, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(0, 0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void setMapLocation(double lat,double lng){
        markerLocation = new LatLng(lat, lng);
        marker = mMap.addMarker(new MarkerOptions().position(markerLocation).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Dragging");
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                markerLocation = marker.getPosition();
            }

            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Started");

            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLocation,15));
    }
    public LatLng getMapLocation(){
        return markerLocation;
    }
}

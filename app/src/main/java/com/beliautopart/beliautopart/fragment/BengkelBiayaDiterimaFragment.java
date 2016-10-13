package com.beliautopart.beliautopart.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.BengkelActivity;
import com.beliautopart.beliautopart.activity.ChatActivity;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.webservices.BengkelService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelBiayaDiterimaFragment extends Fragment implements OnMapReadyCallback {
    private View v;
    private Context context;
    private SessionManager session;
    private BengkelService bengkel;
    private String content;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private RelativeLayout btnChat;
    private String namaBengkel;
    private RelativeLayout btnDatang;
    private TextView txtTitle;
    private TextView txtStatus;
    private LinearLayout linearLayout;
    private GoogleMap mMap;
    private Marker marker;
    private SupportMapFragment mapFragment;
    private LatLngBounds.Builder builder;
    private int jenis;
    private Marker markerBengkel;
    private LatLngBounds bounds;
    private RelativeLayout mapLayout;
    private CardView mapView;
    private boolean salon=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        salon = ((BengkelActivity)getActivity()).getSalon();

        v = inflater.inflate(R.layout.fragment_bengkel_biaya_diterima, container, false);
        context = getContext();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);

        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel2);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);

        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtJobID.setText(session.getBengkelId());
        content = getArguments().getString("content");
        txtNamaBengkel  = (TextView) v.findViewById(R.id.txtNamaBengkel);
        linearLayout  = (LinearLayout) v.findViewById(R.id.linearLayout);
        mapView  = (CardView) v.findViewById(R.id.card_view_relativeLayout43);
        txtJarak  = (TextView) v.findViewById(R.id.txtJarak);

        mapLayout  = (RelativeLayout) v.findViewById(R.id.relativeLayout43);

        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(salon){
            TextView textView68 = (TextView) v.findViewById(R.id.textView68);
            textView68.setText("Nama Salon:");
            TextView textView118 = (TextView) v.findViewById(R.id.textView118);
            textView118.setText("Lokasi Salon Saat Ini:");
            TextView textView73 = (TextView) v.findViewById(R.id.textView73);
            textView73.setText("Anda sudah meng-approve salon dan pembayaran Biaya Panggil Salon telah kami terima. \nHarap tunggu sementara salon kami menuju lokasi Anda. \nJika salon telah sampai di lokasi Anda, Klik tombol Konfirmasi Kedatangan.");
        }
        btnDatang = (RelativeLayout) v.findViewById(R.id.relativeLayout44);
        btnDatang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bengkel.setDatang(session.getBengkelId(),session.getBengkelUserId(),session.getUserId(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
            }
        });
        btnChat = (RelativeLayout) v.findViewById(R.id.lbtnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("nama",namaBengkel);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        txtStatus = (TextView) v.findViewById(R.id.textView73);
        setUp();
        return v;
    }
    public void setUp(){
        try {
            JSONObject data = new JSONObject(content);
            namaBengkel = data.getString("nama");
            txtNamaBengkel.setText(namaBengkel);
            double jarak = data.getDouble("jarak");
            txtJarak.setText(String.format("%.2f", (double)jarak)+" km");
            Log.d("loasino",""+content);
            if(data.getString("jobcode").equals("4")){
                mapView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                txtTitle.setText("Bengkel Telah Sampai di Lokasi Anda\n");
                txtStatus.setText("Bengkel telah sampai di lokasi Anda, silakan Anda meminta penjelasan mengenai kerusakan pada kendaraan Anda dan bernegosiasi mengenai biaya perbaikan kendaraan Anda. Biaya tersebut akan diinput ke dalam sistem oleh Bengkel.");
            }
            if(data.getString("jobcode").equals("104")){
                mapView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                txtTitle.setText("Salon Telah Sampai di Lokasi Anda\n");
                txtStatus.setText("Salon telah sampai di lokasi Anda, silakan Anda meminta penjelasan mengenai pelayanan pada kendaraan Anda dan bernegosiasi mengenai biaya pelayanan kendaraan Anda. Biaya tersebut akan diinput ke dalam sistem oleh Salon.");
            }

            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    public void setLocation(){
        JSONObject data = null;
        try {
            data = new JSONObject(content);
            LatLng lokasi  = new LatLng(data.getDouble("gps_lat"),data.getDouble("gps_long"));
            LatLng lokasiSekarang = new LatLng(session.getUserlat(), session.getUserlng());

            marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
            builder = new LatLngBounds.Builder();
            builder.include(lokasiSekarang);
            if(data.getString("jnsbengkel").equals("2")){
                jenis = data.getInt("jnsbengkel");
                markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasi).title(data.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));

            }
            else if(data.getString("jnsbengkel").equals("1")){
                jenis = data.getInt("jnsbengkel");
                markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasi).title(data.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));

            }
            builder.include(lokasi);

            bounds = builder.build();
            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.12);

            if(mapView.getVisibility()!=View.GONE)
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {

                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                        ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
                    }
                });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       setLocation();
    }
    public void setBengkelPosition(double lat, double lng){
        if(mMap!=null){
            mMap.clear();
            LatLng lokasi  = new LatLng(lat,lng);
            LatLng lokasiSekarang = new LatLng(session.getUserlat(), session.getUserlng());

            marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
            builder = new LatLngBounds.Builder();
            builder.include(lokasiSekarang);
            if(jenis==2){
                markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasi).title("Bengkel").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));

            }
            else if(jenis==1){
                markerBengkel = mMap.addMarker(new MarkerOptions().position(lokasi).title("Bengkel").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));

            }
            builder.include(lokasi);

            bounds = builder.build();
            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,50));

        }
    }
}

package com.beliautopart.beliautopart.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.BengkelActivity;
import com.beliautopart.beliautopart.activity.ChatActivity;
import com.beliautopart.beliautopart.helper.Logic;
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

import java.util.ArrayList;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelResponseFragment extends Fragment implements OnMapReadyCallback {
    private View v;
    private Context context;
    private SessionManager session;
    private SupportMapFragment mapFragment;
    private String content;
    private GoogleMap mMap;
    private Marker marker;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private RelativeLayout btnBayar;
    private BengkelService bengkel;
    private RelativeLayout btnBatal;
    private RelativeLayout btnChat;
    private String namaBengkel;
    private Dialog dialogError;
    private TextView txtNominalPembayaran;
    private Logic logic;
    private TextView tv;
    private boolean salon = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        salon = ((BengkelActivity)getActivity()).getSalon();
        v = inflater.inflate(R.layout.fragment_bengkel_respon, container, false);
        context = getContext();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);
        logic = new Logic();
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel1);
        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);

        txtJobID.setText(session.getBengkelId());
        content = getArguments().getString("content");
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtNamaBengkel  = (TextView) v.findViewById(R.id.txtNamaBengkel);
        txtJarak  = (TextView) v.findViewById(R.id.txtJarak);
        txtNominalPembayaran  = (TextView) v.findViewById(R.id.txtNominalPembayaran);
        btnBayar = (RelativeLayout) v.findViewById(R.id.lbtnBayar);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BengkelActivity)getActivity()).startOnLoadingAnimation();
                bengkel.setReadyBayar(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(), new SendDataHelper.VolleyCallback(){
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
        btnBatal = (RelativeLayout) v.findViewById(R.id.lbtnSimpan);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneAlert();
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
        return v;
    }
    public void setUp(){
        try {
            JSONObject data = new JSONObject(content);
            namaBengkel = data.getString("nama");
            txtNamaBengkel.setText(data.getString("nama"));
            session.setBengkelUserId(data.getString("uid"));
            Double jarak = data.getDouble("jarak");
            txtJarak.setText(String.format("%.2f", (double)jarak)+" km");
            txtNominalPembayaran.setText("Rp"+ logic.thousand(data.getString("biaya_panggil"))+".");
            LatLng lokasiSekarang = new LatLng(data.getDouble("lat"),data.getDouble("lng"));
            marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(lokasiSekarang);
            LatLng lokasi = new LatLng(data.getDouble("gps_lat"), data.getDouble("gps_long"));
            if(data.getString("jnsbengkel").equals("2")){
                tv.setText("Respon Bengkel Mobil Terdekat");
                marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(data.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));

            }
            else if(data.getString("jnsbengkel").equals("1")){
                tv.setText("Respon Bengkel Motor Terdekat");
                marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(data.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));

            }
            if(salon){
                TextView textView68  = (TextView) v.findViewById(R.id.textView68);
                textView68.setText("Nama Salon:");
                TextView textView73  = (TextView) v.findViewById(R.id.textView73);
                textView73.setText("Request Anda mendapat respon dari salon kami.");
                TextView textView74  = (TextView) v.findViewById(R.id.textView74);
                textView74.setText("Biaya Pemanggilan Salon:");
                TextView textView75  = (TextView) v.findViewById(R.id.textView75);
                textView75.setText("Jika Anda menyetujui bahwa Anda akan dibantu oleh salon tersebut, bayarlah biaya panggil bengkel sebagai berikut:");
                TextView textView77  = (TextView) v.findViewById(R.id.textView77);
                textView77.setText("Jika Anda ingin melakukan tanya jawab dengan salon yang merespon request Anda, tekan tombol CHAT." +
                        "                        \nJika Anda ingin membatalkan request, tekan tombol BATAL." +
                        "                        \nManfaatkan fitur chat untuk mengirimkan foto keadaan kendaraan anda.");
                tv.setText("Respon Salon Terdekat");
            }
                builder.include(lokasi);

            final LatLngBounds bounds = builder.build();
            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
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
    public void showDoneAlert(){
        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);

        TextView txtMessage = (TextView) dialogError.findViewById(R.id.txtMessage);
        txtMessage.setText("Anda yakin ingin membatalkan sesi ini?");
        Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        btnCobaLagi.setText("Batalkan");
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);
        btnBatal.setText("tidak");

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bengkel.setPembatalanJob(session.getBengkelId(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        dialogError.dismiss();
                        getActivity().finish();
                        Toast.makeText(context,"Telah dibatalkan",Toast.LENGTH_SHORT).show();
                        session.setBengkelAktif(false);
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });
        if (dialogError != null && !dialogError.isShowing())
            dialogError.show();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUp();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
}

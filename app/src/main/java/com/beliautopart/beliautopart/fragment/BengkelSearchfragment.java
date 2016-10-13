package com.beliautopart.beliautopart.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.BengkelActivity;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.RefModel;
import com.beliautopart.beliautopart.utils.GPSTracker;
import com.beliautopart.beliautopart.webservices.BengkelService;
import com.beliautopart.beliautopart.webservices.PartsService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelSearchfragment extends Fragment implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 2;
    private View v;
    private Activity context;
    private SessionManager session;
    private String content;
    private RelativeLayout layoutLoading;
    private RelativeLayout imgLoading;
    private double currentLatitude;
    private double currentLongitude;
    private GoogleMap mMap;
    private Marker marker;
    private SupportMapFragment mapFragment;
    private BengkelService bengkelService;
    private Dialog dialogError;
    private List<RefModel> tahun = new ArrayList<>();
    private List<RefModel> merk= new ArrayList<>();
    private List<RefModel> tipe= new ArrayList<>();
    private String tahunKendaraan="0";
    private String tipeKendaraan="0";
    private String merkKendaraan="0";
    private PartsService partsService;
    private Location currentLocation;
    private GPSTracker gps;
    private boolean isActive;
    private Dialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private ProgressDialog pDialog;
    private String[] tahunArray;
    private String kat="1";
    private Boolean salon=false;
    private RelativeLayout card_view_progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments()!=null){
            kat = getArguments().getString("kat");
            salon = ((BengkelActivity)getActivity()).getSalon();
        }
        else{
            kat ="1";
            salon = ((BengkelActivity)getActivity()).getSalon();
        }

        v = inflater.inflate(R.layout.fragment_bengkel_search, container, false);
        context =(Activity) getContext();
        session = new SessionManager(context);
        bengkelService = new BengkelService(context);
        partsService = new PartsService(context);
        card_view_progress = (RelativeLayout) getActivity().findViewById(R.id.relativeLayout12);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        RelativeLayout btnMobil = (RelativeLayout) v.findViewById(R.id.lbtnMobil);
        btnMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnDialogShow(kat);
            }
        });
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialog = alertDialogBuilder.create();
        alertDialogBuilder.setTitle("Peringatan GPS");
        alertDialogBuilder.setMessage("GPS tidak aktif. Mengaktifkan sekarang?");
        alertDialogBuilder.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                context.finish();
                dialog.cancel();
            }
        });
        //getLocation();

        return v;
    }
    public void setAllMarker(double lat,double lng,String Array){
        try {
            JSONArray array = new JSONArray(Array);
            if(mMap!=null){
                LatLng lokasiSekarang = new LatLng(lat,lng);
                marker = mMap.addMarker(new MarkerOptions().position(lokasiSekarang).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(lokasiSekarang);
                ArrayList<LatLng> locations = new ArrayList();
                for(int a =0; a<array.length();a++){
                    JSONObject object = array.getJSONObject(a);
                    LatLng lokasi = new LatLng(object.getDouble("gps_lat"), object.getDouble("gps_long"));
                    if(salon)
                        marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(object.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));
                    else if(object.getString("jnsbengkel").equals("1") && kat.equals("1"))
                        marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(object.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_cycles)));
                    else if(object.getString("jnsbengkel").equals("2") && kat.equals("2"))
                        marker = mMap.addMarker(new MarkerOptions().position(lokasi).title(object.getString("nama")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));

                    locations.add(lokasi);
                }
                for(int a=0;a<locations.size();a++)
                    builder.include(locations.get(a));

                final LatLngBounds bounds = builder.build();
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {

                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                        ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
                    }
                });
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-6.2295712, 106.7594778);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
        if ( Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }
        else{
            if (ActivityCompat.shouldShowRequestPermissionRationale( context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        mMap.setMyLocationEnabled(true);
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange (Location location) {
                if(currentLatitude==0 && currentLongitude==0){

                    LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                    if(marker!=null)
                        mMap.clear();
                    marker = mMap.addMarker(new MarkerOptions().position(loc).title("Lokasi saat ini").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_self)));
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    bengkelService.getBengkel("3", currentLatitude, currentLongitude, "10", new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (!obj.getBoolean("error")) {

                                    if (pDialog != null && pDialog.isShowing())
                                        pDialog.dismiss();
                                    content = obj.getString("content");
                                    setAllMarker(currentLatitude,currentLongitude,content);
                                }
                            } catch (JSONException e) {
                            }
                            return null;
                        }
                        @Override
                        public String onError(VolleyError result) {
                            return null;
                        }
                    });

                }

            }
        };
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }
    public void setOnDialogShow(final String jenis){
        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_input_kerusakan);
        dialogError.setCancelable(false);

        if(salon){
            TextView textJudulDialoog = (TextView) dialogError.findViewById(R.id.textView9);
            textJudulDialoog.setText("Input Deskripsi Kendaraan");
            TextView textDeskripsi = (TextView) dialogError.findViewById(R.id.textView92);
            textDeskripsi.setText("Deskripsi");
        }
        Button btnKirim = (Button) dialogError.findViewById(R.id.btnKirim);
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);

        TextView txtMerk = (TextView) dialogError.findViewById(R.id.txtMerk);
        TextView txtTipe = (TextView) dialogError.findViewById(R.id.txtTipe);


        if(jenis.equals("1")){
            txtMerk.setText("Merk Motor");
            txtTipe.setText("Tipe Motor");
        }
        else{
            txtMerk.setText("Merk Mobil");
            txtTipe.setText("Tipe Mobil");
        }



        final EditText inputNomorPolisi = (EditText) dialogError.findViewById(R.id.inputNomorPolisi);
        final EditText inputKerusakan = (EditText) dialogError.findViewById(R.id.inputKerusakan);
        final Spinner spTahun = (Spinner) dialogError.findViewById(R.id.spTahun);
        Spinner spMerk = (Spinner) dialogError.findViewById(R.id.spMerk);
        final Spinner spTipe = (Spinner) dialogError.findViewById(R.id.spTipe);
        tahunArray = getResources().getStringArray(R.array.tahun);
        final ArrayAdapter spinnerArrayAdapter5 = new ArrayAdapter(context,
                R.layout.spinner_item, tahunArray);
        spinnerArrayAdapter5.setDropDownViewResource( R.layout.spinner_item );
        spTahun.setAdapter(spinnerArrayAdapter5);

        final ArrayAdapter<RefModel> spinnerArrayAdapter2 = new ArrayAdapter<RefModel>(context,
                R.layout.spinner_item, merk);
        spinnerArrayAdapter2.setDropDownViewResource( R.layout.spinner_item );
        spMerk.setAdapter(spinnerArrayAdapter2);

        final ArrayAdapter<RefModel> spinnerArrayAdapter3 = new ArrayAdapter<RefModel>(context,
                R.layout.spinner_item, tipe);
        spinnerArrayAdapter3.setDropDownViewResource( R.layout.spinner_item );
        spTipe.setAdapter(spinnerArrayAdapter3);


        spTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tahunKendaraan = spTahun.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spMerk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                RefModel model = merk.get(position);
                if(model.getId()!=null){
                    merkKendaraan = model.getId();
                }
                else
                    merkKendaraan = "0";
                partsService.getTipeSearch(merkKendaraan, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject resultData = new JSONObject(result);
                            boolean error = resultData.getBoolean("error");
                            tipe.clear();
                            RefModel refModel = new RefModel();
                            refModel.setNama("Semua Tipe");
                            tipe.add(refModel);
                            if (!error) {
                                JSONArray dataArray = resultData.getJSONArray("content");
                                int sizeDataArray = dataArray.length();
                                for(int a=0;a<sizeDataArray;a++){
                                    JSONObject data = dataArray.getJSONObject(a);
                                    refModel = new RefModel(data.getString("id"), data.getString("nama"));
                                    tipe.add(refModel);
                                }

                            }
                            spinnerArrayAdapter3.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spTipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    RefModel model = tipe.get(position);
                    tipeKendaraan = model.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Nopol = inputNomorPolisi.getText().toString().trim();
                String kerusakan = inputKerusakan.getText().toString().trim();

                // if(tahunKendaraan.equals("Semua Tahun")) tahunKendaraan="0";
                if(tipeKendaraan==null)
                    tipeKendaraan="0";
                if(Nopol.equals("") || kerusakan.equals("") || tahunKendaraan.equals("0") || tipeKendaraan.equals("0") || merkKendaraan.equals("0") )
                    Toast.makeText(context,"Pastikan semua data terisi",Toast.LENGTH_SHORT).show();
                else{
                    setOnButtonClick(jenis,Nopol,kerusakan);
                    dialogError.dismiss();
                }
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });
        partsService.getSearchMenu(jenis,1, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (!object.getBoolean("error")) {
                                JSONObject object1 = new JSONObject(object.getString("content"));
                                JSONArray arrMerk = new JSONArray(object1.getString("merk"));
                                merk.clear();
                                RefModel refModel = new RefModel();
                                refModel.setNama("Semua Jenis");
                                refModel.setNama("Semua Tipe");
                                tipe.add(refModel);
                                merk.add(refModel);
                                for (int a = 0; a < arrMerk.length(); a++) {
                                    JSONObject data = arrMerk.getJSONObject(a);
                                    refModel = new RefModel(data.getString("id"), data.getString("nama"));
                                    merk.add(refModel);
                                }
                                spinnerArrayAdapter2.notifyDataSetChanged();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (dialogError != null && !dialogError.isShowing())
                        dialogError.show();
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });


    }
    public void setOnButtonClick(String jenis,String nopol,String kerusakan){
        ((BengkelActivity)getActivity()).startOnLoadingAnimation();
        ((BengkelActivity)getActivity()).startGetStatusJobOrder();
        if(salon){
            bengkelService.setJobBengkel("101", currentLatitude, currentLongitude, session.getUserId(), jenis, nopol, kerusakan, tahunKendaraan, tipeKendaraan,merkKendaraan, new SendDataHelper.VolleyCallback() {
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
        else
            bengkelService.setJobBengkel("1", currentLatitude, currentLongitude, session.getUserId(), jenis, nopol, kerusakan, tahunKendaraan, tipeKendaraan,merkKendaraan, new SendDataHelper.VolleyCallback() {
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

    public void getLocation(){
        gps = new GPSTracker(context);
        if (gps.canGetLocation()) {
            FindLocation();
        } else {
            if(isActive && !alertDialog.isShowing())
                alertDialog = alertDialogBuilder.show();
            alertDialog.show();
        }
    }
    public void showDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_alert_order_aktif);
        TextView txtDetail = (TextView) dialog.findViewById(R.id.textView27);
        txtDetail.setText("mohon maaf kami mengalami kesulitan mendeteksi lokasi Anda. Silakan coba lagi");
        dialog.setCancelable(true);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void FindLocation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentLocation==null)
                    showDialog();
            }
        }, 180000);
        Log.d("handler","proses hadler 6000");
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Mendapatkan koordinat saat ini");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));

        pDialog.show();
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("lokasi",""+location);
                updateLocation(location);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


    }


    void updateLocation(Location location) {
        if(currentLocation==null){
            currentLocation = location;
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();

            bengkelService.getBengkel("3", currentLatitude, currentLongitude, "10", new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (!obj.getBoolean("error")) {

                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                            content = obj.getString("content");
                            setAllMarker(currentLatitude,currentLongitude,content);
                        }
                    } catch (JSONException e) {
                    }
                    return null;
                }
                @Override
                public String onError(VolleyError result) {
                    return null;
                }
            });

        }
        else{
            currentLocation = location;
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();
        }

    }


    /**
     * Get provider name.
     * @return Name of best suiting provider.
     * */
    String getProviderName() {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }
}

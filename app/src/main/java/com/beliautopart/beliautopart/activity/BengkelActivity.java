package com.beliautopart.beliautopart.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.app.AppController;
import com.beliautopart.beliautopart.fragment.BengkelBayarJasaFragment;
import com.beliautopart.beliautopart.fragment.BengkelBayarPanggilFragment;
import com.beliautopart.beliautopart.fragment.BengkelBayarSelesaiFragment;
import com.beliautopart.beliautopart.fragment.BengkelBiayaDiterimaFragment;
import com.beliautopart.beliautopart.fragment.BengkelGetResponFragment;
import com.beliautopart.beliautopart.fragment.BengkelResponseFragment;
import com.beliautopart.beliautopart.fragment.BengkelSearchfragment;
import com.beliautopart.beliautopart.fragment.BengkelVerifikasiBayarJasaFragment;
import com.beliautopart.beliautopart.fragment.BengkelVerifikasiBayarPanggilanFragment;
import com.beliautopart.beliautopart.helper.ProgressBarAnimation;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.MessageModel;
import com.beliautopart.beliautopart.utils.GPSTracker;
import com.beliautopart.beliautopart.webservices.BengkelService;
import com.beliautopart.beliautopart.webservices.OrderService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;

public class BengkelActivity extends AppCompatActivity {

    private BengkelService bengkelService;
    private OrderService order;
    private Handler handler;
    private SessionManager session;
    private Activity context;
    private String status="";
    private BengkelGetResponFragment bengkelGetResponFragment;
    private RelativeLayout layoutLoading;
    private RelativeLayout imgLoading;
    private Animation rotation;
    private GPSTracker gps;
    private Location currentLocation;
    private double currentLatitude;
    private double currentLongitude;
    private BengkelSearchfragment bengkelSearchFragment;
    private BengkelResponseFragment bengkelResponseFragment;
    private BengkelBayarPanggilFragment bengkelBayarPanggilFragment;
    private BengkelVerifikasiBayarPanggilanFragment bengkelVerifikasiBayarPanggilanFragment;
    private BengkelBiayaDiterimaFragment bengkelBiayaDiterimaFragment;
    private BengkelBayarJasaFragment bengkelBayarJasaFragment;
    private BengkelVerifikasiBayarJasaFragment bengkelVerifikasiBayarJasaFragment;
    private BengkelBayarSelesaiFragment bengkelBayarSelesaiFragment;
    private Toolbar toolbar;
    private ImageButton btnback;
    private TextView txtTitle;
    private Dialog dialogStatus;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;
    private Animation rotationA;
    private Animation rotationC;
    private AlertDialog alertDialog;
    private boolean isActive = false;
    private AlertDialog.Builder alertDialogBuilder;
    private Dialog dialogError;
    private TextView dialogStatusText;
    private String kat;
    private MediaPlayer notif;
    private Dialog dialogChat;
    private String namaBengkel;
    private RelativeLayout card_view_progress;
    private ProgressBar progressBar2;
    private TextView progressText;
    private int pro=0;
    private boolean salon=false;
    private ProgressBarAnimation anim;
    private FirebaseDatabase mDatabase;
    private Query myRef;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bengkel);
        bengkelService = new BengkelService(this);
        session = new SessionManager(this);
        order = new OrderService(this);
        handler = new Handler();
        context = (Activity) this;


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                kat = "1";
                salon = false;
            } else {
                kat = extras.getString("kat");
                salon = extras.getBoolean("salon");
            }
        } else {
            kat = (String) savedInstanceState.getSerializable("page");
        }
        Log.d("salon",""+salon);

        dialogStatus = new Dialog(context);
        dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStatus.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogStatus.setContentView(R.layout.dialog_status_berubah);
        dialogStatusText = (TextView) dialogStatus.findViewById(R.id.textView27);
        dialogStatus.setCancelable(true);
        Button btnOk = (Button) dialogStatus.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogStatus.dismiss();
            }
        });

        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert_order_aktif);
        dialogError.setCancelable(true);

        TextView txtStatus = (TextView) dialogError.findViewById(R.id.textView27);
        txtStatus.setText("fitur sedang dalam pengembangan");
        Button dialogErrorbtnOk = (Button) dialogError.findViewById(R.id.btnOk);
        dialogErrorbtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogError.dismiss();
            }
        });




        Button dialogStatusbtnOk = (Button) dialogStatus.findViewById(R.id.btnOk);
        dialogStatusbtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogStatus.dismiss();
            }
        });

        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        //imgLoading = (RelativeLayout) findViewById(R.id.imgLoading);
        dialogChat = new Dialog(context);
        dialogChat.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChat.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogChat.setContentView(R.layout.dialog_alert);
        dialogChat.setCancelable(false);
        TextView txtMessage = (TextView) dialogChat.findViewById(R.id.txtMessage);
        if(salon)
            txtMessage.setText("Anda mendapatkan pesan dari Salon, baca sekarang?");
        else
            txtMessage.setText("Anda mendapatkan pesan dari Bengkel, baca sekarang?");
        Button btnCobaLagi = (Button) dialogChat.findViewById(R.id.btnCobaLagi);
        btnCobaLagi.setText("Baca");
        Button btnBatal = (Button) dialogChat.findViewById(R.id.btnBatal);
        btnBatal.setText("Nanti");
        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("nama",namaBengkel);
                context.startActivity(i);
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                dialogChat.dismiss();
            }
        });
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogChat.dismiss();
            }
        });
        notif = MediaPlayer.create(this, R.raw.notif);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnback.setVisibility(View.VISIBLE);
        if(salon)
            txtTitle.setText("Salon");
        else
            txtTitle.setText("Bengkel");
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        final Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        startOnLoadingAnimation();
        if(!session.isLoggedIn()) {
            Intent i = new Intent(this, AuthenticationActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        card_view_progress = (RelativeLayout) findViewById(R.id.relativeLayout12);
        progressText = (TextView) findViewById(R.id.textView106);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar2.setProgress(0);



        mDatabase = FirebaseDatabase.getInstance();

        myRef = mDatabase.getReference("jobs").orderByChild("id").equalTo(session.getBengkelId());
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                long total = dataSnapshot.child("messages").getChildrenCount();
                long tot = 0;
                for (DataSnapshot messageSnapshot: dataSnapshot.child("messages").getChildren()) {
                    tot=tot+1;
                    Log.d("total ="+total,"saat ini"+tot);
                    String userId = (String) messageSnapshot.child("id").getValue();
                    boolean is_read = (boolean) messageSnapshot.child("is_read").getValue();
                    if(!userId.contentEquals(session.getUserId())){
                        mDatabase.getReference("jobs").child(dataSnapshot.getKey()).child("messages").child(messageSnapshot.getKey()).child("is_sent").setValue(true);
                    }
                    if(!userId.contentEquals(session.getUserId()) && !is_read && tot==total){
                        notif.start();
                        if (dialogChat != null && !dialogChat.isShowing())
                            dialogChat.show();
                    }
                }


                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildChanged:" + dataSnapshot.child("messages").getValue());
                Log.d("tag", "onChildAdded:" + dataSnapshot.child("messages").getValue());
                long total = dataSnapshot.child("messages").getChildrenCount();
                long tot = 0;
                for (DataSnapshot messageSnapshot: dataSnapshot.child("messages").getChildren()) {
                    tot=tot+1;
                    Log.d("total ="+total,"saat ini"+tot);
                    String userId = (String) messageSnapshot.child("id").getValue();
                    boolean is_read = (boolean) messageSnapshot.child("is_read").getValue();
                    if(!userId.contentEquals(session.getUserId())){
                        mDatabase.getReference("jobs").child(dataSnapshot.getKey()).child("messages").child(messageSnapshot.getKey()).child("is_sent").setValue(true);
                    }
                    if(!userId.contentEquals(session.getUserId()) && !is_read && tot==total){
                        notif.start();
                        if (dialogChat != null && !dialogChat.isShowing())
                            dialogChat.show();
                    }
                }
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("tag", "onChildRemoved:" + dataSnapshot.child("messages").getValue());


                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("tag", "onChildMoved:" + dataSnapshot.child("messages").getValue());


                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("tag", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "gagal mendapatkan info chat.",
                        Toast.LENGTH_SHORT).show();
            }


        };


    }
    @Override
    public void onStart(){
        super.onStart();
        isActive = true;
        myRef.addChildEventListener(childEventListener);
    }
    @Override
    public void onResume(){
        super.onResume();
        isActive =true;
        startGetStatusJobOrder();
        if(salon)
            Toast.makeText(this,"salon benar",Toast.LENGTH_LONG);
    }
    @Override
    public void onPause(){
        super.onPause();
        isActive = false;
        myRef.removeEventListener(childEventListener);
        stopGetStatusJobOrder();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        isActive =false;
        myRef.removeEventListener(childEventListener);
        AppController.getInstance().cancelPendingRequests("volley");
    }

    public void startOnLoadingAnimation(){
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotation.setRepeatCount(Animation.INFINITE);
        rotationA = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation_bap_loading);
        rotationA.setRepeatCount(Animation.INFINITE);
        rotationC = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotationC.setRepeatCount(Animation.INFINITE);
        lB.setAnimation(rotation);
        lA.setAnimation(rotationA);
        lP.setAnimation(rotationC);
        layoutLoading.setVisibility(View.VISIBLE);
    }
    public void stopOnLoadingAnimation(){
        layoutLoading.setVisibility(View.GONE);
        lB.clearAnimation();
        lA.clearAnimation();
        lP.clearAnimation();
    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public Runnable orderStatusTread = new Runnable() {
        @Override
        public void run() {
            bengkelService.getJobDetailUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject res = new JSONObject(result);

                        // check for error
                        if (!res.getBoolean("error")) {
                            session.setBengkelAktif(true);
                            JSONObject obj = new JSONObject(res.getString("content"));
                            if(session.getBengkelId().equals("")){
                                session.setBengkelId(obj.getString("jobid"));
                            }
                            else{
                                namaBengkel = obj.getString("nama");
                                session.setNamaUserJob(namaBengkel);
                            }
                            if (obj.getString("jobcode").equals("19")) {
                                finish();
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "Order telah dibatalkan", Toast.LENGTH_LONG).show();

                            }else if (obj.getString("jobcode").equals("98")) {
                                finish();
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "Order telah dibatalkan", Toast.LENGTH_LONG).show();

                            }else if (obj.getString("jobcode").equals("92")) {
                                session.setOrderAktif(false);
                                session.setOrderId("");
                                session.setOrderStatus("");
                                Toast.makeText(context, "ORDER TELAH DI-CANCEL OLEH SISTEM KERENA BATAS WAKTU PEMBAYARAN TELAH LEWAT", Toast.LENGTH_LONG).show();

                            } else if (obj.getString("jobcode").equals("1")) {
                                if(!status.equals("waitResponBengkel")){
                                    card_view_progress.setVisibility(View.VISIBLE);
                                    setProgress(20,"Langkah 1 dari 5");
                                    txtTitle.setText("Cari Bengkel");
                                    bengkelGetResponFragment = new BengkelGetResponFragment();
                                    changeFragment(bengkelGetResponFragment);
                                    status = "waitResponBengkel";
                                }
                            }
                            else if (obj.getString("jobcode").equals("101")) {
                                if(!status.equals("waitResponSalon")){
                                    setSalonAktif();
                                    card_view_progress.setVisibility(View.VISIBLE);
                                    setProgress(20,"Langkah 1 dari 5");
                                    txtTitle.setText("Cari Salon");
                                    bengkelGetResponFragment = new BengkelGetResponFragment();
                                    changeFragment(bengkelGetResponFragment);
                                    status = "waitResponSalon";
                                }
                            } else if (obj.getString("jobcode").equals("2")) {
                                if(!status.equals("ResponBengkel")){
                                    dialogStatus("Bengkel kami telah merespon request Anda");
                                    try {
                                        setProgress(20,"Langkah 1 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        status = "ResponBengkel";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("102")) {
                                if(!status.equals("ResponSalon")){
                                    setSalonAktif();
                                    dialogStatus("Salon kami telah merespon request Anda");
                                    try {
                                        setProgress(20,"Langkah 1 dari 5");
                                        bengkelResponseFragment = new BengkelResponseFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelResponseFragment.setArguments(bundle);
                                        changeFragment(bengkelResponseFragment);
                                        status = "ResponSalon";
                                        Log.d("salon",res.getString("content"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("21")) {
                                if(!status.equals("bayarPanggil")){
                                    dialogStatus("Bayarlah biaya kunjungan dengan metode yang tersedia");
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelBayarPanggilFragment = new BengkelBayarPanggilFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarPanggilFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarPanggilFragment);
                                        status = "bayarPanggil";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else if (obj.getString("jobcode").equals("121")) {
                                if(!status.equals("bayarPanggil")){
                                    setSalonAktif();
                                    dialogStatus("Bayarlah biaya kunjungan dengan metode yang tersedia");
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelBayarPanggilFragment = new BengkelBayarPanggilFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarPanggilFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarPanggilFragment);
                                        status = "bayarPanggil";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("40")) {
                                if(!status.equals("verifiasiPembayaran")){
                                    dialogStatus("Silakan menunggu, pembayaran anda sedang kami verifikasi");
                                    txtTitle.setText("Panggil Bengkel");
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");

                                        bengkelVerifikasiBayarPanggilanFragment = new BengkelVerifikasiBayarPanggilanFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelVerifikasiBayarPanggilanFragment.setArguments(bundle);
                                        changeFragment(bengkelVerifikasiBayarPanggilanFragment);
                                        status = "verifiasiPembayaran";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("140")) {
                                if(!status.equals("verifiasiPembayaran")){
                                    setSalonAktif();
                                    Log.d("jobcode",obj.getString("jobcode"));
                                    dialogStatus("Silakan menunggu, pembayaran anda sedang kami verifikasi");
                                    txtTitle.setText("Panggil Salon");
                                    try {
                                        setProgress(40,"Langkah 2 dari 5");
                                        bengkelVerifikasiBayarPanggilanFragment = new BengkelVerifikasiBayarPanggilanFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelVerifikasiBayarPanggilanFragment.setArguments(bundle);
                                        changeFragment(bengkelVerifikasiBayarPanggilanFragment);
                                        status = "verifiasiPembayaran";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("41") || obj.getString("jobcode").equals("42")) {
                                if(!status.equals("PembayaranDiterima")){
                                    dialogStatus("Pembayaran diterima, silakan menunggu bengkel datang");
                                    try {
                                        setProgress(60,"Langkah 3 dari 5");
                                        bengkelBiayaDiterimaFragment = new BengkelBiayaDiterimaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBiayaDiterimaFragment.setArguments(bundle);
                                        changeFragment(bengkelBiayaDiterimaFragment);
                                        status = "PembayaranDiterima";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    bengkelBiayaDiterimaFragment.setBengkelPosition(obj.getDouble("bengkel_lat"),obj.getDouble("bengkel_lng"));
                                }
                            }else if (obj.getString("jobcode").equals("141") || obj.getString("jobcode").equals("142")) {
                                if(!status.equals("PembayaranDiterima")){
                                    setSalonAktif();
                                    dialogStatus("Pembayaran diterima, silakan menunggu salon datang");
                                    try {
                                        setProgress(60,"Langkah 3 dari 5");
                                        bengkelBiayaDiterimaFragment = new BengkelBiayaDiterimaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBiayaDiterimaFragment.setArguments(bundle);
                                        changeFragment(bengkelBiayaDiterimaFragment);
                                        status = "PembayaranDiterima";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    bengkelBiayaDiterimaFragment.setBengkelPosition(obj.getDouble("bengkel_lat"),obj.getDouble("bengkel_lng"));
                                }
                            }else if (obj.getString("jobcode").equals("4")) {
                                if(!status.equals("BengkelDatang")){
                                    try {
                                        setProgress(60,"Langkah 3 dari 5");
                                        bengkelBiayaDiterimaFragment = new BengkelBiayaDiterimaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBiayaDiterimaFragment.setArguments(bundle);
                                        changeFragment(bengkelBiayaDiterimaFragment);
                                        status = "BengkelDatang";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("104")) {
                                if(!status.equals("BengkelDatang")){
                                    try {
                                        setSalonAktif();
                                        setProgress(60,"Langkah 3 dari 5");
                                        bengkelBiayaDiterimaFragment = new BengkelBiayaDiterimaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBiayaDiterimaFragment.setArguments(bundle);
                                        changeFragment(bengkelBiayaDiterimaFragment);
                                        status = "BengkelDatang";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("5")) {
                                if(!status.equals("BayarJasa")){
                                    dialogStatus("Silakan melakukan pembayaran biaya jasa sesuai dengan kesepakatan antara Anda dan Bengkel");
                                    txtTitle.setText("Biaya Perbaikan");
                                    try {
                                        setProgress(80,"Langkah 4 dari 5");
                                        bengkelBayarJasaFragment = new BengkelBayarJasaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarJasaFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarJasaFragment);
                                        status = "BayarJasa";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("105")) {
                                if(!status.equals("BayarJasa")){
                                    dialogStatus("Silakan melakukan pembayaran biaya jasa sesuai dengan kesepakatan antara Anda dan Salon");
                                    txtTitle.setText("Biaya Perbaikan");
                                    try {
                                        setSalonAktif();
                                        setProgress(80,"Langkah 4 dari 5");
                                        bengkelBayarJasaFragment = new BengkelBayarJasaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarJasaFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarJasaFragment);
                                        status = "BayarJasa";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("60")) {
                                if(!status.equals("VerifikasiBayarJasa")){
                                    dialogStatus("Silakan menunggu, pembayaran anda sedang kami verifikasi");
                                    try {
                                        setProgress(80,"Langkah 4 dari 5");
                                        bengkelVerifikasiBayarJasaFragment = new BengkelVerifikasiBayarJasaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelVerifikasiBayarJasaFragment.setArguments(bundle);
                                        changeFragment(bengkelVerifikasiBayarJasaFragment);
                                        status = "VerifikasiBayarJasa";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("160")) {
                                if(!status.equals("VerifikasiBayarJasa")){
                                    dialogStatus("Silakan menunggu, pembayaran anda sedang kami verifikasi");
                                    try {
                                        setSalonAktif();
                                        setProgress(80,"Langkah 4 dari 5");
                                        bengkelVerifikasiBayarJasaFragment = new BengkelVerifikasiBayarJasaFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelVerifikasiBayarJasaFragment.setArguments(bundle);
                                        changeFragment(bengkelVerifikasiBayarJasaFragment);
                                        status = "VerifikasiBayarJasa";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("61") || obj.getString("jobcode").equals("62") || obj.getString("jobcode").equals("66")) {
                                if(!status.equals("bayarSelesai")){
                                    stopGetStatusJobOrder();
                                    try {
                                        setProgress(100,"Selesai");
                                        bengkelBayarSelesaiFragment = new BengkelBayarSelesaiFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarSelesaiFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarSelesaiFragment);
                                        status = "bayarSelesai";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (obj.getString("jobcode").equals("161") || obj.getString("jobcode").equals("162") || obj.getString("jobcode").equals("66")) {
                                if(!status.equals("bayarSelesai")){
                                    stopGetStatusJobOrder();
                                    try {
                                        setSalonAktif();
                                        setProgress(100,"Selesai");
                                        bengkelBayarSelesaiFragment = new BengkelBayarSelesaiFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",res.getString("content"));
                                        bengkelBayarSelesaiFragment.setArguments(bundle);
                                        changeFragment(bengkelBayarSelesaiFragment);
                                        status = "bayarSelesai";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (obj.getString("jobcode").equals("70")) {
                                stopGetStatusJobOrder();
                                if(!status.equals("shipment")){
                                    dialogStatus("Terima kasih telah menggunakan Aplikasi ini");
                                }
                            }
                        }
                        else if(res.getBoolean("error")) {
                            if(!status.equals("firsttime") && session.getBengkelId().equals("")){
                                bengkelSearchFragment = new BengkelSearchfragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("kat",kat);
                                bengkelSearchFragment.setArguments(bundle);
                                changeFragment(bengkelSearchFragment);
                                stopOnLoadingAnimation();
                                stopGetStatusJobOrder();
                                status="firsttime";
                            }
                            else {
                                session.setBengkelSalon(false);
                                session.setBengkelAktif(false);
                                session.setBengkelUserId("");
                                session.setBengkelIdOrder("");
                                session.setBengkelId("");
                            }
                        }
                    } catch (JSONException e) {
                    }
                    return result;
                }

                @Override
                public String onError(VolleyError result) {
                    return null;
                }
            });
            handler.postDelayed(orderStatusTread,1000);

        }

    };
    public Runnable getTimer = new Runnable() {
        @Override
        public void run() {
            order.getTime(session.getUserId(), new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if(!obj.getBoolean("error")) {
                            String content = obj.getString("content");
                            //paymentWaitFragment.setTime(content);
                        }
                        else{
                            stopGetTime();
                            startGetStatusJobOrder();
                        }
                        return null;
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
            handler.postDelayed(getTimer,3000);
        }

    };
    public void startGetStatusJobOrder(){
        handler.post(orderStatusTread);
    }
    public void stopGetStatusJobOrder(){
        handler.removeCallbacks(orderStatusTread);
    }
    public void startGetTime(){
        handler.post(getTimer);

    }

    public void stopGetTime(){
        handler.removeCallbacks(getTimer);
    }
    @Override
    public void onStop(){
        super.onStop();
        stopGetStatusJobOrder();
        stopGetTime();
    }
    public void dialogStatus(String text){
        if(!text.equals(""))
        dialogStatusText.setText(text);
        if(!dialogStatus.isShowing())
            dialogStatus.show();

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void dialogFitur(View v){
        if(!dialogError.isShowing())
            dialogError.show();
    }

    public void setProgress(int now,String text){
        card_view_progress.setVisibility(View.VISIBLE);
        anim = new ProgressBarAnimation(progressBar2, pro, now);
        anim.setDuration(1000);
        progressText.setText(text);
        progressBar2.startAnimation(anim);
        pro=pro+20;
    }

    public boolean getSalon(){
        return salon;
    }
    public void setSalonAktif(){
        salon = true;
        session.setBengkelSalon(true);
    }

}
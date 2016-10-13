package com.beliautopart.beliautopart.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.adapter.CartAdapter;
import com.beliautopart.beliautopart.helper.LocationUser;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.ItemProduk;
import com.beliautopart.beliautopart.utils.GPSTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sesssionmanager;
    private Toolbar toolbar;
    private TextView txtNama;
    private ImageButton btnCart;
    private Animation rotation;
    private Animation backrotation;
    private LocationUser locationUser;
    private GPSTracker gps;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "nope";
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        sesssionmanager = new SessionManager(this);
        locationUser = new LocationUser(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.imgLogo);
        logo.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("");
        txtNama = (TextView) findViewById(R.id.txtNama);



        RelativeLayout buttonPartMobilBackgroun = (RelativeLayout) this.findViewById(R.id.buttonPartMobilBackgroun);
        RelativeLayout buttonPartMotorBackgroun = (RelativeLayout) this.findViewById(R.id.buttonPartMotorBackgroun);
        RelativeLayout buttonBengkelBackgroun = (RelativeLayout) this.findViewById(R.id.buttonBengkelBackgroun);
        RelativeLayout buttonHiburanBackgroun = (RelativeLayout) this.findViewById(R.id.buttonHiburanBackgroun);
        RelativeLayout buttonTentangBackgroun = (RelativeLayout) this.findViewById(R.id.buttonTentangBackgroun);
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation);
        rotation.setRepeatCount(Animation.INFINITE);
        backrotation = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation);
        backrotation.setRepeatCount(Animation.INFINITE);

        buttonPartMobilBackgroun.startAnimation(rotation);
        buttonPartMotorBackgroun.startAnimation(rotation);
        buttonBengkelBackgroun.startAnimation(rotation);
        buttonHiburanBackgroun.startAnimation(rotation);
        buttonTentangBackgroun.startAnimation(backrotation);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    if(sesssionmanager.getToken()!=null && !sesssionmanager.getToken().equals(""))
                        mAuth.signInWithCustomToken(sesssionmanager.getToken()).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("tag","signInWithCustomToken:onComplete:" + task.isSuccessful());
                                    if (!task.isSuccessful()) {
                                        Log.d("tag", "signInWithCustomToken", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onLoginClick(View v) {
        Intent i = new Intent(this, AuthenticationActivity.class);
        i.putExtra("page", "login");
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onRegisterClick(View v) {
        Intent i = new Intent(this, AuthenticationActivity.class);
        i.putExtra("page", "register");
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onSearchPartMobilClick(View v) {

        gps = new GPSTracker(getApplicationContext());
        if (!gps.canGetLocation()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
            alertDialog.setTitle("Peringatan GPS");
            alertDialog.setMessage("GPS tidak aktif. Aktifkan sekarang?");
            alertDialog.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent,1);
                }
            });
            alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();

        }

        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 && sesssionmanager.getOrderAktif()){
            Intent i = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 &&  sesssionmanager.getCart()!=null){
            Intent i = new Intent(this, CartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 &&  sesssionmanager.getbengkelAktif()){
            Intent i = new Intent(this, BengkelActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 &&  sesssionmanager.getbengkelAktif() && sesssionmanager.getOrderAktif()){
            Intent i = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else {
            Intent i = new Intent(this, SearchActivity.class);
            i.putExtra("kat", "2");
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void onSearchPartMotorClick(View v) {

        gps = new GPSTracker(getApplicationContext());
        if (!gps.canGetLocation()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
            alertDialog.setTitle("Peringatan GPS");
            alertDialog.setMessage("GPS tidak aktif. Aktifkan sekarang?");
            alertDialog.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent,1);
                }
            });
            alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();

        }
        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 && sesssionmanager.getOrderAktif()){
            Intent i = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 && sesssionmanager.getCart()!=null){
            Intent i = new Intent(this, CartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 && sesssionmanager.getbengkelAktif()){
            Intent i = new Intent(this, BengkelActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if(sesssionmanager.getUserlat()!=0 && sesssionmanager.getUserlng()!=0 && sesssionmanager.getbengkelAktif() && sesssionmanager.getOrderAktif()){
            Intent i = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else {
            Intent i = new Intent(this, SearchActivity.class);
            i.putExtra("kat", "1");
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
    public void onBengkelClick(View v) {
        Intent i = null;
        if(sesssionmanager.getOrderAktif()){
            showCartAlert();
        }
        else if(sesssionmanager.isLoggedIn()){
            i = new Intent(this, BengkelActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        else{
            i = new Intent(this, AuthenticationActivity.class);
            i.putExtra("kat", "1");
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
    public void onAccountClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, AccountActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onComplaintClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, ComplaintActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onMyOrderClick(View v) {
        Intent i;
        if(sesssionmanager.isLoggedIn())
            i = new Intent(this, MyOrderActivity.class);
        else
            i = new Intent(this, AuthenticationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onTandCClick(View v) {
        Intent i = new Intent(this, TandCActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onPolicyClick(View v) {
        Intent i = new Intent(this, PolicyActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onAboutClick(View v) {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onHiburanClick(View v) {
        Intent i = new Intent(this, HiburanActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onBackClick(View v){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sesssionmanager = new SessionManager(this);
        locationUser = new LocationUser(this);
        locationUser.start();
        String Nama= sesssionmanager.getUserNama();

        int umur = getAge(sesssionmanager.getUserThn(),sesssionmanager.getUserBln(),sesssionmanager.getUserTgl());
        String stat ="";
        if(umur>=30)
            if(sesssionmanager.getUserJk().equals("L"))
                stat = "Bapak";
            else
                stat = "Ibu";
        else
            if(sesssionmanager.getUserJk().equals("L"))
                stat = "Sdr.";
            else
                stat = "Sdri.";
        if(sesssionmanager.isLoggedIn()){
            txtNama.setText(Html.fromHtml("Selamat Datang, "+"<font color=#d9474f><b>"+stat+" "+ Nama.substring(0,1).toUpperCase() + Nama.substring(1).toLowerCase() +"</b></font>") );
        }
        else{
            txtNama.setText("Selamat Datang");
        }
    }


    public void showCartAlert(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_alert_order_aktif);
        dialog.setCancelable(true);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public int getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if(DOBmonth > currentMonth){
            --age;
        }
        else if(DOBmonth == currentMonth){
            if(DOBday > todayDay){
                --age;
            }
        }
        return age;
    }
}

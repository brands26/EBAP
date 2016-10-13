package com.beliautopart.beliautopart.activity;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.app.AppController;
import com.beliautopart.beliautopart.fragment.LoginFragment;
import com.beliautopart.beliautopart.fragment.RegisterFragment;
import com.beliautopart.beliautopart.helper.SessionManager;

public class AuthenticationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private SessionManager session;
    private boolean order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        setContentView(R.layout.activity_authentication);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.imgLogo);
        logo.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("");
        String page;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                page = "login";
            } else {
                order = extras.getBoolean("order",false);
                page = extras.getString("page");
            }
        } else {
            page = (String) savedInstanceState.getSerializable("page");
        }
        if (page.equals("login")) {
            loginFragment = new LoginFragment();
            changeFragment(loginFragment);
        } else {
            registerFragment = new RegisterFragment();
            changeFragment(registerFragment);
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_alert_order_aktif);
        TextView textMes  = (TextView) dialog.findViewById(R.id.textView27);
        textMes.setText("Silakan login terlebih dahulu untuk melanjutkan");
        dialog.setCancelable(true);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if(session.getCart()!=null && order)
            dialog.show();
    }


    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameAuth, fragment);
        fragmentTransaction.commit();
    }
    private void onRegisterButtonClick(View view){
        registerFragment = new RegisterFragment();
        changeFragment(registerFragment);
    }
    public void onBackClick(View v){
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }


}

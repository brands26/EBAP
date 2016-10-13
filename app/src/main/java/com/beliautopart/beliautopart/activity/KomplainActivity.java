package com.beliautopart.beliautopart.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.webservices.OrderService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KomplainActivity extends AppCompatActivity {

    private TextView txtOrderNo;
    private String idOrder;
    private OrderService order;
    private RelativeLayout btnSimpan;
    private ImageButton cartButton;
    private TextView countTextview;
    private ImageButton btnback;
    private TextView txtTitle;
    private RelativeLayout loadingView;
    private SessionManager session;
    private Toolbar toolbar;
    private EditText inputKomplain;
    private String status;
    private String nojob;
    private String norder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                status = "order";
                idOrder = "1";
            } else {
                status = extras.getString("status");
                idOrder = extras.getString("id");
            }
        } else {
            idOrder = (String) savedInstanceState.getSerializable("idOrder");
        }
        cartButton = (ImageButton) findViewById(R.id.btnCart);
        inputKomplain = (EditText) findViewById(R.id.inputKomplain);
        countTextview = (TextView) findViewById(R.id.badge_textView);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Komplain Anda");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        loadingView = (RelativeLayout) findViewById(R.id.loadingLayout);
        session = new SessionManager(this);
        btnback.setVisibility(View.VISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        cartButton.setVisibility(View.GONE);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        order = new OrderService(this);
        txtOrderNo = (TextView) findViewById(R.id.txtKomplainNoOrder);
        txtOrderNo.setTypeface(tf);
        btnSimpan = (RelativeLayout) findViewById(R.id.lbtnSimpan);
        if(status.equalsIgnoreCase("job")){
            order.getHistoryJobDetail(idOrder, 0, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (!obj.getBoolean("error")) {
                            JSONObject data = new JSONObject(obj.getString("content"));
                            txtOrderNo.setText("No. Job Terkait: "+data.getString("jobid"));
                            nojob = data.getString("jobid");
                            norder = data.getString("orderno");
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
            order.getOrderDetail(idOrder, 1, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (!obj.getBoolean("error")) {
                            JSONObject data = new JSONObject(obj.getString("content"));
                            txtOrderNo.setText("No. Order Terkait: "+data.getString("nomor"));
                            norder =idOrder;
                            nojob = "null";
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
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String komplain = Html.toHtml(inputKomplain.getText());
                order.setKomplain(norder, session.getUserId(), nojob, komplain, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        Toast.makeText(getApplicationContext(),"berhasil ditambahkan",Toast.LENGTH_LONG).show();
                        finish();
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
            }
        });
    }

    public void onBackClick(View v){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

package com.beliautopart.beliautopart.activity;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.adapter.ItemListAdapter;
import com.beliautopart.beliautopart.helper.DIalogSearch;
import com.beliautopart.beliautopart.helper.Logic;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.ItemProduk;
import com.beliautopart.beliautopart.utils.GPSTracker;
import com.beliautopart.beliautopart.webservices.PartsService;

/**
 * Created by Brands on 23/08/2016.
 */

public class OrderIndentActivity extends AppCompatActivity {
    private String id;
    private Toolbar toolbar;
    private ImageButton btnback;
    private TextView txtTitle;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;
    private RelativeLayout loadingView;
    private TextView txtId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent_order);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = "1";
            } else {
                id = extras.getString("id");
            }
        } else {
            id = (String) savedInstanceState.getSerializable("id");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtId = (TextView) findViewById(R.id.textView108);
        txtTitle.setText("Order Inden");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        txtId.setText(id);



        loadingView = (RelativeLayout) findViewById(R.id.loadingLayout);

        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        btnback.setVisibility(View.VISIBLE);
    }
}

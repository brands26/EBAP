package com.beliautopart.beliautopart.helper;

/**
 * Created by Brands Pratama on 7/26/2016.
 */
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.BengkelActivity;
import com.beliautopart.beliautopart.activity.CartActivity;
import com.beliautopart.beliautopart.activity.SearchActivity;
import com.beliautopart.beliautopart.fragment.DialogSearchMerk;
import com.beliautopart.beliautopart.fragment.DialogServiceParts;
import com.beliautopart.beliautopart.model.RefModel;
import com.beliautopart.beliautopart.model.SearchOptionModel;
import com.beliautopart.beliautopart.utils.GPSTracker;
import com.beliautopart.beliautopart.webservices.PartsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;

public class DIalogSearch extends DialogFragment {
    public String kategori;
    public int Jenis;
    private DialogSearchMerk dialogSearchmerk;
    private PartsService partsService;
    private List<RefModel> merk = new ArrayList<>();
    private List<RefModel> tipe = new ArrayList<>();
    private String merkKendaraan=null;
    private String tipeKendaraan=null;
    private String jenis=null;
    private DialogServiceParts dialogServiceParts;
    private SessionManager session;
    private static RelativeLayout searchLayout;
    private static Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_search_item, container,
                false);
        dialog = getDialog();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        searchLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout27);
        ImageView btnBack = (ImageView) rootView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 1) {
                    fm.popBackStack();
                }
                else {
                    getDialog().dismiss();
                }
            }
        });
        final EditText SearchInput = (EditText) rootView.findViewById(R.id.SearchInput);
        SearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String kat = ((SearchActivity)getActivity()).getKat();
                    SearchOptionModel searchOption = new SearchOptionModel();
                    searchOption.setKat(kat);
                    searchOption.setLat(""+session.getUserlat());
                    searchOption.setLng(""+session.getUserlng());
                    searchOption.setKeyword(SearchInput.getText().toString().trim());
                    ((SearchActivity)getActivity()).startOnLoadingAnimation();
                    ((SearchActivity)getActivity()).onSearch(searchOption);
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        });
        SearchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    SearchInput.setTypeface(null,Typeface.NORMAL);
                }
                else
                {
                    SearchInput.setTypeface(null, Typeface.ITALIC);
                }
            }
        });
        RelativeLayout btnSearch = (RelativeLayout) rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = SearchInput.getText().toString().trim();
                if(!key.equals("")){
                    String kat = ((SearchActivity)getActivity()).getKat();
                    SearchOptionModel searchOption = new SearchOptionModel();
                    searchOption.setKat(kat);
                    searchOption.setKeyword(key);
                    searchOption.setLat(""+session.getUserlat());
                    searchOption.setLng(""+session.getUserlng());
                    ((SearchActivity)getActivity()).startOnLoadingAnimation();
                    ((SearchActivity)getActivity()).onSearch(searchOption);
                    getDialog().dismiss();
                }
                else{
                    Toast.makeText(getContext(),"Masukkan nama Barang yang anda butuhkan",Toast.LENGTH_SHORT).show();
                }
            }
        });
        session = new SessionManager(getContext());
        dialogServiceParts = new DialogServiceParts();
        dialogSearchmerk = new DialogSearchMerk();
        partsService = new PartsService(getContext());
        if(session.getbengkelAktif() || session.getCart()!=null)
            changeFragment(dialogSearchmerk);
        else
            changeFragment(dialogServiceParts);
        // Do something else
        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        rootView.setMinimumWidth((int)(displayRectangle.width() * 0.8f));
        return rootView;
    }
    public void changeFragment(Fragment fragment) {
        if(!fragment.isAdded())
        {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    public static void setLayoutGone(){
        searchLayout.setVisibility(View.GONE);
    }
    public static void setLayoutVis(){
        searchLayout.setVisibility(View.VISIBLE);
    }
    public static void dialogDismiss(){
        dialog.dismiss();
    }




}


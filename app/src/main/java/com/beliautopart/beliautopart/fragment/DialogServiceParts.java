package com.beliautopart.beliautopart.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.BengkelActivity;
import com.beliautopart.beliautopart.activity.SearchActivity;
import com.beliautopart.beliautopart.helper.DIalogSearch;
import com.beliautopart.beliautopart.utils.GPSTracker;

/**
 * Created by Brands on 26/08/2016.
 */
public class DialogServiceParts extends Fragment {
    private View v;
    private DialogSearchMerk dialogSearchmerk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DIalogSearch.setLayoutGone();

        v = inflater.inflate(R.layout.fragment_dialog_search_service_parts, container, false);
        RelativeLayout lspare = (RelativeLayout) v.findViewById(R.id.lspare);
        RelativeLayout laks = (RelativeLayout) v.findViewById(R.id.laks);
        RelativeLayout salon = (RelativeLayout) v.findViewById(R.id.salon);
        dialogSearchmerk = new DialogSearchMerk();
        lspare.setOnClickListener(new View.OnClickListener() {
            public GPSTracker gps;

            @Override
            public void onClick(View v) {
                gps = new GPSTracker(getContext());
                if (gps.canGetLocation()) {

                    Intent i = new Intent(getContext(), BengkelActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("kat", ((SearchActivity)getActivity()).getKat());
                    bundle.putBoolean("salon", false);
                    i.putExtras(bundle);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else {
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
            }
        });
        salon.setOnClickListener(new View.OnClickListener() {
            public GPSTracker gps;

            @Override
            public void onClick(View v) {
                gps = new GPSTracker(getContext());
                if (gps.canGetLocation()) {

                    Intent i = new Intent(getContext(), BengkelActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("kat", ((SearchActivity)getActivity()).getKat());
                    bundle.putBoolean("salon", true);
                    i.putExtras(bundle);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else {
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
            }
        });
        laks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getParentFragment().getChildFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.frameLayout, dialogSearchmerk);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        if(((SearchActivity)getActivity()).getKat().equalsIgnoreCase("1")){
            salon.setVisibility(View.GONE);
        }
        return v;
    }
}

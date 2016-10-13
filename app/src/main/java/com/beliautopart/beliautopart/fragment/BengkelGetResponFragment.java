package com.beliautopart.beliautopart.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.webservices.BengkelService;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelGetResponFragment extends Fragment {
    private String content;
    private Context context;
    private View v;
    private RelativeLayout layoutLoading;
    private SessionManager session;
    private RelativeLayout btnBatal;
    private BengkelService bengkel;
    private Dialog dialogError;
    private boolean salon = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        salon = ((BengkelActivity)getActivity()).getSalon();

        v = inflater.inflate(R.layout.fragment_bengkel_get_respon, container, false);
        context = getContext();
        session = new SessionManager(context);
        bengkel = new BengkelService(context);
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel1);
        ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtJobID.setText(session.getBengkelId());
        btnBatal = (RelativeLayout) v.findViewById(R.id.lbtnSimpan);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneAlert();
            }
        });
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtNoOrderVerifikasi);
        tv.setTypeface(tf);
        if(salon){
            TextView txtStatus = (TextView) v.findViewById(R.id.txtStatus);
            txtStatus.setText("Menunggu respon dari salon terdekat yang tersedia.");
            TextView txtNoOrderVerifikasi = (TextView) v.findViewById(R.id.txtNoOrderVerifikasi);
            txtNoOrderVerifikasi.setText("Menunggu Respon Salon Terdekat");

        }
        return v;
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
        btnBatal.setText("Tidak");

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

}


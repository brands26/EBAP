package com.beliautopart.beliautopart.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.HiburanActivity;
import com.beliautopart.beliautopart.app.AppController;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 19/06/2016.
 */
public class HiburanDetailKonten extends Fragment{
    private View v;
    private Activity context;
    private OrderService orderService;
    private SessionManager session;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private RelativeLayout btnBatal;
    private String nama;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LinearLayout vewList;
    private String url;
    private TextView txtJudul;
    private TextView txtIsi;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_hiburan_detail_content, container, false);
        context = (Activity)getContext();
        orderService = new OrderService(context);
        session = new SessionManager(context);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        //txtTitle.setText("Order");
        txtJudul =(TextView) v.findViewById(R.id.txtJudul);
        txtIsi =(TextView) v.findViewById(R.id.txtIsi);
        content = getArguments().getString("content");
        nama = getArguments().getString("nama","");
        if(!getArguments().getString("nama").equals(""))
            txtTitle.setText(nama);
        setUp(content);
        return v;
    }
    public void setUp(String content){
        if(!content.equals("")){
            try {
                JSONObject obj = new JSONObject(content);
                txtIsi.setText(Html.fromHtml(obj.getString("isi")));
                txtJudul.setText(obj.getString("judul"));
                ((HiburanActivity)getActivity()).stopOnLoadingAnimation();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            ((HiburanActivity)getActivity()).stopOnLoadingAnimation();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}

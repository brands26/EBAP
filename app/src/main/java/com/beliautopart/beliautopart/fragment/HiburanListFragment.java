package com.beliautopart.beliautopart.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.BengkelActivity;
import com.beliautopart.beliautopart.activity.HiburanActivity;
import com.beliautopart.beliautopart.app.AppController;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.ItemProduk;
import com.beliautopart.beliautopart.webservices.HiburanService;
import com.beliautopart.beliautopart.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon Pratama on 19/06/2016.
 */
public class HiburanListFragment extends Fragment{
    private View v;
    private Activity context;
    private OrderService orderService;
    private SessionManager session;
    private TextView txtTitle;
    private TextView txtNoOrder;
    private String content;
    private RelativeLayout btnBatal;
    private LinearLayout vewList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private HiburanService hiburanService;
    private String nama;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_hiburan_kategori, container, false);
        context = (Activity)getContext();
        hiburanService = new HiburanService(context);
        session = new SessionManager(context);
        vewList = (LinearLayout) v.findViewById(R.id.linierLayoutKategori);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtNoOrder =(TextView) v.findViewById(R.id.txtNomorOrder);
        content = getArguments().getString("content");

        nama = getArguments().getString("nama","");
        if(!getArguments().getString("nama").equals(""))
            txtTitle.setText(nama);
        setUp(content);
        return v;
    }
    public void setUp(String content){
        try {
            JSONArray data = new JSONArray(content);
            int size = data.length();
            for (int a = 0; a < size; a++) {
                JSONObject dataCart = data.getJSONObject(a);
                try {
                    addVewkategori(dataCart.getString("id"),dataCart.getString("nama"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ((HiburanActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addVewkategori(final String id, final String nama){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_list_kontent_hiburan, null);
        final TextView txtNama = (TextView) child.findViewById(R.id.txtNama);
        NetworkImageView img = (NetworkImageView) child.findViewById(R.id.imgFile);
        String url ="";
        if(id.equals("1"))
            url ="http://beliautopart.com/img/hiburan/hiburan_1.png";
        else if(id.equals("2"))
            url ="http://beliautopart.com/img/hiburan/hiburan_2.png";
        else if(id.equals("3"))
            url ="http://beliautopart.com/img/hiburan/hiburan_3.png";
        else
            url ="http://beliautopart.com/img/hiburan/folder.png";
        img.setImageUrl(url,imageLoader);
        txtNama.setText(""+nama);
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HiburanActivity)getActivity()).startOnLoadingAnimation();
                hiburanService.GetFolder(id, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            final JSONObject res = new JSONObject(result);
                            if(!res.getBoolean("error")){

                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                HiburanListFragment hiburan = new HiburanListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("content",res.getString("content"));
                                bundle.putString("nama",nama);
                                hiburan.setArguments(bundle);
                                ft.replace(R.id.frameLayout, hiburan, "NewFragmentTag");
                                ft.addToBackStack(null);
                                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                                ft.commit();
                            }
                            else{
                                hiburanService.GetKontent(id, new SendDataHelper.VolleyCallback() {
                                    @Override
                                    public String onSuccess(String result) {
                                        try {
                                            JSONObject no = new JSONObject(result);
                                            if(!no.getBoolean("error")){
                                                    final JSONObject obj = new JSONObject(result);
                                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                    HiburanListKontenFragment hiburan = new HiburanListKontenFragment();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("content",obj.getString("content"));
                                                    bundle.putString("nama",nama);
                                                    hiburan.setArguments(bundle);
                                                    ft.replace(R.id.frameLayout, hiburan);
                                                    ft.addToBackStack(null);
                                                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                                                    ft.commit();
                                            }
                                            else{
                                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                HiburanListKontenFragment hiburan = new HiburanListKontenFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("content","");
                                                bundle.putString("nama",nama);
                                                hiburan.setArguments(bundle);
                                                ft.replace(R.id.frameLayout, hiburan);
                                                ft.addToBackStack(null);
                                                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                                                ft.commit();
                                            }
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
        });
        vewList.addView(child);
    }
    @Override
    public void onResume(){
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}

package com.beliautopart.beliautopart.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.SearchActivity;
import com.beliautopart.beliautopart.helper.DIalogSearch;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.SearchOptionModel;

/**
 * Created by Brands on 26/08/2016.
 */
public class DialogSearchJenis extends Fragment {
    private View v;
    private SessionManager session;
    private DialogFragment dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DIalogSearch.setLayoutVis();
        v = inflater.inflate(R.layout.fragment_dialog_search_jenis, container, false);
        RelativeLayout lspare = (RelativeLayout) v.findViewById(R.id.lspare);
        RelativeLayout laks = (RelativeLayout) v.findViewById(R.id.laks);
        session = new SessionManager(getContext());
        lspare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jenis = "2";


                SearchOptionModel searchOption = new SearchOptionModel();

                String kat = ((SearchActivity)getActivity()).getKat();
                String kategori = ((SearchActivity)getActivity()).getkategori();
                String merkKendaraan = ((SearchActivity)getActivity()).getmerkKendaraan();
                String tipeKendaraan = ((SearchActivity)getActivity()).gettipeKendaraan();

                searchOption.setJenis(jenis);
                searchOption.setKatItem(kategori);
                searchOption.setMerk(merkKendaraan);
                searchOption.setKat(kat);
                searchOption.setTipe(tipeKendaraan);
                searchOption.setLat(""+session.getUserlat());
                searchOption.setLng(""+session.getUserlng());
                ((SearchActivity)getActivity()).startOnLoadingAnimation();
                ((SearchActivity)getActivity()).onSearch(searchOption);
                DIalogSearch.dialogDismiss();
            }
        });
        laks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jenis = "1";


                SearchOptionModel searchOption = new SearchOptionModel();


                String kat = ((SearchActivity)getActivity()).getKat();
                String kategori = ((SearchActivity)getActivity()).getkategori();
                String merkKendaraan = ((SearchActivity)getActivity()).getmerkKendaraan();
                String tipeKendaraan = ((SearchActivity)getActivity()).gettipeKendaraan();

                searchOption.setJenis(jenis);
                searchOption.setKatItem(kategori);
                searchOption.setMerk(merkKendaraan.equals("0")?null:merkKendaraan);
                searchOption.setKat(kat);
                searchOption.setTipe(tipeKendaraan.equals("0")?null:tipeKendaraan);
                searchOption.setLat("" + session.getUserlat());
                searchOption.setLng("" + session.getUserlng());
                ((SearchActivity) getActivity()).startOnLoadingAnimation();
                ((SearchActivity) getActivity()).onSearch(searchOption);
                DIalogSearch.dialogDismiss();
            }
        });
        return v;
    }

}

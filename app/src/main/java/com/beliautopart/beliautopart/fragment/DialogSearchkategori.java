package com.beliautopart.beliautopart.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.SearchActivity;
import com.beliautopart.beliautopart.helper.DIalogSearch;

/**
 * Created by Brands on 26/08/2016.
 */
public class DialogSearchkategori extends Fragment {
    private View v;
    private DialogSearchJenis dialogSearchJenis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DIalogSearch.setLayoutVis();
        v = inflater.inflate(R.layout.fragment_dialog_search_kategori, container, false);

        dialogSearchJenis = new DialogSearchJenis();

        RelativeLayout lspare = (RelativeLayout) v.findViewById(R.id.lspare);
        RelativeLayout laks = (RelativeLayout) v.findViewById(R.id.laks);
        lspare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kategori = "1";
                ((SearchActivity)getActivity()).setkategori(kategori);
                FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.frameLayout, dialogSearchJenis);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        laks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kategori = "2";
                ((SearchActivity)getActivity()).setkategori(kategori);
                FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.frameLayout, dialogSearchJenis);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return v;
    }
}

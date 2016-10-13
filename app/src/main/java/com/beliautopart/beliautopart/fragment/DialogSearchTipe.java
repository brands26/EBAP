package com.beliautopart.beliautopart.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.SearchActivity;
import com.beliautopart.beliautopart.helper.DIalogSearch;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.model.RefModel;
import com.beliautopart.beliautopart.webservices.PartsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Brands on 26/08/2016.
 */
public class DialogSearchTipe extends Fragment {
    private View v;
    private ArrayList<RefModel> tipe = new ArrayList<>();
    private PartsService partsService;
    private DialogSearchkategori dialogSearchkategori;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DIalogSearch.setLayoutVis();
        v = inflater.inflate(R.layout.fragment_dialog_search_tipe, container, false);
        Bundle bundle = this.getArguments();
        String merkKendaraan = bundle.getString("merkKendaraan");
        partsService = new PartsService(getContext());
        dialogSearchkategori = new DialogSearchkategori();

        ListView listView = (ListView) v.findViewById(R.id.list);

        final ArrayAdapter<RefModel> adapter = new ArrayAdapter<RefModel>(getContext(), android.R.layout.simple_list_item_1,tipe);
        listView.setAdapter(adapter);
        partsService.getTipeSearch(merkKendaraan, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject resultData = new JSONObject(result);
                    boolean error = resultData.getBoolean("error");
                    if (!error) {
                        JSONArray arrTipe = resultData.getJSONArray("content");
                        adapter.clear();
                        tipe.add(new RefModel(null,"Semua Tipe"));
                        for(int a=0;a<arrTipe.length();a++){
                            JSONObject data = arrTipe.getJSONObject(a);
                            RefModel refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                            tipe.add(refModel);
                        }
                        adapter.notifyDataSetChanged();

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                RefModel item = tipe.get(position);
                if(item.getId()!=null && !item.getId().equals("null"))
                    ((SearchActivity)getActivity()).settipeKendaraan(item.getId());
                FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.frameLayout, dialogSearchkategori);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return v;
    }
}

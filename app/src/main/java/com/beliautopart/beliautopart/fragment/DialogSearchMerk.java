package com.beliautopart.beliautopart.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Brands on 26/08/2016.
 */
public class DialogSearchMerk extends Fragment {
    private View v;
    private PartsService partsService;
    private DialogSearchkategori dialogSearchkategori;
    private ArrayList<RefModel> merk = new ArrayList<>();
    private String merkKendaraan;
    private DialogSearchTipe dialogSearchtipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DIalogSearch.setLayoutVis();
        v = inflater.inflate(R.layout.fragment_dialog_search_merk, container, false);
        partsService = new PartsService(getContext());
        String kat = ((SearchActivity)getActivity()).getKat();

        ListView listView = (ListView) v.findViewById(R.id.list);
        dialogSearchkategori = new DialogSearchkategori();
        dialogSearchtipe = new DialogSearchTipe();

        final ArrayAdapter<RefModel> adapter = new ArrayAdapter<RefModel>(getContext(), android.R.layout.simple_list_item_1,merk);
        listView.setAdapter(adapter);

        partsService.getSearchMenu(kat,0, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if(!object.getBoolean("error")){
                        JSONObject object1 = new JSONObject(object.getString("content"));
                        JSONArray arrMerk = new JSONArray(object1.getString("merk"));
                        merk.add(new RefModel(null,"Semua Merk"));
                        for(int a=0;a<arrMerk.length();a++){
                            JSONObject data = arrMerk.getJSONObject(a);
                            RefModel refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                            merk.add(refModel);
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
                RefModel item = merk.get(position);
                merkKendaraan = item.getId();
                if(merkKendaraan==null){
                    FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frameLayout, dialogSearchkategori);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else{
                    ((SearchActivity)getActivity()).setmerkKendaraan(merkKendaraan);
                    FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("merkKendaraan",merkKendaraan);
                    dialogSearchtipe.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frameLayout, dialogSearchtipe);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

            }
        });
        return v;
    }
}

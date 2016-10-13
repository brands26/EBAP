package com.beliautopart.beliautopart.webservices;

import android.content.Context;

import com.beliautopart.beliautopart.app.AppConfig;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.model.SearchOptionModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon on 12/05/16.
 */
public class PartsService {
    private final Context context;
    private SendDataHelper sendData;
    private String response;

    public PartsService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
    }


    public void SearchParts(final SearchOptionModel option,int dialog, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        if (!option.getKeyword().equals("")) {
            params.put("keyword", option.getKeyword());
        }
        if (option.getKat() != null) {
            params.put("kat", "" + option.getKat());
        }
        if (option.getJenis() != null) {
            params.put("jenis", "" + option.getJenis());
        }
        if (option.getMerk() != null) {
            params.put("merk", "" + option.getMerk());
        }
        if (option.getTipe() != null) {
            params.put("tipe", "" + option.getTipe());
        }
        if (option.getKatItem() != null) {
            params.put("kat_item", "" + option.getKatItem());
        }
        if (option.getPage() != null) {
            params.put("page", "" + option.getPage());
        }
        params.put("lat", "" + option.getLat());
        params.put("lng", "" + option.getLng());
        sendData.SendData(params, AppConfig.URL_PARTS_GET, dialog, callback);
    }
    public void getSearchMenu(String jenis,int Dialog, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("jenis", jenis);
        sendData.SendData(params, AppConfig.URL_PARTS_SEARCH, Dialog, callback);
    }
    public void getTipeSearch(String tipe, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tipe", tipe);
        sendData.SendData(params, AppConfig.URL_PARTS_TIPE, 1, callback);
    }
}

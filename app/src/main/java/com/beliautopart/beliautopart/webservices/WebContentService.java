package com.beliautopart.beliautopart.webservices;

import android.content.Context;

import com.beliautopart.beliautopart.app.AppConfig;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.model.UserModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon on 12/05/16.
 */
public class WebContentService {
    private final Context context;
    private SendDataHelper sendData;
    public WebContentService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
    }

    public void getAboutUs( SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nope", "nope");
        sendData.SendData(params, AppConfig.URL_WEBKONTENT_ABOUT, 0, callback);
    }
    public void getSetting( SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nope", "nope");
        sendData.SendData(params, AppConfig.URL_WEBKONTENT_SETTING, 0, callback);
    }
}

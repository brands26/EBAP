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
public class UserService {
    private final Context context;
    private SendDataHelper sendData;
    private String response;

    public UserService(Context context) {
        this.context = context;
        sendData = new SendDataHelper(context);
    }


    public void RegisterUser(final UserModel user, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("namadpn", user.getNamaDepan());
        params.put("namablk", user.getNamaBelakang());
        params.put("email", user.getEmail());
        params.put("hp", "" + user.getHp());
        params.put("password", user.getPassword());
        params.put("tgl", user.getTgl());
        params.put("bln", user.getBln());
        params.put("thn", user.getThn());
        params.put("jk", user.getjK());
        params.put("FID",user.getFID());
        sendData.SendData(params, AppConfig.URL_USER_REGISTER, 1, callback);
    }

    public void LoginUser(final UserModel user, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", user.getEmail());
        params.put("password", user.getPassword());
        params.put("FID", user.getFID());
        sendData.SendData(params, AppConfig.URL_USER_LOGIN, 1, callback);
    }
    public void ForgotPasswordUser(String email, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        sendData.SendData(params, AppConfig.URL_USER_GET_PASSWORD, 1, callback);
    }
    public void profileUser(String userId, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", userId);
        sendData.SendData(params, AppConfig.URL_USER_PROFILE, 0, callback);
    }
    public void getKab(String id, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        sendData.SendData(params, AppConfig.URL_USER_KAB, 1, callback);
    }
    public void UpdateUser(final UserModel user, SendDataHelper.VolleyCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", ""+user.getId());
        params.put("namadpn", user.getNamaDepan());
        params.put("namablk", user.getNamaBelakang());
        params.put("email", user.getEmail());
        params.put("hp", "" + user.getHp());
        params.put("prop", "" + user.getProvinsi());
        params.put("alamat", "" + user.getAlamat());
        params.put("kabkota", "" + user.getKab());
        params.put("bank", "" + user.getBank());
        params.put("norek", "" + user.getNorek());
        params.put("password", user.getPassword());
        params.put("tgl", user.getTgl());
        params.put("bln", user.getBln());
        params.put("thn", user.getThn());
        params.put("jk", user.getjK());
        sendData.SendData(params, AppConfig.URL_USER_UPDATE, 1, callback);
    }
}

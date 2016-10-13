package com.beliautopart.beliautopart.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.app.AppController;
import com.beliautopart.beliautopart.helper.LocationUser;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.RefModel;
import com.beliautopart.beliautopart.model.UserModel;
import com.beliautopart.beliautopart.webservices.UserService;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputNamaBelakang;
    private EditText inputHp;
    private EditText inputNamaDepan;
    private EditText inputKonfirmPassword;
    private SessionManager session;
    private UserService userService;
    private EditText inputAlamat;
    private ImageButton cartButton;
    private TextView countTextview;
    private ImageButton btnback;
    private TextView txtTitle;
    private RelativeLayout loadingView;
    private Spinner spProvinsi;
    private Spinner spKab;
    private EditText inputNoRekening;
    private List<RefModel> provinsi = new ArrayList<>();
    private List<RefModel> bank  = new ArrayList<>();
    private List<RefModel> kab  = new ArrayList<>();
    private Spinner spBank;
    private String provinsiId="";
    private String kabID="";
    private String bankId="";
    private RelativeLayout btnSimpan;
    private RelativeLayout btnlogout;
    private RelativeLayout layoutLoading;
    private RelativeLayout lB;
    private RelativeLayout lA;
    private RelativeLayout lP;
    private Animation rotation;
    private Animation rotationA;
    private Animation rotationC;
    private ImageButton btnLogout;
    private Dialog dialogError;
    private ArrayAdapter<RefModel>  spinnerArrayAdapter3;
    private LocationUser locationUser;
    private Spinner inputTanggal;
    private Spinner inputBln;
    private Spinner inputTahun;
    private Spinner inputGender;
    private ArrayList<String> listTanggal;
    private ArrayList<String> listBln;
    private ArrayList<String> listThn;
    private ArrayList<String> listJenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        userService = new UserService(this);
        locationUser =new LocationUser(this);
        setContentView(R.layout.activity_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ImageView logo = (ImageView) findViewById(R.id.imgLogo);
        countTextview = (TextView) findViewById(R.id.badge_textView);
        btnback = (ImageButton) findViewById(R.id.btnBack);
        btnSimpan = (RelativeLayout) findViewById(R.id.lbtnSimpan);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.VISIBLE);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneAlert();
            }
        });
        txtTitle.setText("Profile");
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        txtTitle.setTypeface(tf);
        session = new SessionManager(this);
        btnback.setVisibility(View.VISIBLE);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputNamaDepan = (EditText) findViewById(R.id.inputNamadepan);
        inputNamaBelakang = (EditText) findViewById(R.id.inputNamaBelakang);
        inputHp = (EditText) findViewById(R.id.inputHp);
        inputAlamat = (EditText) findViewById(R.id.inputAlamat);
        spProvinsi = (Spinner) findViewById(R.id.spPropinsi);
        spKab = (Spinner) findViewById(R.id.spKab);
        inputNoRekening = (EditText) findViewById(R.id.inputNoRekening);
        spBank = (Spinner) findViewById(R.id.spBank);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputKonfirmPassword = (EditText) findViewById(R.id.inputkonfirmPassword);


        layoutLoading = (RelativeLayout) findViewById(R.id.layoutLoading);
        lB = (RelativeLayout) findViewById(R.id.lB);
        lA = (RelativeLayout) findViewById(R.id.lA);
        lP = (RelativeLayout) findViewById(R.id.lP);

        startOnLoadingAnimation();
        inputTanggal = (Spinner) findViewById(R.id.inputTgl);
        inputBln = (Spinner) findViewById(R.id.inputBln);
        inputTahun = (Spinner) findViewById(R.id.inputTahun);
        inputGender = (Spinner) findViewById(R.id.inputGender);

        listTanggal = new ArrayList<String>();
        listTanggal.add("Tgl");
        for(int a=1;a<=31;a++){
            listTanggal.add(""+a);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listTanggal);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        inputTanggal.setAdapter(adapter);

        listBln = new ArrayList<String>();
        listBln.add("Bln");
        for(int a=1;a<=12;a++){
            listBln.add(""+a);
        }
        ArrayAdapter<String> adapterBln = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listBln);
        adapterBln.setDropDownViewResource(R.layout.spinner_item);
        inputBln.setAdapter(adapterBln);

        listThn = new ArrayList<String>();
        listThn.add("Thn");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for(int a=year;a>=1900;a--){
            listThn.add(""+a);
        }
        ArrayAdapter<String> adapterThn = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listThn);
        adapterThn.setDropDownViewResource(R.layout.spinner_item);
        inputTahun.setAdapter(adapterThn);

        listJenis = new ArrayList<String>();
        listJenis.add("Laki-Laki");
        listJenis.add("Perempuan");
        ArrayAdapter<String> adapterJns = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listJenis);
        adapterJns.setDropDownViewResource(R.layout.spinner_item);
        inputGender.setAdapter(adapterJns);

        userService.profileUser(session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if(!object.getBoolean("error")){
                        JSONObject object1 = new JSONObject(object.getString("content"));
                        Log.d("content",object.getString("content"));
                        final JSONObject object2 = new JSONObject(object1.getString("profile"));
                        JSONArray arrProvinsi = new JSONArray(object1.getString("provinsi"));
                        JSONArray arrBank = new JSONArray(object1.getString("bank"));
                        RefModel refModel =  new RefModel();
                        for(int a=0;a<listTanggal.size();a++){
                            if(listTanggal.get(a).equals(object2.getString("tgl_lahir")))
                                inputTanggal.setSelection(a);
                        }
                        for(int a=0;a<listBln.size();a++){
                            if(listBln.get(a).equals(object2.getString("bln_lahir")))
                                inputBln.setSelection(a);
                        }
                        for(int a=0;a<listThn.size();a++){
                            if(listThn.get(a).equals(object2.getString("thn_lahir")))
                                inputTahun.setSelection(a);
                        }
                        if(object2.getString("jenis_kelamin").equals("P"))
                            inputGender.setSelection(1);
                        else
                            inputGender.setSelection(0);
                        provinsi.clear();
                        bank.clear();
                        refModel.setNama("pilih");
                        provinsi.add(refModel);
                        bank.add(refModel);
                        for(int a=0;a<arrProvinsi.length();a++){
                            JSONObject data = arrProvinsi.getJSONObject(a);
                            refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                            provinsi.add(refModel);
                        }
                        for(int a=0;a<arrBank.length();a++){
                            JSONObject data = arrBank.getJSONObject(a);
                            refModel =  new RefModel(data.getString("id"),data.getString("nama"));
                            bank.add(refModel);
                        }
                            spProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    Log.d("pos",""+position);
                                    if(position!=0){
                                    RefModel model = provinsi.get(position);
                                    provinsiId = model.getId();
                                    userService.getKab(provinsiId, new SendDataHelper.VolleyCallback() {
                                        @Override
                                        public String onSuccess(String result) {
                                            try {
                                                JSONObject resultData = new JSONObject(result);
                                                boolean error = resultData.getBoolean("error");
                                                kab.clear();
                                                RefModel refModel = new RefModel();
                                                refModel.setNama("Pilih");
                                                kab.add(refModel);
                                                if (!error) {
                                                    JSONArray dataArray = resultData.getJSONArray("content");
                                                    int sizeDataArray = dataArray.length();
                                                    for(int a=0;a<sizeDataArray;a++){
                                                        JSONObject data = dataArray.getJSONObject(a);
                                                        refModel = new RefModel(data.getString("id"), data.getString("nama"));
                                                        kab.add(refModel);
                                                    }


                                                    int index = 0;
                                                    for(int a=0;a<kab.size();a++){
                                                        RefModel model = kab.get(a);
                                                        if(object2.getString("kabupaten").equals(model.getId()))
                                                            index=a;
                                                    }
                                                    spinnerArrayAdapter3.notifyDataSetChanged();

                                                    spKab.setSelection(index);

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
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }

                            });

                        spKab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position>0){
                                    RefModel model = kab.get(position);
                                    kabID = model.getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        spBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position>0){
                                    RefModel model = bank.get(position);
                                    bankId = model.getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spinnerArrayAdapter3 = new ArrayAdapter<RefModel>(getApplicationContext(),
                                R.layout.spinner_item, kab);
                        spinnerArrayAdapter3.setDropDownViewResource( R.layout.spinner_item );
                        spKab.setAdapter(spinnerArrayAdapter3);
                        ArrayAdapter<RefModel> spinnerArrayAdapter = new ArrayAdapter<RefModel>(getApplicationContext(),
                                R.layout.spinner_item, provinsi);
                        spinnerArrayAdapter.setDropDownViewResource( R.layout.spinner_item );
                        spProvinsi.setAdapter(spinnerArrayAdapter);
                        ArrayAdapter<RefModel> spinnerArrayAdapter2 = new ArrayAdapter<RefModel>(getApplicationContext(),
                                R.layout.spinner_item, bank);
                        spinnerArrayAdapter2.setDropDownViewResource( R.layout.spinner_item );
                        spBank.setAdapter(spinnerArrayAdapter2);
                        int index= 0;
                        if(!object2.getString("propinsi").equals("null")){
                            for(int a=0;a<provinsi.size();a++){
                                RefModel model = provinsi.get(a);
                                if(object2.getString("propinsi").equals(model.getId()))
                                    index=a;
                            }
                            spProvinsi.setSelection(index);
                        }

                        if(!object2.getString("bank").equals("null")) {
                            index= 0;
                            for (int a = 0; a < bank.size(); a++) {
                                RefModel model = bank.get(a);
                                if (object2.getString("bank").equals(model.getId()))
                                    index = a;
                            }
                            spBank.setSelection(index);
                        }
                        inputEmail.setText(object2.getString("email"));
                        if(!object2.getString("nama_depan").equals("null"))
                            inputNamaDepan.setText(object2.getString("nama_depan"));
                        if(!object2.getString("nama_belakang").equals("null"))
                            inputNamaBelakang.setText(object2.getString("nama_belakang"));
                        if(!object2.getString("hp").equals("null"))
                            inputHp.setText(object2.getString("hp"));
                        if(!object2.getString("alamat").equals("null"))
                            inputAlamat.setText(object2.getString("alamat"));
                        if(!object2.getString("norek").equals("null"))
                            inputNoRekening.setText(object2.getString("norek"));

                        inputPassword.setText(object2.getString("password"));
                        inputKonfirmPassword.setText(object2.getString("password"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stopOnLoadingAnimation();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });

    }
    public void startOnLoadingAnimation(){
        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotation.setRepeatCount(Animation.INFINITE);
        rotationA = AnimationUtils.loadAnimation(this, R.anim.reverse_clockwise_animation_bap_loading);
        rotationA.setRepeatCount(Animation.INFINITE);
        rotationC = AnimationUtils.loadAnimation(this, R.anim.clockwise_animation_bap_loading);
        rotationC.setRepeatCount(Animation.INFINITE);
        lB.setAnimation(rotation);
        lA.setAnimation(rotationA);
        lP.setAnimation(rotationC);
        layoutLoading.setVisibility(View.VISIBLE);
    }
    public void stopOnLoadingAnimation(){
        layoutLoading.setVisibility(View.GONE);
        lB.clearAnimation();
        lA.clearAnimation();
        lP.clearAnimation();
    }
    public void onUbahClick(View v){
        if(inputPassword.getText().toString().trim().equals(inputKonfirmPassword.getText().toString().trim())){

            UserModel userModel = new UserModel();
            userModel.setNamaDepan(inputNamaDepan.getText().toString().trim());
            userModel.setNamaBelakang(inputNamaBelakang.getText().toString().trim());
            userModel.setHp(inputHp.getText().toString().trim());
            userModel.setEmail(inputEmail.getText().toString().trim());
            userModel.setId(session.getUserId());
            userModel.setAlamat(inputAlamat.getText().toString().trim());
            userModel.setPassword(inputPassword.getText().toString().trim());
            userModel.setKab(kabID);
            userModel.setProvinsi(provinsiId);
            userModel.setBank(bankId);
            userModel.setNorek(inputNoRekening.getText().toString().trim());
            userModel.setTgl(inputTanggal.getSelectedItem().toString().trim());
            userModel.setBln(inputBln.getSelectedItem().toString().trim());
            userModel.setThn(inputTahun.getSelectedItem().toString().trim());
            userModel.setjK(inputGender.getSelectedItem().toString().trim());

            userService.UpdateUser(userModel, new SendDataHelper.VolleyCallback() {
                @Override
                public String onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);

                        // check for error
                        if (!obj.getBoolean("error")) {
                            JSONObject dataUser = new JSONObject(obj.getString("content"));
                            session.setLogin(true);
                            session.setUserId(dataUser.getString("id"));
                            session.setUserNama(dataUser.getString("nama_depan"));
                            session.setUserNamaDepan(dataUser.getString("nama_depan"));
                            session.setUserNamaBelakang(dataUser.getString("nama_belakang"));
                            session.setUserEmail(dataUser.getString("email"));
                            session.setUserHandphone(dataUser.getString("hp"));
                            session.setUserAlamat(dataUser.getString("alamat"));
                            session.setUserPassword(dataUser.getString("password"));
                            session.setUserTgl(dataUser.getInt("tgl_lahir"));
                            session.setUserBln(dataUser.getInt("bln_lahir"));
                            session.setUserThn(dataUser.getInt("thn_lahir"));
                            session.setUserJk(dataUser.getString("jenis_kelamin"));
                            Toast.makeText(getApplicationContext(),"Berhasil Mengubah Profil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("content"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                    }
                    return null;
                }

                @Override
                public String onError(VolleyError result) {
                    return null;
                }
            });
        }
        else
            Toast.makeText(getApplicationContext(),"Password tidak sama", Toast.LENGTH_SHORT).show();

    }
    public void onKeluarClick(View v){
        FirebaseAuth.getInstance().signOut();
        session.setLogin(false);
        session.setFID("");
        session.setToken("");
        session.setUserId("");
        session.setUserNama("");
        session.setUserNamaDepan("");
        session.setUserNamaBelakang("");
        session.setUserEmail("");
        session.setUserHandphone("");
        session.setUserAlamat("");
        session.setUserPassword("");
        session.setBengkelAktif(false);
        session.setBengkelId("");
        session.setBengkelUserId("");
        session.setBengkelIdOrder("");
        session.setOrderAktif(false);
        session.setOrderId("");
        session.setOrderStatus("");
        finish();

    }
    public void showDoneAlert(){
        dialogError = new Dialog(this);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);

        TextView txtMessage = (TextView) dialogError.findViewById(R.id.txtMessage);
        txtMessage.setText("Anda yakin ingin keluar?");
        Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        btnCobaLagi.setText("Keluar");
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);
        btnBatal.setText("Batal");

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeluarClick(v);
                dialogError.dismiss();
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }
    @Override
    public void onResume(){
        super.onResume();
        locationUser = new LocationUser(this);
        locationUser.start();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}

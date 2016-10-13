package com.beliautopart.beliautopart.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.RefModel;
import com.beliautopart.beliautopart.model.UserModel;
import com.beliautopart.beliautopart.webservices.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by brandon on 12/05/16.
 */
public class RegisterFragment extends Fragment {
    private EditText inputEmail;
    private EditText inputPassword;
    private UserService userService;
    private RelativeLayout btnRegister;
    private EditText inputNamaDepan;
    private EditText inputNamaBelakang;
    private EditText inputHp;
    private EditText inputRePassword;
    private SessionManager session;
    private Spinner inputGender;
    private Spinner inputTanggal;
    private Spinner inputBln;
    private Spinner inputTahun;
    private ArrayList<String> listTanggal;
    private ArrayList<String> listBln;
    private ArrayList<String> listThn;
    private ArrayList<String> listJenis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.register_fragment, container, false);

        session = new SessionManager(getContext());
        if(session.isLoggedIn()){
            Toast.makeText(getContext(),"Anda Sudah Login",Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        inputEmail = (EditText) v.findViewById(R.id.inputEmail);
        inputPassword = (EditText) v.findViewById(R.id.inputKomplain);
        inputNamaDepan = (EditText) v.findViewById(R.id.inputNamaDepan);
        inputNamaBelakang = (EditText) v.findViewById(R.id.inputNamaBelakang);
        inputHp = (EditText) v.findViewById(R.id.inputHp);
        inputRePassword = (EditText) v.findViewById(R.id.inputRePassword);
        btnRegister = (RelativeLayout) v.findViewById(R.id.lbtnregister);
        inputTanggal = (Spinner) v.findViewById(R.id.inputTgl);
        inputBln = (Spinner) v.findViewById(R.id.inputBln);
        inputTahun = (Spinner) v.findViewById(R.id.inputTahun);
        inputGender = (Spinner) v.findViewById(R.id.inputGender);

        listTanggal = new ArrayList<String>();
        listTanggal.add("Tgl");
        for(int a=1;a<=31;a++){
            listTanggal.add(""+a);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, listTanggal);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        inputTanggal.setAdapter(adapter);

        listBln = new ArrayList<String>();
        listBln.add("Bln");
        for(int a=1;a<=12;a++){
            listBln.add(""+a);
        }
        ArrayAdapter<String> adapterBln = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, listBln);
        adapterBln.setDropDownViewResource(R.layout.spinner_item);
        inputBln.setAdapter(adapterBln);

        listThn = new ArrayList<String>();
        listThn.add("Thn");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for(int a=year;a>=1900;a--){
            listThn.add(""+a);
        }
        ArrayAdapter<String> adapterThn = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, listThn);
        adapterThn.setDropDownViewResource(R.layout.spinner_item);
        inputTahun.setAdapter(adapterThn);

        listJenis = new ArrayList<String>();
        listJenis.add("Laki-Laki");
        listJenis.add("Perempuan");
        ArrayAdapter<String> adapterJns = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, listJenis);
        adapterJns.setDropDownViewResource(R.layout.spinner_item);
        inputGender.setAdapter(adapterJns);




        userService = new UserService(getContext());
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputEmail.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Email belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputPassword.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Password belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputNamaDepan.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Nama Depan belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputNamaBelakang.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Nama belakang belum diisi", Toast.LENGTH_SHORT).show();
                else if (inputHp.getText().toString() == "")
                    Toast.makeText(getContext(), "No Hp belum diisi", Toast.LENGTH_SHORT).show();
                else if (!inputPassword.getText().toString().equals(inputRePassword.getText().toString()))
                    Toast.makeText(getContext(), "Password Tidak Sama", Toast.LENGTH_SHORT).show();
                else if (inputTanggal.getSelectedItem().toString().equals("Tgl"))
                    Toast.makeText(getContext(), "Tanggal Harus diisi", Toast.LENGTH_SHORT).show();
                else if (inputBln.getSelectedItem().toString().equals("Bln"))
                    Toast.makeText(getContext(), "Bulan Harus diisi", Toast.LENGTH_SHORT).show();
                else if (inputTahun.getSelectedItem().toString().equals("Thn"))
                    Toast.makeText(getContext(), "Tahun Harus diisi", Toast.LENGTH_SHORT).show();
                else
                    onRegister();
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return v;
    }

    private void onRegister() {
        String namaDepan = inputNamaDepan.getText().toString().trim();
        String namaBelakang = inputNamaBelakang.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String hp = inputHp.getText().toString();
        String password = inputPassword.getText().toString().trim();
        UserModel user = new UserModel("0", namaDepan, namaBelakang, email, ""+hp, password);
        user.setTgl(inputTanggal.getSelectedItem().toString().trim());
        user.setBln(inputBln.getSelectedItem().toString().trim());
        user.setThn(inputTahun.getSelectedItem().toString().trim());
        user.setjK(inputGender.getSelectedItem().toString().trim());
        user.setFID(session.getFID());
        userService.RegisterUser(user, new SendDataHelper.VolleyCallback() {
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
                        session.setUserTgl(dataUser.getInt("tgl_lahir"));
                        session.setUserBln(dataUser.getInt("bln_lahir"));
                        session.setUserThn(dataUser.getInt("thn_lahir"));
                        session.setUserJk(dataUser.getString("jenis_kelamin"));
                        Toast.makeText(getContext(),"Pendaftaran Berhasil", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), obj.getString("content"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                }

                return result;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }

}

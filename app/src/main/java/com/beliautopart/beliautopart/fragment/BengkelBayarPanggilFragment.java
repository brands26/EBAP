package com.beliautopart.beliautopart.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.BengkelActivity;
import com.beliautopart.beliautopart.activity.ChatActivity;
import com.beliautopart.beliautopart.helper.Logic;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.BankModel;
import com.beliautopart.beliautopart.webservices.BengkelService;
import com.beliautopart.beliautopart.webservices.OrderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon Pratama on 12/06/2016.
 */
public class BengkelBayarPanggilFragment extends Fragment {
    private View v;
    private Context context;
    private SessionManager session;
    private String content;
    private LinearLayout vewRekening;
    private OrderService order;
    private TextView txtNamaBengkel;
    private TextView txtJarak;
    private CardView btnBayar;
    private BengkelService bengkelService;
    private RelativeLayout btnBatal;
    private RelativeLayout btnChat;
    private String namaBengkel;
    private Dialog dialogError;
    private TextView txtNominal;
    private Logic logic;
    private boolean salon = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        salon = ((BengkelActivity)getActivity()).getSalon();

        v = inflater.inflate(R.layout.fragment_bengkel_bayar_panggil, container, false);
        context = getContext();
        order = new OrderService(context);
        session = new SessionManager(context);
        bengkelService = new BengkelService(context);
        logic = new Logic();
        ImageView i = (ImageView) getActivity().findViewById(R.id.imgStep);
        i.setImageResource(R.drawable.stepbengkel1);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuCondensed-Regular.ttf");
        TextView tv = (TextView) v.findViewById(R.id.txtTitle);
        tv.setTypeface(tf);

        TextView txtJobID = (TextView) v.findViewById(R.id.txtJobID);
        txtJobID.setText(session.getBengkelId());
        content = getArguments().getString("content");
        vewRekening = (LinearLayout) v.findViewById(R.id.linierLayoutRekening);
        txtNamaBengkel  = (TextView) v.findViewById(R.id.txtNamaBengkel);
        txtJarak  = (TextView) v.findViewById(R.id.txtJarak);
        txtNominal  = (TextView) v.findViewById(R.id.txtNominalPembayaran);
        btnBayar  = (CardView) v.findViewById(R.id.card_view_transfer);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogKonfirmasi();
            }
        });
        btnBatal = (RelativeLayout) v.findViewById(R.id.lbtnSimpan);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoneAlert();
            }
        });
        btnChat = (RelativeLayout) v.findViewById(R.id.lbtnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("nama",namaBengkel);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });
        setUp(content);
        return v;
    }
    public void setUp(String content){
        try {
            JSONObject data = new JSONObject(content);
            namaBengkel= data.getString("nama");
            session.setBengkelUserId(data.getString("uid"));
            txtNamaBengkel.setText(data.getString("nama"));
            txtNominal.setText("Rp" +logic.thousand(data.getString("biaya_panggil")));
            String jarak = data.getString("jarak");
            if(!jarak.equals("0") && !jarak.equals("") && jarak!=null)
                txtJarak.setText(jarak.substring(0,jarak.length()-10)+"km");
            else if(jarak.equalsIgnoreCase("0"))
                txtJarak.setText("0.00 km");
            Log.d("bank ",data.getString("bank_list"));
            setrekening(data.getString("bank_list"));
            if(salon){
                TextView textView68  =(TextView) v.findViewById(R.id.textView68);
                textView68.setText("Nama Salon:");
                TextView textView73  =(TextView) v.findViewById(R.id.textView73);
                textView73.setText("Menunggu pembayaran biaya pemanggilan salon.");
                TextView textView74  =(TextView) v.findViewById(R.id.textView74);
                textView74.setText("Biaya Pemanggilan Salon:");
                TextView textView75  =(TextView) v.findViewById(R.id.textView75);
                textView75.setText("Anda telah menyetujui bahwa Anda akan akan dibantu oleh salon tersebut, silakan lakukan pembayaran biaya panggil salon sebagai berikut:");
                TextView textView77  =(TextView) v.findViewById(R.id.textView77);
                textView77.setText("Jika Anda ingin melakukan tanya jawab dengan salon yang merespon request Anda, tekan tombol CHAT.\nJika Anda ingin membatalkan request, tekan tombol BATAL.");
            }
            ((BengkelActivity)getActivity()).stopOnLoadingAnimation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setrekening(String listRekening){
        JSONArray arrData = null;
        try {
            arrData = new JSONArray(listRekening);
            Log.d("arrData ",arrData.toString());
            for(int i = 0; i < arrData.length(); i++)
            {
                JSONObject data = arrData.getJSONObject(i);
                addVewRekening( i+1,data.getString("nama_bank")+" "+data.getString("kantor_cabang"),data.getString("norek"),data.getString("nama_rek"),data.getString("kode") );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addVewRekening(int no,String nama, String noRek, String pt,String kodeBank){

        View child =  getActivity().getLayoutInflater().inflate(R.layout.row_order_rekening, null);
        TextView txtNomer = (TextView) child.findViewById(R.id.txtNomer);
        TextView txtNamaBank = (TextView) child.findViewById(R.id.txtNamaBank);
        TextView txtNoRekening = (TextView) child.findViewById(R.id.txtNoRekening);
        TextView txtPt = (TextView) child.findViewById(R.id.txtPt);
        TextView txtKOdeTransfer = (TextView) child.findViewById(R.id.txtKOdeTransfer);
        txtNomer.setText(""+no);
        txtNamaBank.setText(nama);
        txtNoRekening.setText("No. Rekening: " +noRek);
        txtPt.setText("a/n. "+pt);
        txtKOdeTransfer.setText("Kode Transfer Antarbank: "+kodeBank);
        vewRekening.addView(child);
    }
    private void onDialogKonfirmasi() {
        final String[] norek = {""};
        final int[] kodeBank = {0};
        final List<BankModel> listBank = new ArrayList<BankModel>();
        final List<BankModel> bankPerusahaan = new ArrayList<BankModel>();
        order.getBank(new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {
                        JSONObject content = new JSONObject(obj.getString("content"));

                        JSONArray arrayBankPerusahaan = new JSONArray(content.getString("bank_perusahaan"));
                        JSONArray arrayListPerusahaan = new JSONArray(content.getString("bank_list"));

                        for (int i = 0; i < arrayBankPerusahaan.length(); i++) {
                            JSONObject itemData = arrayBankPerusahaan.getJSONObject(i);
                            BankModel bankModel = new BankModel(itemData.getInt("id_bank"), itemData.getString("nama_bank"));
                            bankPerusahaan.add(bankModel);

                        }
                        for (int i = 0; i < arrayListPerusahaan.length(); i++) {
                            JSONObject listData = arrayListPerusahaan.getJSONObject(i);
                            listBank.add(new BankModel(listData.getInt("id"), listData.getString("nama")));
                        }

                        JSONObject profileArray = new JSONObject(content.getString("profile"));
                        JSONObject profile = new JSONObject(profileArray.getString("profile"));

                        Log.d("content", profile.getString("bank"));


                        kodeBank[0] = profile.getInt("bank");
                        norek[0] = profile.getString("norek");



                    }
                } catch (JSONException e) {
                }

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                dialog.setContentView(R.layout.dialog_konfirm_pembayaran);
                dialog.setCancelable(true);
                final Spinner spBankPerusahaan = (Spinner) dialog.findViewById(R.id.spBankPerusaan);
                final Spinner spBankList = (Spinner) dialog.findViewById(R.id.spBankList);
                final EditText inputRekening = (EditText) dialog.findViewById(R.id.inputNomorRekening);
                Button btnKonfirmasi = (Button) dialog.findViewById(R.id.btnKonfirmasi);
                Button btnBatal = (Button) dialog.findViewById(R.id.btnSimpan);

                ArrayAdapter<BankModel> dataAdapter = new ArrayAdapter<BankModel>(getContext(),
                        android.R.layout.simple_spinner_item, bankPerusahaan);
                spBankPerusahaan.setAdapter(dataAdapter);
                ArrayAdapter<BankModel> listAdapter = new ArrayAdapter<BankModel>(getContext(),
                        android.R.layout.simple_spinner_item, listBank);
                spBankList.setAdapter(listAdapter);
                for(int a=0;a<listBank.size();a++){
                    BankModel bank = listBank.get(a);
                    if(kodeBank[0]==bank.getId()){
                        spBankList.setSelection(a);
                        inputRekening.setText(norek[0]);
                    }
                }
                btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BengkelActivity)getActivity()).startOnLoadingAnimation();
                        int bankpengirim;
                        int bankdikirim;
                        BankModel bank1 = listBank.get(spBankList.getSelectedItemPosition());
                        bankpengirim = bank1.getId();
                        BankModel bank2 = bankPerusahaan.get(spBankPerusahaan.getSelectedItemPosition());
                        bankdikirim = bank2.getId();
                        bengkelService.setPembayaranJob(session.getBengkelId(), session.getUserId(), session.getBengkelUserId(),bankpengirim,bankdikirim,inputRekening.getText().toString().trim(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                dialog.dismiss();
                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                    }
                });
                btnBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }
    public void showDoneAlert(){
        dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogError.setContentView(R.layout.dialog_alert);
        dialogError.setCancelable(false);

        TextView txtMessage = (TextView) dialogError.findViewById(R.id.txtMessage);
        txtMessage.setText("Anda yakin ingin membatalkan sesi ini?");
        Button btnCobaLagi = (Button) dialogError.findViewById(R.id.btnCobaLagi);
        btnCobaLagi.setText("Batalkan");
        Button btnBatal = (Button) dialogError.findViewById(R.id.btnBatal);
        btnBatal.setText("Tidak");

        btnCobaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bengkelService.setPembatalanJob(session.getBengkelId(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        dialogError.dismiss();
                        getActivity().finish();
                        Toast.makeText(context,"Telah dibatalkan",Toast.LENGTH_SHORT).show();
                        session.setBengkelAktif(false);
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        return null;
                    }
                });
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
}

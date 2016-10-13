package com.beliautopart.beliautopart.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.app.AppController;
import com.beliautopart.beliautopart.fragment.BayarFragment;
import com.beliautopart.beliautopart.fragment.KirimFragment;
import com.beliautopart.beliautopart.fragment.OrderFragmentOld;
import com.beliautopart.beliautopart.fragment.MapFragment;
import com.beliautopart.beliautopart.fragment.StepCart;
import com.beliautopart.beliautopart.helper.SendDataHelper;
import com.beliautopart.beliautopart.helper.SessionManager;
import com.beliautopart.beliautopart.model.BankModel;
import com.beliautopart.beliautopart.model.ItemProduk;
import com.beliautopart.beliautopart.model.OrderModel;
import com.beliautopart.beliautopart.utils.GPSTracker;
import com.beliautopart.beliautopart.webservices.OrderService;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private GPSTracker gps;
    private CheckoutActivity context;
    private int positionTab = 0;
    private StepCart stepCart;
    private MapFragment mapFragment;
    private SessionManager session;
    private OrderService order;
    private String alamat;
    private double latLokasi;
    private double lngLokasi;
    private OrderFragmentOld orderFragmentOld;
    private ImageButton btnNext;
    private BayarFragment bayarFragment;
    private KirimFragment kirimFragment;
    private String kodeKurir;
    private ImageButton btnBack;
    double currentLatitude, currentLongitude;
    private Location currentLocation;
    private ProgressDialog pDialog;
    private TextView txtbtnBack;
    private TextView txtbtnNext;
    private ImageButton btnBatal;
    private TextView txtbtnBatal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        context = this;
        session = new SessionManager(context);
        order = new OrderService(context);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                positionTab = position;
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        onDialogMapInfo();
                        gps = new GPSTracker(getApplicationContext());
                        // check if GPS enabled
                        if (gps.canGetLocation()) {
                        } else {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                            // Setting Dialog Title
                            alertDialog.setTitle("Peringatan GPS");

                            // Setting Dialog Message
                            alertDialog.setMessage("GPS tidak aktif. Mengaktifkan seakarang?");

                            // On pressing Settings button
                            alertDialog.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    context.startActivity(intent);
                                }
                            });

                            // on pressing cancel button
                            alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            // Showing Alert Message
                            alertDialog.show();
                        }
                        break;
                    case 2:
                        onLoadingStatus();
                        break;
                    case 3:
                        onLoadingPembayaran();
                        break;
                    case 4:
                        onLoadingbarangDiantar();
                        break;

                }
                for (int a = 0; a <= position; a++) {
                    View v = tabLayout.getTabAt(a).getCustomView();
                    TextView tabCount = (TextView) v.findViewById(R.id.tabCount);
                    TextView tabText = (TextView) v.findViewById(R.id.tabText);
                    tabCount.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle_bg));
                    tabText.setTextColor(getResources().getColor(R.color.colorPrimary));
                }


            }
        });
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnBatal = (ImageButton) findViewById(R.id.btnSimpan);
        txtbtnBatal = (TextView) findViewById(R.id.txtbtnBatal);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("perhatian");
                alertDialog.setMessage("Anda Yakin ingin membatalkannya?");
                alertDialog.setPositiveButton("yakin", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        order.setBatal(session.getOrderId(),session.getUserId(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                session.setOrderAktif(false);
                                session.setOrderStatus("");
                                session.setOrderId("");
                                finish();
                                Toast.makeText(getApplicationContext(),"Order telah dibatalkan",Toast.LENGTH_LONG).show();
                                return null;
                            }

                            @Override
                            public String onError(VolleyError result) {
                                return null;
                            }
                        });
                        dialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            }
        });
        btnNext = (ImageButton) findViewById(R.id.btnnext);
        txtbtnNext = (TextView) findViewById(R.id.txtbtnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionTab == 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("perhatian");
                    alertDialog.setMessage("Anda Yakin?");
                    alertDialog.setPositiveButton("yakin", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            viewPager.setCurrentItem(1, true);
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setNegativeButton("batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                } else if (positionTab == 1) {
                    onCartSave();
                } else if (positionTab == 2) {
                    order.setPembayaran(session.getOrderId(), session.getUserId(), 1, new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {
                            viewPager.setCurrentItem(3);
                            return null;
                        }

                        @Override
                        public String onError(VolleyError result) {
                            return null;
                        }
                    });

                } else if (positionTab == 3) {
                    onDialogKonfirmasi();
                } else if (positionTab == 4) {
                    //if (!txtbtnNext.getText().equals("Done"))
                        //onDialogbarangDiterima();
                   /// else
                   //     finish();

                }
            }
        });
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String orderId = extras.getString("status");
                order.getOrderStatus(orderId, 1, new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);

                            // check for error
                            if (!obj.getBoolean("error")) {
                                session.setOrderStatus(obj.getString("content"));
                                Log.d("statusorder = ", obj.getString("content"));
                                if (obj.getString("content").equals("10"))
                                    viewPager.setCurrentItem(2);
                                else if (obj.getString("content").equals("19")) {
                                    finish();
                                    session.setOrderAktif(false);
                                    session.setOrderId(null);
                                    session.setOrderStatus(null);
                                    Toast.makeText(context, "Order telah dibatalkan", Toast.LENGTH_LONG).show();
                                } else if (obj.getString("content").equals("31")) {
                                    viewPager.setCurrentItem(2);
                                } else if (obj.getString("content").equals("11")) {
                                    viewPager.setCurrentItem(3);
                                } else if (obj.getString("content").equals("12")) {
                                    viewPager.setCurrentItem(3);
                                    onLoadingVerifikasi();
                                } else if (obj.getString("content").equals("33")) {
                                    viewPager.setCurrentItem(4);
                                } else if (obj.getString("content").equals("34")) {
                                    viewPager.setCurrentItem(4);
                                    onWaitLoadingDiantar();
                                } else if (obj.getString("content").equals("13")) {
                                    viewPager.setCurrentItem(4);
                                }
                            } else {
                                Toast.makeText(context, obj.getString("content"), Toast.LENGTH_LONG).show();
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
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

    }

    private void setupTabIcons() {

        View tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabShopingCartCountOne = (TextView) tabOne.findViewById(R.id.tabCount);
        TextView tabhopingCartOne = (TextView) tabOne.findViewById(R.id.tabText);
        tabShopingCartCountOne.setText("1");
        tabhopingCartOne.setText("Shopping Chart");
        tabShopingCartCountOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_circle_bg));
        tabhopingCartOne.setTextColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.getTabAt(0).setCustomView(tabOne);
        View tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabShopingCartCountTwo = (TextView) tabTwo.findViewById(R.id.tabCount);
        TextView tabhopingCartTwo = (TextView) tabTwo.findViewById(R.id.tabText);
        tabShopingCartCountTwo.setText("2");
        tabhopingCartTwo.setText("Lokasi Kirim");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
        View tabthree = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabShopingCartCountthree = (TextView) tabthree.findViewById(R.id.tabCount);
        TextView tabhopingCartthree = (TextView) tabthree.findViewById(R.id.tabText);
        tabShopingCartCountthree.setText("3");
        tabhopingCartthree.setText("Order");
        tabLayout.getTabAt(2).setCustomView(tabthree);
        View tab4 = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabShopingCartCount4 = (TextView) tab4.findViewById(R.id.tabCount);
        TextView tabhopingCart4 = (TextView) tab4.findViewById(R.id.tabText);
        tabShopingCartCount4.setText("4");
        tabhopingCart4.setText("Bayar");
        tabLayout.getTabAt(3).setCustomView(tab4);
        View tab5 = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabShopingCartCount5 = (TextView) tab5.findViewById(R.id.tabCount);
        TextView tabhopingCart5 = (TextView) tab5.findViewById(R.id.tabText);
        tabShopingCartCount5.setText("5");
        View divider = tab5.findViewById(R.id.divider);
        divider.setVisibility(View.GONE);
        tabhopingCart5.setText("Kirim");
        tabLayout.getTabAt(4).setCustomView(tab5);
    }

    private void setupViewPager(ViewPager viewPager) {
        stepCart = new StepCart();
        mapFragment = new MapFragment();
        orderFragmentOld = new OrderFragmentOld();
        bayarFragment = new BayarFragment();
        kirimFragment = new KirimFragment();
        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return stepCart;
                    case 1:
                        return mapFragment;
                    case 2:
                        return orderFragmentOld;
                    case 3:
                        return bayarFragment;
                    case 4:
                        return kirimFragment;
                    default:
                        return null;
                }

            }
        };
        viewPager.setAdapter(adapter);
    }

    public void onDialogMapInfo() {
        AlertDialog alertDialog = new AlertDialog.Builder(CheckoutActivity.this).create();
        alertDialog.setTitle("Pilih Lokasi kirim");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Masukkan lokasi anda dengan menggeser marker pada peta.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "mengerti", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                dialog.dismiss();
                FindLocation();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void onCartSave() {
        AlertDialog.Builder MapDialog = new AlertDialog.Builder(context);
        MapDialog.setTitle("Apakah benar Lokasi Anda ?");
        final LatLng latLng = mapFragment.getMapLocation();
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses = null;
        String cityName = null;
        String stateName = null;
        String countryName = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);
            MapDialog.setMessage("" + cityName + " " + stateName + " " + countryName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String finalCityName = cityName;
        final String finalStateName = stateName;
        final String finalCountryName = countryName;
        MapDialog.setPositiveButton("benar", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                alamat = finalCityName + "," + finalStateName + "," + finalCountryName;
                latLokasi = latLng.latitude;
                lngLokasi = latLng.longitude;
                final List<ItemProduk> cart = session.getCart();
                int size = session.getCart().size();
                JSONArray cartArray = new JSONArray();
                for (int a = 0; a < size; a++) {
                    ItemProduk item = cart.get(a);
                    JSONObject dataCart = new JSONObject();
                    try {
                        dataCart.put("id", item.getIdItem());
                        dataCart.put("jumlah", item.getJumlah());
                        dataCart.put("harga", item.getHarga());
                        cartArray.put(dataCart);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                OrderModel orderModel = new OrderModel(session.getUserId(), "1", latLokasi, lngLokasi, alamat,session.getBengkelId());
                order.setOrder(orderModel, cartArray.toString(), new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            Log.d("error :", obj.getString("error"));
                            // check for error
                            if (!obj.getBoolean("error")) {
                                JSONObject dataOrder = new JSONObject(obj.getString("content"));
                                Log.d("result:", result);
                                session.setOrderAktif(true);
                                session.setOrderId(dataOrder.getString("idorder"));
                                Log.d("dataorder= idOrder=", dataOrder.getString("idorder"));
                                session.setOrderStatus(dataOrder.getString("statusorder"));
                                session.emptyCart();
                                dialog.dismiss();
                                btnNext.setVisibility(View.GONE);
                                txtbtnNext.setText("");
                                viewPager.setCurrentItem(2, true);
                            } else {
                                Toast.makeText(context, obj.getString("content"), Toast.LENGTH_LONG).show();
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
        });
        MapDialog.setNegativeButton("salah", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        MapDialog.show();

    }

    public void onLoadingStatus() {
        btnNext.setVisibility(View.GONE);
        txtbtnNext.setText("");
        order.getOrderStatus(session.getOrderId(), 0, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        if (obj.getString("content").equals("31")) {
                            getOrderDetail();
                        } else {
                            onLoadingStatus();
                        }
                    }
                } catch (JSONException e) {
                }
                return result;
            }

            @Override
            public String onError(VolleyError result) {
                onLoadingStatus();return null;
            }
        });
    }

    public void onWaitLoadingVerifikasi() {

        btnNext.setVisibility(View.GONE);
        txtbtnNext.setText("");
        order.getOrderStatus(session.getOrderId(), 0, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        if (obj.getString("content").equals("33")) {

                            viewPager.setCurrentItem(4);
                        } else {
                            onWaitLoadingVerifikasi();
                        }
                    }
                } catch (JSONException e) {
                }
                return result;
            }

            @Override
            public String onError(VolleyError result) {
                onWaitLoadingVerifikasi();
                return null;
            }
        });
    }

    public void onWaitLoadingDiantar() {
        order.getOrderStatus(session.getOrderId(), 0, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        if (obj.getString("content").equals("34")) {
                            onLoadingbarangKurir();
                        } else if (obj.getString("content").equals("13")) {
                            kirimFragment.setBarangDiterima();
                        } else {
                            onWaitLoadingDiantar();
                        }
                    }
                } catch (JSONException e) {
                }
                return result;
            }

            @Override
            public String onError(VolleyError result) {
                onWaitLoadingDiantar();
                return null;
            }
        });
    }

    public void onWaitLoadingDiterima() {

        btnNext.setVisibility(View.GONE);
        txtbtnNext.setText("");
        order.getOrderStatus(session.getOrderId(), 0, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        if (obj.getString("content").equals("13")) {
                            onLoadingbarangDiterima();
                        } else {
                            onWaitLoadingDiantar();
                        }
                    }
                } catch (JSONException e) {
                }
                return result;
            }

            @Override
            public String onError(VolleyError result) {
                onWaitLoadingDiantar();
                return null;
            }
        });
    }

    private void onLoadingPembayaran() {
        btnNext.setVisibility(View.GONE);
        txtbtnNext.setText("");
        order.getOrderDetail(session.getOrderId(), 0, new SendDataHelper.VolleyCallback() {

            @Override
            public String onSuccess(String result) {
                String noId = null, listbarang = null,nominal=null, listRekening = null;
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {

                        JSONObject data = new JSONObject(obj.getString("content"));
                        noId = data.getString("nomor");
                        listbarang = data.getString("listbarang");
                        nominal = data.getString("total_harga");
                        bayarFragment.setStatus(noId, listbarang,nominal);

                    }
                } catch (JSONException e) {
                }
                order.getRekening(new SendDataHelper.VolleyCallback() {
                    @Override
                    public String onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (!obj.getBoolean("error")) {
                                btnNext.setVisibility(View.VISIBLE);
                                txtbtnNext.setText("Konfirm Bayar");
                                bayarFragment.setrekening(obj.getString("content"));

                            }
                        } catch (JSONException e) {
                        }
                        getTimePembayaran();
                        bayarFragment.stopAnimation();
                        return null;
                    }

                    @Override
                    public String onError(VolleyError result) {
                        getTimePembayaran();
                        return null;
                    }
                });
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }

    public void onLoadingVerifikasi() {
        btnBatal.setVisibility(View.GONE);
        txtbtnBatal.setText("");
        btnNext.setVisibility(View.GONE);
        txtbtnNext.setText("");
        order.getDetailVerifikasi(session.getOrderId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        bayarFragment.setVerifikasi(data.getString("bankTujuan"), data.getString("totalbiaya"), data.getString("bankPengirim"), data.getString("noRek"));
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
        onWaitLoadingVerifikasi();
    }


    private void onLoadingbarangDiantar() {

        btnNext.setVisibility(View.GONE);
        txtbtnNext.setText("");
        order.getBarangDiantar(session.getOrderId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        kirimFragment.setDiantar(data.getString("nomor"), data.getString("listBarang"));
                        kirimFragment.stopLoading();
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
        onWaitLoadingDiantar();
    }

    private void onLoadingbarangKurir() {
        order.getDetailOrderKurir(session.getOrderId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        kodeKurir = data.getString("kurir_kode");
                        JSONObject kurir = new JSONObject(data.getString("kurir"));
                        kirimFragment.setKurir(data.getString("nomor"), data.getString("listBarang"), kurir.getString("nama_depan") + kurir.getString("nama_belakang"), kurir.getString("hp"), kurir.getString("foto"));
                        btnNext.setVisibility(View.VISIBLE);
                        txtbtnNext.setText("Barang Diterima");
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
        onWaitLoadingDiterima();
    }

    private void onLoadingbarangDiterima() {
        order.getDetailOrderKurir(session.getOrderId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        kirimFragment.setBarangDiterima(data.getString("nomor"));
                        txtbtnNext.setText("Done");
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

    private void getTimePembayaran() {
        order.getTime(session.getUserId(), new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        bayarFragment.setTimmer(obj.getString("content"));
                        getTimePembayaran();
                    }
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                getTimePembayaran();
                return null;
            }
        });
    }

    private void getOrderDetail() {
        order.getOrderDetail(session.getOrderId(), 1, new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (!obj.getBoolean("error")) {
                        JSONObject data = new JSONObject(obj.getString("content"));
                        orderFragmentOld.setStatus(
                                data.getString("nomor"), data.getString("warehouse_name"),
                                data.getString("warehouse_alamat"), data.getString("warehouse_jarak"),
                                data.getDouble("lat"), data.getDouble("lng"),
                                data.getDouble("warehouse_lat"), data.getDouble("warehouse_lng"), data.getString("listbarang")
                        );
                        orderFragmentOld.stopAnimation();
                        btnBatal.setVisibility(View.VISIBLE);
                        txtbtnBatal.setText("Batal");
                        btnNext.setVisibility(View.VISIBLE);
                        txtbtnNext.setText("Bayar");

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

    private void onDialogKonfirmasi() {
        order.getBank(new SendDataHelper.VolleyCallback() {
            @Override
            public String onSuccess(String result) {
                final List<BankModel> listBank = new ArrayList<BankModel>();
                final List<BankModel> bankPerusahaan = new ArrayList<BankModel>();
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

                ArrayAdapter<BankModel> dataAdapter = new ArrayAdapter<BankModel>(getBaseContext(),
                        android.R.layout.simple_spinner_item, bankPerusahaan);
                spBankPerusahaan.setAdapter(dataAdapter);
                ArrayAdapter<BankModel> listAdapter = new ArrayAdapter<BankModel>(getBaseContext(),
                        android.R.layout.simple_spinner_item, listBank);
                spBankList.setAdapter(listAdapter);
                btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int bankpengirim;
                        int bankdikirim;
                        BankModel bank1 = listBank.get(spBankList.getSelectedItemPosition());
                        bankpengirim = bank1.getId();
                        BankModel bank2 = bankPerusahaan.get(spBankPerusahaan.getSelectedItemPosition());
                        bankdikirim = bank2.getId();
                        order.setKonfirmasiPembayaran(session.getOrderId(), session.getUserId(), bankpengirim, bankdikirim, inputRekening.getText().toString().trim(), new SendDataHelper.VolleyCallback() {
                            @Override
                            public String onSuccess(String result) {
                                onLoadingVerifikasi();
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
                dialog.show();
                return null;
            }

            @Override
            public String onError(VolleyError result) {
                return null;
            }
        });
    }
/*
    private void onDialogbarangDiterima() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(R.layout.dialog_barang_diterima);
        dialog.setCancelable(true);
        final EditText inputKodeKurir = (EditText) dialog.findViewById(R.id.inputKodeKurir);
        Button btnKonfirmasi = (Button) dialog.findViewById(R.id.btnKonfirmasi);
        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputKodeKurir.getText().toString().trim().equals(kodeKurir)) {
                    order.setBarangDiterima(session.getOrderId(), session.getUserId(), idKurir, new SendDataHelper.VolleyCallback() {
                        @Override
                        public String onSuccess(String result) {
                            dialog.dismiss();
                            kirimFragment.setBarangDiterima();
                            txtbtnNext.setText("Done");
                            return null;
                        }

                        @Override
                        public String onError(VolleyError result) {
                            return null;
                        }
                    });
                } else {
                    Toast.makeText(context, "kode kurir salah", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }
*/
    public void FindLocation() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Mendapatkan kordinat saat ini");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }


    void updateLocation(Location location) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        if(currentLocation==null){
            currentLocation = location;
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();
            Log.d("location saat ini",""+currentLocation);
            mapFragment.setMapLocation(currentLatitude,currentLongitude);
        }

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests("volley");
    }

}

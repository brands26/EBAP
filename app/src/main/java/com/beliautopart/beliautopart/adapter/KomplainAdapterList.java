package com.beliautopart.beliautopart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.activity.KomplainActivity;
import com.beliautopart.beliautopart.activity.KomplainDetailActivity;
import com.beliautopart.beliautopart.app.AppController;
import com.beliautopart.beliautopart.model.ItemProduk;
import com.beliautopart.beliautopart.model.KomplainModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Brandon Pratama on 11/06/2016.
 */
public class KomplainAdapterList  extends RecyclerView.Adapter<KomplainAdapterList.MyViewHolder> {

    private final Activity context;
    private List<KomplainModel> itemList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNomor;
        private final TextView txtTanggal;
        private final TextView txtPesan;
        private final ImageButton btnDetail;

        public MyViewHolder(View view) {
            super(view);
            txtNomor = (TextView) view.findViewById(R.id.txtNomor);
            txtTanggal = (TextView) view.findViewById(R.id.txtTanggal);
            txtPesan = (TextView) view.findViewById(R.id.txtPesan);
            btnDetail = (ImageButton) view.findViewById(R.id.btnDetail);
        }
    }


    public KomplainAdapterList(Context context, List<KomplainModel> itemList) {
        this.context = (Activity) context;
        this.itemList = itemList;
    }


    @Override
    public KomplainAdapterList.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_list_komplain, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(KomplainAdapterList.MyViewHolder holder, int position) {
        final KomplainModel komplain = itemList.get(position);
        holder.txtNomor.setText(komplain.getNomor());
        Date time = new java.util.Date(Long.parseLong(komplain.getTanggal())* (long) 1000);
        String vv = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(time);
        holder.txtTanggal.setText(vv);
        holder.txtPesan.setText(Html.fromHtml(komplain.getPesan()));
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(komplain.getJobId().equalsIgnoreCase("") || komplain.getJobId().equalsIgnoreCase("null")){

                    Intent i = new Intent(context.getApplicationContext(),KomplainActivity.class);
                    i.putExtra("id",komplain.getJobId());
                    i.putExtra("status","job");
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else{
                    Intent i = new Intent(context.getApplicationContext(),KomplainActivity.class);

                    i.putExtra("id",komplain.getOrderId());
                    i.putExtra("status","order");
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                Intent i = new Intent(context, KomplainDetailActivity.class);
                i.putExtra("id",komplain.getId());
                context.startActivity(i);
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

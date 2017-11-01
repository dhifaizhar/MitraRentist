package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Picasso;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.TransDetailActivity;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.PricingTools;

public class TransactionAcceptAdapter extends RecyclerView.Adapter<TransactionAcceptAdapter.ViewHolder> {
    private List<ItemTransaksiModul> mTransaksi;
    private Context context;
    private static final String TAG = "HistoryAdapter";

    public TransactionAcceptAdapter(Context context, List<ItemTransaksiModul> mTransaksi){
        super();
        this.mTransaksi = mTransaksi;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_accept_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mTransaksi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, idTrans, transCode, member, stardDate, endDate, nominal, asetName;
        CardView cardDetTrans;
        ImageView thumb;
        SpinKitView pBar;

        public ViewHolder(View itemView){
            super(itemView);
            transCode = (TextView) itemView.findViewById(R.id.tr_acc_code_trans);
            member = (TextView) itemView.findViewById(R.id.tr_acc_member);
            nominal = (TextView) itemView.findViewById(R.id.tr_acc_nominal);
            stardDate = (TextView) itemView.findViewById(R.id.tr_acc_start_date);
            endDate = (TextView) itemView.findViewById(R.id.tr_acc_end_date);
            asetName = (TextView) itemView.findViewById(R.id.tr_acc_aset);
            thumb = (ImageView) itemView.findViewById(R.id.tr_acc_thumb);
            cardDetTrans = (CardView) itemView.findViewById(R.id.card_view_acctransaksi);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i ){
        String aset, member, startDate, endDate;
        final ItemTransaksiModul trx = mTransaksi.get(i);

        aset = ": " + trx.getAsetName();
        member = ": " + trx.getMember();
        startDate = ": " + trx.getStartDate();
        endDate = ": " + trx.getEndDate();

        //  simpan value dalam objec
        viewHolder.transCode.setText(trx.getCodeTrans());
        viewHolder.nominal.setText(PricingTools.PriceStringFormat(trx.getPrice()));
        viewHolder.asetName.setText(aset);
        viewHolder.member.setText(member);
        viewHolder.stardDate.setText(startDate);
        viewHolder.endDate.setText(endDate);

        if (trx.getThumbnail().equals("null")){
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + "default.png";
            Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(viewHolder.thumb);
        }else{
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + trx.getThumbnail();
            Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(viewHolder.thumb);
        }

        viewHolder.cardDetTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDetTrans = new Intent(context, TransDetailActivity.class);
//                Intent iDetTrans = new Intent("transaction-accept");
                iDetTrans.putExtra("status", "accepted");
                iDetTrans.putExtra("id_trans", trx.getIdTrans());
                iDetTrans.putExtra("code_trans", viewHolder.transCode.getText());
                iDetTrans.putExtra("price", trx.getPrice());
                iDetTrans.putExtra("aset", trx.getAsetName());
                iDetTrans.putExtra("member", viewHolder.member.getText());
                iDetTrans.putExtra("startDate", trx.getStartDate());
                iDetTrans.putExtra("endDate", trx.getEndDate());
                iDetTrans.putExtra("driver", trx.getDriverIncluded());
                iDetTrans.putExtra("driver_name", trx.getDriverName());

//              LocalBroadcastManager.getInstance(context).sendBroadcast(iDetTrans);

                iDetTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iDetTrans);
            }
        });

    }
}

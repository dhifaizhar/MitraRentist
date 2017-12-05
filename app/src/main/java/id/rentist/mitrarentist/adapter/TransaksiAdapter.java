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

import com.squareup.picasso.Picasso;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.TransDetailActivity;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.PricingTools;
import id.rentist.mitrarentist.tools.Tools;

/**
 * Created by mdhif on 18/06/2017.
 */

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
    private final List<ItemTransaksiModul> mTransaksi;
    private Context context;
    private static final String TAG = "TransactionAdapter";

    public TransaksiAdapter(Context context, List<ItemTransaksiModul> mTransaksi) {
        super();
        this.mTransaksi = mTransaksi;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trx_item_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount(){
        return mTransaksi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView orderDate, idTrans, transCode, member, stardDate, endDate, nominal, asetName;
        CardView cardDetTrans;
        ImageView thumb;

        public ViewHolder(View itemView){
            super(itemView);
            transCode = (TextView) itemView.findViewById(R.id.tr_new_code_trans);
            member = (TextView) itemView.findViewById(R.id.tr_new_member);
            nominal = (TextView) itemView.findViewById(R.id.tr_new_nominal);
            stardDate = (TextView) itemView.findViewById(R.id.tr_new_start_date);
            endDate = (TextView) itemView.findViewById(R.id.tr_new_end_date);
            asetName = (TextView) itemView.findViewById(R.id.tr_new_aset);
            cardDetTrans = (CardView) itemView.findViewById(R.id.card_view_newtransaksi);
            thumb = (ImageView) itemView.findViewById(R.id.tr_new_thumb);
            orderDate = (TextView) itemView.findViewById(R.id.tr_order_date);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i ){
        final String aset, member, startDate, endDate, orderDate;
        final ItemTransaksiModul trx = mTransaksi.get(i);

        orderDate = Tools.dateHourFormat(trx.getOrderDate());
        aset = ": " + trx.getAsetName();
        member = ": " + trx.getMember();
        startDate = ": " + trx.getStartDate();
        endDate = ": " + trx.getEndDate();

        String imageUrl = AppConfig.URL_IMAGE_PROFIL + (trx.getThumbnail().equals("null") ? "default.png" : trx.getThumbnail());
        Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(viewHolder.thumb);

        //  simpan value dalam object
        viewHolder.transCode.setText(trx.getCodeTrans());
        viewHolder.nominal.setText(PricingTools.PriceStringFormat(trx.getPrice()));
        viewHolder.asetName.setText(aset);
        viewHolder.member.setText(member);
        viewHolder.stardDate.setText(startDate);
        viewHolder.endDate.setText(endDate);
        viewHolder.orderDate.setText(orderDate);
        viewHolder.cardDetTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDetTrans = new Intent(context, TransDetailActivity.class);
//                Intent iDetTrans = new Intent("transaction-new");

                iDetTrans.putExtra("status", trx.getStatus());
                iDetTrans.putExtra("id_trans", trx.getIdTrans());
                iDetTrans.putExtra("code_trans", viewHolder.transCode.getText());
                iDetTrans.putExtra("price", trx.getPrice());
                iDetTrans.putExtra("aset", trx.getAsetName());
                iDetTrans.putExtra("aset_thumb", trx.getAsetThumb());
                iDetTrans.putExtra("id_member", trx.getIdMember());
                iDetTrans.putExtra("member", viewHolder.member.getText());
                iDetTrans.putExtra("startDate", trx.getStartDate());
                iDetTrans.putExtra("endDate", trx.getEndDate());
                iDetTrans.putExtra("pickup_time", trx.getPickTime());
                iDetTrans.putExtra("latitude", trx.getLat());
                iDetTrans.putExtra("longitude", trx.getLong());
                iDetTrans.putExtra("address", trx.getAddress());
                iDetTrans.putExtra("note", trx.getNote());
                iDetTrans.putExtra("id_additional", trx.getIdAddtional());
                iDetTrans.putExtra("orderDate", orderDate);
                if (trx.getStatus().matches("accepted|ongoing|completed")){
                    iDetTrans.putExtra("with_driver", trx.getDriverIncluded());
                    iDetTrans.putExtra("driver_name", trx.getDriverName());
                }
                iDetTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iDetTrans);
            }
        });
    }

}

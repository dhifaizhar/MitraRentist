package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.TransDetailActivity;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;

/**
 * Created by mdhif on 07/07/2017.
 */

public class HistoryCancelAdapter extends RecyclerView.Adapter<HistoryCancelAdapter.ViewHolder> {
    private List<ItemTransaksiModul> mTransaksi;
    private Context context;
    private static final String TAG = "HistoryAdapter";

    public HistoryCancelAdapter(Context context, List<ItemTransaksiModul> mTransaksi){
        super();
        this.mTransaksi = mTransaksi;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_cancel_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mTransaksi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, idTrans, member, stardDate, endDate, nominal, asetName;
        CardView cardDetTrans;

        public ViewHolder(View itemView){
            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.tr_aset_type);
            idTrans = (TextView) itemView.findViewById(R.id.tr_can_id_trans);
            member = (TextView) itemView.findViewById(R.id.tr_can_member);
            nominal = (TextView) itemView.findViewById(R.id.tr_can_nominal);
            stardDate = (TextView) itemView.findViewById(R.id.tr_can_start_date);
            endDate = (TextView) itemView.findViewById(R.id.tr_can_end_date);
            asetName = (TextView) itemView.findViewById(R.id.tr_can_aset);
            cardDetTrans = (CardView) itemView.findViewById(R.id.card_view_cantransaksi);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemTransaksiModul trx = mTransaksi.get(i);

//        simpan value dalam object
        viewHolder.idTrans.setText(trx.getIdTrans());
        viewHolder.nominal.setText(trx.getPrice());
        viewHolder.asetName.setText(trx.getAsetName());
        viewHolder.member.setText(trx.getMember());
        viewHolder.stardDate.setText(trx.getStartDate());
        viewHolder.endDate.setText(trx.getEndDate());
        viewHolder.cardDetTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDetTrans = new Intent(context, TransDetailActivity.class);
                iDetTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iDetTrans);
            }
        });
    }
}

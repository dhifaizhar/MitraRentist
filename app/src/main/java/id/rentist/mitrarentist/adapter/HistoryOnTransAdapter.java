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
 * Created by mdhif on 13/07/2017.
 */

public class HistoryOnTransAdapter extends RecyclerView.Adapter<HistoryOnTransAdapter.ViewHolder> {
    private List<ItemTransaksiModul> mTransaksi;
    private Context context;
    private static final String TAG = "HistoryAdapter";

    public HistoryOnTransAdapter(Context context, List<ItemTransaksiModul> mTransaksi) {
        super();
        this.mTransaksi = mTransaksi;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_on_transaksi_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mTransaksi.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, member, waktu, harga;
        CardView cardDetTrans;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tr_aset_type);
            member = (TextView) itemView.findViewById(R.id.tr_member_det);
            waktu = (TextView) itemView.findViewById(R.id.tr_waktu_det);
            harga = (TextView) itemView.findViewById(R.id.tr_harga_det);
            cardDetTrans = (CardView) itemView.findViewById(R.id.card_view_ontransaksi);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemTransaksiModul trx = mTransaksi.get(i);

//        simpan value dalam object
        viewHolder.title.setText(trx.getTitle());
        viewHolder.member.setText(trx.getMember());
        viewHolder.waktu.setText(trx.getDate());
        viewHolder.harga.setText(trx.getPrice());
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

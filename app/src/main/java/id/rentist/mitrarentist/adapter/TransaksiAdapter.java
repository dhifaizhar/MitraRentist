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

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.TransDetailActivity;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;

/**
 * Created by mdhif on 18/06/2017.
 */

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
    private List<ItemTransaksiModul> mTransaksi;
    private Context context;
    private int j;

    public TransaksiAdapter(Context context, List<ItemTransaksiModul> mTransaksi) {
        super();
        this.mTransaksi = mTransaksi;
        this.context = context;
        ItemTransaksiModul tr;

//        untuk pengambilan data dari server, gunakan foreach
        for(j = 1;j < 8;j++){
            tr = new ItemTransaksiModul();
            tr.setTitle("Daihatsu Jazz | DC 123 WOW");
            if(j%2==0){
                tr.setThumbnail(R.drawable.mobil_1);
            }else{
                tr.setThumbnail(R.drawable.mobil_2);
            }
            tr.setMember("Pengguna Jasa Sewa " + j);
            tr.setDate("10 Mei (10:00) - 13 Mei (10:00) ");
            tr.setLoc("Ngurah Rai International Airport ");
            tr.setPrice("Rp 900.000 ");

            this.mTransaksi.add(tr);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trx_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mTransaksi.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgThumbnail;
        public TextView title;
        public TextView member;
        public TextView waktu;
        public TextView loca;
        public TextView harga;
        public CardView cardDetTrans;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tr_aset_type);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.tr_thumb_aset);
            member = (TextView) itemView.findViewById(R.id.tr_member_det);
            waktu = (TextView) itemView.findViewById(R.id.tr_waktu_det);
            loca = (TextView) itemView.findViewById(R.id.tr_lokasi_det);
            harga = (TextView) itemView.findViewById(R.id.tr_harga_det);
            cardDetTrans = (CardView) itemView.findViewById(R.id.card_view_transaksi);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemTransaksiModul trx = mTransaksi.get(i);

//        simpan value dalam object
        viewHolder.title.setText(trx.getTitle());
        viewHolder.imgThumbnail.setImageResource(trx.getThumbnail());
        viewHolder.member.setText(trx.getMember());
        viewHolder.waktu.setText(trx.getDate());
        viewHolder.loca.setText(trx.getLoc());
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

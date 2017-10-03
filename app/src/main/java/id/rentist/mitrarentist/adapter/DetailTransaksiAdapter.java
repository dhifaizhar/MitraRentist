package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;

/**
 * Created by mdhif on 13/07/2017.
 */

public class DetailTransaksiAdapter extends RecyclerView.Adapter<DetailTransaksiAdapter.ViewHolder> {

    private List<ItemTransaksiModul> mTransaksi;
    private Context context;
    private int j;

    public DetailTransaksiAdapter(Context context, List<ItemTransaksiModul> mTransaksi) {
        super();
        this.mTransaksi = mTransaksi;
        this.context = context;
        ItemTransaksiModul tr;

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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_trans_detail, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mTransaksi.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgThumbnail;
        private TextView title, member, waktu, loca, harga;
        private Button btnAccept;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tr_aset_type);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.tr_thumb_aset);
            member = (TextView) itemView.findViewById(R.id.tr_member_det);
            waktu = (TextView) itemView.findViewById(R.id.tr_waktu_det);
            loca = (TextView) itemView.findViewById(R.id.tr_lokasi_det);
            harga = (TextView) itemView.findViewById(R.id.tr_harga_det);
//            btnAccept = (Button) itemView.findViewById(R.id.btnDrop);
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

//        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}

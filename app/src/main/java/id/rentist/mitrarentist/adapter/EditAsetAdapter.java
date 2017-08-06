package id.rentist.mitrarentist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemAsetModul;

/**
 * Created by Nugroho Tri Pambud on 7/8/2017.
 */

public class EditAsetAdapter extends RecyclerView.Adapter<EditAsetAdapter.ViewHolder> {

    private List<ItemAsetModul> mAset;
    private int j;

    public EditAsetAdapter(){
        super();
        this.mAset = new ArrayList<ItemAsetModul>();
        ItemAsetModul as;

        as = new ItemAsetModul();
        as.setTitle("Daihatsu Jazz | DC 123 WOW");
        as.setThumbnail(R.drawable.mobil_1);
        as.setRating("4/5");
        as.setPrice("Rp 300.000 /hari");

        mAset.add(as);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_form_edit_aset, viewGroup, false);
        EditAsetAdapter.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ItemAsetModul as = mAset.get(i);

//        simpan value dalam object
        viewHolder.type.setText(as.getTitle());
//        viewHolder.transmisi.setText(as.getTrans());
//        viewHolder.platNo.setText(as.get());
//        viewHolder.pintu.setText(as.getDoor());
//        viewHolder.kursi.setText(as.getSeat());
        viewHolder.harga.setText(as.getPrice());
        viewHolder.gambar.setImageResource(as.getThumbnail());

    }

    @Override
    public int getItemCount() {
        return mAset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView type, transmisi, platNo, pintu, kursi, harga;
        public ImageView gambar;

        public ViewHolder(View itemView){
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.as_aset_type);
//            transmisi = (TextView) itemView.findViewById(R.id.as_trans);
////            platNo = (TextView) itemView.findViewById(R.id.as_plat_no);
//            pintu = (TextView) itemView.findViewById(R.id.as_pintu);
//            kursi  = (TextView) itemView.findViewById(R.id.as_kursi);
            harga = (TextView) itemView.findViewById(R.id.as_price);

            gambar = (ImageView) itemView.findViewById(R.id.as_thumb);
        }
    }
}

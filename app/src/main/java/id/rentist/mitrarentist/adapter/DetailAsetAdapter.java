package id.rentist.mitrarentist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemAsetModul;

/**
 * Created by Nugroho Tri Pambud on 7/6/2017.
 */

public class DetailAsetAdapter extends RecyclerView.Adapter<DetailAsetAdapter.ViewHolder> {

    private List<ItemAsetModul> mDetail;
    private int j;

    public DetailAsetAdapter(List<ItemAsetModul> mDetail){
        super();
        this.mDetail = mDetail;
        ItemAsetModul detAs;

//        detAs = new ItemAsetModul();
//        detAs.setTitle("Daihatsu Jazz | DC 123 WOW");
//        detAs.setThumbnail(R.drawable.mobil_1);
//        detAs.setRating("4/5");
//        detAs.setPrice("Rp 300.000 /hari");
//
//        this.mDetail.add(detAs);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_detail_aset, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mDetail.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView title;
        public TextView rating, price, feature;
        public ImageView imgThumbnail, priceIco, featureIco;

        public ViewHolder(View itemView) {
            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.det_as_aset_type);
//            imgThumbnail = (ImageView) itemView.findViewById(R.id.det_as_thumb_aset);
//            rating = (TextView) itemView.findViewById(R.id.det_as_rating_text);
//            price = (TextView) itemView.findViewById(R.id.det_as_harga_det);

        }
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ItemAsetModul detAs = mDetail.get(i);

//        viewHolder.title.setText(detAs.getTitle());
//        viewHolder.imgThumbnail.setImageResource(detAs.getThumbnail());
//        viewHolder.rating.setText(detAs.getRating());
//        viewHolder.price.setText(detAs.getPrice());

    }
}

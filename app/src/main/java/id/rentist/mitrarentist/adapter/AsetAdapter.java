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

import id.rentist.mitrarentist.DetailAsetActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemAsetModul;

/**
 * Created by mdhif on 19/06/2017.
 */

public class AsetAdapter extends RecyclerView.Adapter<AsetAdapter.ViewHolder> implements View.OnClickListener {

    private List<ItemAsetModul> mAset;
    private Context context;
    private int j;

    public AsetAdapter(Context context, List<ItemAsetModul> mAset){
        super();
        this.mAset = mAset;
        this.context = context;
        ItemAsetModul as;

        for(j = 1;j < 8;j++){
            as = new ItemAsetModul();

            as.setTitle("Daihatsu Jazz | DC 123 WOW");
            if(j%2==0){
                as.setThumbnail(R.drawable.mobil_1);
            }else{
                as.setThumbnail(R.drawable.mobil_2);
            }
            as.setRating("4/5");
            as.setPrice("Rp " + (j*100000) + " /hari");

            this.mAset.add(as);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.aset_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mAset.size();
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView rating;
        public ImageView imgThumbnail;
        public TextView price;
        public CardView cardDetAset;
        public ImageView priceIco;
        public TextView feature;
        public ImageView featureIco;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.as_aset_type);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.as_thumb_aset);
            rating = (TextView) itemView.findViewById(R.id.as_rating_text);
            price = (TextView) itemView.findViewById(R.id.as_harga_det);
            cardDetAset = (CardView) itemView.findViewById(R.id.card_view_aset);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemAsetModul as = mAset.get(i);

//        simpan value dalam object
        viewHolder.title.setText(as.getTitle());
        viewHolder.imgThumbnail.setImageResource(as.getThumbnail());
        viewHolder.rating.setText(as.getRating());
        viewHolder.price.setText(as.getPrice());
        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAset = new Intent(context, DetailAsetActivity.class);
                iAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iAset);
            }
        });
    }
}

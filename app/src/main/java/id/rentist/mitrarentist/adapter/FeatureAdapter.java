package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemFeatureModul;

/**
 * Created by mdhif on 12/10/2017.
 */

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> implements View.OnClickListener   {
    private List<ItemFeatureModul> mFeature;
    private Context context;
    private static final String TAG = "FeatureAdapter";

    public FeatureAdapter(final Context context, final List<ItemFeatureModul> mFeature){
        super();
        this.mFeature = mFeature;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mFeature.size();
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nama, price;

        public ViewHolder(View itemView){
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.fr_name);
            price = (TextView) itemView.findViewById(R.id.fr_price);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        final ItemFeatureModul feature = mFeature.get(i);

//        simpan value dalam object
        viewHolder.nama.setText(feature.getName());
        viewHolder.price.setText(feature.getPrice());
    }
}

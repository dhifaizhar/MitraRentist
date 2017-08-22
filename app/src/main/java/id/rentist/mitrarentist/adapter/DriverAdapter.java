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

import id.rentist.mitrarentist.DriverDetailActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemDriverModul;

/**
 * Created by mdhif on 16/07/2017.
 */

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> implements View.OnClickListener  {

    private List<ItemDriverModul> mDriver;
    private Context context;
    private static final String TAG = "DriverAdapter";

    public DriverAdapter(final Context context, final List<ItemDriverModul> mDriver){
        super();
        this.mDriver = mDriver;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.driver_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mDriver.size();
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nama, telp, sim;
        private ImageView imgThumbnail;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.dr_nama_info);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.dr_thumb);
            sim = (TextView) itemView.findViewById(R.id.dr_sim_info);
            cardDetAset = (CardView) itemView.findViewById(R.id.card_view_driver);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        final ItemDriverModul driver = mDriver.get(i);

//        simpan value dalam object
        viewHolder.nama.setText(driver.getName());
        viewHolder.sim.setText(driver.getSIM());
        viewHolder.imgThumbnail.setImageResource(driver.getThumbnail());
        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDriver = new Intent(context, DriverDetailActivity.class);
                iDriver.putExtra("id_driver", driver.getId());
                iDriver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iDriver);
            }
        });
    }
}

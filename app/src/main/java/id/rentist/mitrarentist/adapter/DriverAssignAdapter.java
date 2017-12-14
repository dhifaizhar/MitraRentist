package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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
import id.rentist.mitrarentist.modul.ItemDriverModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;

/**
 * Created by Nugroho Tri Pambud on 10/27/2017.
 */

public class DriverAssignAdapter extends RecyclerView.Adapter<DriverAssignAdapter.ViewHolder> implements View.OnClickListener{

    private List<ItemDriverModul> mDriver;
    private Context context;
    private static final String TAG = "DriverAdapter";

    public DriverAssignAdapter(final Context context, final List<ItemDriverModul> mDriver){
        super();
        this.mDriver = mDriver;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.driver_assign_item_view, viewGroup, false);
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
        private TextView nama;
        private ImageView imgThumbnail;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.dr_nama_info);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.dr_thumb);
            cardDetAset = (CardView) itemView.findViewById(R.id.card_view_driver);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        final ItemDriverModul driver = mDriver.get(i);

//        simpan value dalam object
        if (driver.getThumb().equals("")){
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + "default.png";
            Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(viewHolder.imgThumbnail);
        }else{
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + driver.getThumb();
            Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(viewHolder.imgThumbnail);
        }


        viewHolder.nama.setText(driver.getName());
        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("custom-message");
                intent.putExtra("id_driver",driver.getId());
                intent.putExtra("driver_name",driver.getName());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }
}

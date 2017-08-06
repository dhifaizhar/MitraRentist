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
    private int j;

    public DriverAdapter(Context context, List<ItemDriverModul> mDriver){
        super();
        this.mDriver = mDriver;
        this.context = context;
        ItemDriverModul as;

        for(j = 1;j < 4;j++){
            as = new ItemDriverModul();

            as.setName("Nama Driver" + j);
            as.setThumbnail(R.drawable.ic_person_black_24dp);
            as.setTelp("08xxxxxxxxx" + j);

            this.mDriver.add(as);
        }
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
        private TextView nama, telp;
        private ImageView imgThumbnail;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.dr_nama_info);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.dr_thumb);
            telp = (TextView) itemView.findViewById(R.id.dr_telp_info);
            cardDetAset = (CardView) itemView.findViewById(R.id.card_view_driver);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemDriverModul as = mDriver.get(i);

//        simpan value dalam object
        viewHolder.nama.setText(as.getName());
        viewHolder.telp.setText(as.getTelp());
        viewHolder.imgThumbnail.setImageResource(as.getThumbnail());
        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDriver = new Intent(context, DriverDetailActivity.class);
                iDriver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iDriver);
            }
        });
    }
}

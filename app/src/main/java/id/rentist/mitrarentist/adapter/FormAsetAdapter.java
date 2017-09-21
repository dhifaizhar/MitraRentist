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
 * Created by mdhif on 16/07/2017.
 */

public class FormAsetAdapter extends RecyclerView.Adapter<FormAsetAdapter.ViewHolder> {

    private List<ItemAsetModul> mAset;
    private int j;

    public FormAsetAdapter(){
        super();
        this.mAset = new ArrayList<ItemAsetModul>();
        ItemAsetModul as;

        as = new ItemAsetModul();
        mAset.add(as);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_form_car_aset, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mAset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, email, telp;
        public ImageView profilePhoto;

        public ViewHolder(View itemView){
            super(itemView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemAsetModul as = mAset.get(i);

//        simpan value dalam object
    }
}

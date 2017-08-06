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
import id.rentist.mitrarentist.modul.ItemDriverModul;

/**
 * Created by mdhif on 16/07/2017.
 */

public class FormDriverAdapter extends RecyclerView.Adapter<FormDriverAdapter.ViewHolder> {

    private List<ItemDriverModul> mProfile;
    private int j;

    public FormDriverAdapter(){
        super();
        this.mProfile = new ArrayList<ItemDriverModul>();
        ItemDriverModul pr;

        pr = new ItemDriverModul();
        pr.setThumbnail(R.drawable.blue_android);
        pr.setName("Mamat Ashoy");
        pr.setEmail("driver@mail.com");
        pr.setTelp("08 xxx xxx xxx");

        mProfile.add(pr);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_form_driver, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mProfile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, email, telp;
        public ImageView profilePhoto;

        public ViewHolder(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.pr_driver_name);
            email = (TextView) itemView.findViewById(R.id.pr_email);
            telp = (TextView) itemView.findViewById(R.id.pr_telp);
            profilePhoto = (ImageView) itemView.findViewById(R.id.pr_thumb_driver);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ItemDriverModul as = mProfile.get(i);

//        simpan value dalam object
        viewHolder.name.setText(as.getName());
        viewHolder.email.setText(as.getEmail());
        viewHolder.telp.setText(as.getTelp());
        viewHolder.profilePhoto.setImageResource(as.getThumbnail());
    }
}

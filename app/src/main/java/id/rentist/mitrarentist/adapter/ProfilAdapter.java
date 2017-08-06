package id.rentist.mitrarentist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ProfileContentModul;

/**
 * Created by mdhif on 21/06/2017.
 */

public class ProfilAdapter extends RecyclerView.Adapter<ProfilAdapter.ViewHolder> {

    private List<ProfileContentModul> mProfile;
    private int j;

    public ProfilAdapter(){
        super();
        this.mProfile = new ArrayList<ProfileContentModul>();
        ProfileContentModul pr;

        pr = new ProfileContentModul();
        pr.setProfilePhoto(R.drawable.user_ava_man);
        pr.setRentName("Rental Budi Makmur");
        pr.setAddress("Gg. Pusaka Kuta, Kabupaten Badung, Bali 80361");
        mProfile.add(pr);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_rental_view, viewGroup, false);
//        ViewHolder viewHolder = new ViewHolder(v);
//        return viewHolder;
        return null;
    }

    @Override
    public int getItemCount(){
        return mProfile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView rental, address;
        private ImageView profilePhoto;

        public ViewHolder(View itemView){
            super(itemView);
            rental = (TextView) itemView.findViewById(R.id.pr_rental_name);
            address = (TextView) itemView.findViewById(R.id.pr_address_name);
            profilePhoto = (ImageView) itemView.findViewById(R.id.pr_thumb);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ProfileContentModul as = mProfile.get(i);

//        simpan value dalam object
        viewHolder.rental.setText(as.getRentName());
        viewHolder.address.setText(as.getAddress());
        viewHolder.profilePhoto.setImageResource(as.getProfilePhoto());
    }
}

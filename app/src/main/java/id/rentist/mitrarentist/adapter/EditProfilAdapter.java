package id.rentist.mitrarentist.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ProfileContentModul;

/**
 * Created by mdhif on 07/07/2017.
 */

public class EditProfilAdapter extends RecyclerView.Adapter<EditProfilAdapter.ViewHolder>  {

    private List<ProfileContentModul> mProfile;
    private Context context;
    private int j;

    public EditProfilAdapter(Context context){
        super();
        this.mProfile = new ArrayList<ProfileContentModul>();
        this.context = context;
        ProfileContentModul pr;

        pr = new ProfileContentModul();
        pr.setProfilePhoto(R.drawable.user_ava_man);
        pr.setRentName("Rental Budi Makmur");
        pr.setEmail("rental@mail.com");
        pr.setTelp("08 xxx xxx xxx");
        pr.setAddress("Gg. Pusaka Kuta, Kabupaten Badung, Bali 80361");
        mProfile.add(pr);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_form_edit_profil, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mProfile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView rental, address, email, telp;
        private ImageView profilePhoto;
        private Button btnUploadFoto;

        public ViewHolder(View itemView){
            super(itemView);
            rental = (TextView) itemView.findViewById(R.id.pr_rental_name);
            email = (TextView) itemView.findViewById(R.id.pr_email);
            telp = (TextView) itemView.findViewById(R.id.pr_telp);
            address = (TextView) itemView.findViewById(R.id.pr_address_name);
            profilePhoto = (ImageView) itemView.findViewById(R.id.pr_thumb);
            btnUploadFoto = (Button) itemView.findViewById(R.id.btnUploadFoto);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        ProfileContentModul as = mProfile.get(i);

//        simpan value dalam object
        viewHolder.rental.setText(as.getRentName());
        viewHolder.email.setText(as.getEmail());
        viewHolder.telp.setText(as.getTelp());
        viewHolder.address.setText(as.getAddress());
        viewHolder.profilePhoto.setImageResource(as.getProfilePhoto());
        viewHolder.btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageProfile();
            }
        });
    }

    private void selectImageProfile() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.show();
    }
}

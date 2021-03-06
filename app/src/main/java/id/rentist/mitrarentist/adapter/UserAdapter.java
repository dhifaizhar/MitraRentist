package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.UserDetailActivity;
import id.rentist.mitrarentist.modul.UserModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;

/**
 * Created by Nugroho Tri Pambud on 8/9/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements View.OnClickListener {

    private final List<UserModul> mUser;
    private Context context;
    private static final String TAG = "UserAdapter";

    public UserAdapter(final Context context, final List<UserModul> mUser) {
        super();
        this.mUser = mUser;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final UserModul user = mUser.get(i);
        Log.e(TAG, String.format("Data Bind : %s", mUser));

//        viewHolder.imgThumbnail.setImageResource(user.getThumbnail());
        if (user.getThumbnail().equals("null")){
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + "img_default.png";
            Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(viewHolder.imgThumbnail);
        }else{
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + user.getThumbnail();
            Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(viewHolder.imgThumbnail);
        }

        viewHolder.name.setText(user.getName());
        viewHolder.role.setText(user.getRole());
        switch (user.getRole()) {
            case "SuperAdmin":
                viewHolder.role.setBackgroundColor(0xffff2828);
                break;
            case "Admin":
                viewHolder.role.setBackgroundColor(0xff99cc00);
                break;
            case "Operation":
                viewHolder.role.setBackgroundColor(0xff33b5e5);
                break;
            case "Finance":
                viewHolder.role.setBackgroundColor(0xfff19800);
                break;
        }
        viewHolder.cardViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra("id_user", user.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    @Override
    public void onClick(View v) {}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, role, phone, email;
        private ImageView imgThumbnail;
        private CardView cardViewUser;


        public ViewHolder(View itemView) {
            super(itemView);

            imgThumbnail = (ImageView) itemView.findViewById(R.id.user_thumb);
            name = (TextView) itemView.findViewById(R.id.user_name);
            role = (TextView) itemView.findViewById(R.id.user_role);
//            email = (TextView) itemView.findViewById(R.id.use);
            cardViewUser = (CardView) itemView.findViewById(R.id.card_view_user);


        }
    }
}

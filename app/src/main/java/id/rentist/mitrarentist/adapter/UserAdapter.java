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

import id.rentist.mitrarentist.MessageDetailActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.UserModul;

/**
 * Created by Nugroho Tri Pambud on 8/9/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<UserModul> mUser;
    private final Context context;
    private int j;

    public UserAdapter(Context context, List<UserModul> mUser) {
        super();
        this.mUser = mUser;
        this.context = context;
        UserModul user ;

        for (j = 1; j < 4; j++) {
           user  = new UserModul();

            switch (j){
                case 1 :
                    user.setName("Riki Saputra");
                    user.setEmail("riki@mail.com");
                    user.setPhone("081254674578");
                    user.setRole("Executive");
                    user.setThumbnail(R.drawable.ava_budi);
                    break;

                case 2 :
                    user.setName("Indra Permana");
                    user.setEmail("indra@mail.com");
                    user.setPhone("081254234578");
                    user.setRole("Operasional");
                    user.setThumbnail(R.drawable.user_ava_man);
                    break;

                case 3:
                    user.setName("Joni Iskandar");
                    user.setEmail("joni@mail.com");
                    user.setPhone("085754290578");
                    user.setRole("Operasional");
                    user.setThumbnail(R.drawable.user_ava_man);
                    break;
            }

            this.mUser.add(user);
        }
    }

    

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        UserModul user = mUser.get(i);

        viewHolder.imgThumbnail.setImageResource(user.getThumbnail());
        viewHolder.name.setText(user.getName());
        viewHolder.role.setText(user.getRole());
        viewHolder.phone.setText(user.getPhone());
        viewHolder.cardViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imgThumbnail;
        public TextView role;
        public TextView phone;
        public TextView email;
        public CardView cardViewUser;


        public ViewHolder(View itemView) {
            super(itemView);

            imgThumbnail = (ImageView) itemView.findViewById(R.id.user_thumb);
            name = (TextView) itemView.findViewById(R.id.user_name);
            role = (TextView) itemView.findViewById(R.id.user_role);
//            email = (TextView) itemView.findViewById(R.id.use);
            phone = (TextView) itemView.findViewById(R.id.user_phone);
            cardViewUser = (CardView) itemView.findViewById(R.id.card_view_user);


        }
    }
}

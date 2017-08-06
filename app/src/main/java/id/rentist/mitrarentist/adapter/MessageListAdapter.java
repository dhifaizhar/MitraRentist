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
import id.rentist.mitrarentist.modul.MessageListModul;

/**
 * Created by Nugroho Tri Pambud on 7/16/2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<MessageListModul> mMsg;
    private Context context;
    private int j;


    public MessageListAdapter(Context context, List<MessageListModul> mMsg) {
        super();
        this.mMsg = mMsg;
        this.context = context;
        MessageListModul msg;

        for(j = 1;j < 8;j++){
            msg = new MessageListModul();

            msg.setTitle("Daihatsu Jazz | DC 123 WOW");
            msg.setName("Dani Lukmana");
            msg.setLastMessage("Baik pak, terimakasih");
            msg.setLastMessageTime("09:29");
            if(j%2==0){
                msg.setThumbnail(R.drawable.blue_android);
            }else{
                msg.setThumbnail(R.drawable.ava_budi);
            }

            this.mMsg.add(msg);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        MessageListModul msg = mMsg.get(i);

        viewHolder.title.setText(msg.getTitle());
        viewHolder.imgThumbnail.setImageResource(msg.getThumbnail());
        viewHolder.name.setText(msg.getName());
        viewHolder.lastMessage.setText(msg.getLastMessage());
        viewHolder.lastMessageTime.setText(msg.getLastMessageTime());
        viewHolder.cardViewMessageList.setOnClickListener(new View.OnClickListener() {
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
        return mMsg.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView name;
        public ImageView imgThumbnail;
        public TextView lastMessage;
        public TextView lastMessageTime;
        public CardView cardViewMessageList;


        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.msg_title);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.msg_thumb);
            name = (TextView) itemView.findViewById(R.id.msg_name);
            lastMessage = (TextView) itemView.findViewById(R.id.msg_hal);
            lastMessageTime = (TextView) itemView.findViewById(R.id.msg_last_time);
            cardViewMessageList = (CardView) itemView.findViewById(R.id.card_view_msgList);


        }
    }
}

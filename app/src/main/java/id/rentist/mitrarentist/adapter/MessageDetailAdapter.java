package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.MessageDetailModul;

/**
 * Created by mdhif on 02/01/2018.
 */

public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.ViewHolder> {
    private List<MessageDetailModul> mMsg;
    Context context;

    private static final String TAG = "MessageAdapter";
    private static final String TOKEN = "secretissecret";


    public MessageDetailAdapter(Context context, List<MessageDetailModul> mMsg) {
        super();
        this.mMsg = mMsg;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_detail_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        MessageDetailModul msg = mMsg.get(i);

        viewHolder.messageText.setText(msg.getMessageText());
        viewHolder.messageUser.setText(msg.getMessageUser());
        viewHolder.messageTime.setText(msg.getMessageTime());
        if(msg.getType() == 2){
            viewHolder.cardViewMessageDetail.setCardBackgroundColor(0xffb0e0e6);
        }
    }

    @Override
    public int getItemCount() {
        return mMsg.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText, messageUser, messageTime;
        private CardView cardViewMessageDetail;

        public ViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text);
            messageUser = (TextView) itemView.findViewById(R.id.message_user);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            cardViewMessageDetail = (CardView) itemView.findViewById(R.id.card_view_msgDetail);
        }
    }
}

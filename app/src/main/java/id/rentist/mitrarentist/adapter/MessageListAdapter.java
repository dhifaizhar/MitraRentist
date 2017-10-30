package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private AsyncTask mMessageTask = null;

    private static final String TAG = "MessageAdapter";
    private static final String TOKEN = "secretissecret";


    public MessageListAdapter(Context context, List<MessageListModul> mMsg) {
        super();
        this.mMsg = mMsg;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final MessageListModul msg = mMsg.get(i);

        viewHolder.title.setText(msg.getName());
        viewHolder.cardViewMessageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.putExtra("key",msg.getTitle());
                intent.putExtra("phone", msg.getName());
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
        private TextView title;
        private CardView cardViewMessageList;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.msg_title);
            cardViewMessageList = (CardView) itemView.findViewById(R.id.card_view_msgList);
        }
    }
}

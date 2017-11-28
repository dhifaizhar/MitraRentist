package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.ComplainDetailActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ComplainModul;

/**
 * Created by mdhif on 16/07/2017.
 */

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ViewHolder> {
    private List<ComplainModul> mMsg;
    private Context context;
    private int j;


    public ComplainAdapter(Context context, List<ComplainModul> mMsg) {
        super();
        this.mMsg = mMsg;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_complain, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ComplainModul msg = mMsg.get(i);

        viewHolder.nama.setText(msg.getNamaPelanggan());
        viewHolder.hal.setText(msg.getPerihal());
        viewHolder.date.setText(msg.getTglKirim());
        viewHolder.cardViewMessageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComplainDetailActivity.class);
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
        private TextView nama, hal, date;
        public CardView cardViewMessageList;

        public ViewHolder(View itemView) {
            super(itemView);

            nama = (TextView) itemView.findViewById(R.id.msg_title);
            hal = (TextView) itemView.findViewById(R.id.msg_hal);
            date = (TextView) itemView.findViewById(R.id.msg_last_time);
            cardViewMessageList = (CardView) itemView.findViewById(R.id.card_view_msgList);
        }
    }
}

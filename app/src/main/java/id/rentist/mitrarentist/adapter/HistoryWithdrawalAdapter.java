package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.WithdrawalModul;

/**
 * Created by Nugroho Tri Pambud on 9/29/2017.
 */

public class HistoryWithdrawalAdapter extends RecyclerView.Adapter<HistoryWithdrawalAdapter.ViewHolder> {
    private List<WithdrawalModul> mWithdrawal;
    private Context context;

    public HistoryWithdrawalAdapter(Context applicationContext, List<WithdrawalModul> mWithdrawal) {
        super();
        this.mWithdrawal = mWithdrawal;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_withdrawal_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mWithdrawal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nominal, date, desc, status;

        public ViewHolder(View itemView){
            super(itemView);
            nominal = (TextView) itemView.findViewById(R.id.wd_balance);
            desc = (TextView) itemView.findViewById(R.id.wd_desc);
            date = (TextView) itemView.findViewById(R.id.wd_date);
            status = (TextView) itemView.findViewById(R.id.wd_status);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        WithdrawalModul wd = mWithdrawal.get(i);

//       simpan value dalam object
        viewHolder.nominal.setText(wd.getNominal());
        viewHolder.desc.setText(wd.getDesc());
        viewHolder.date.setText(wd.getDate());
        viewHolder.status.setText(wd.getStatus());
    }

}

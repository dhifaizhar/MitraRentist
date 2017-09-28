package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.DompetModul;

/**
 * Created by mdhif on 07/07/2017.
 */

public class HistorySaldoAdapter extends RecyclerView.Adapter<HistorySaldoAdapter.ViewHolder> {
    private List<DompetModul> mDompet;
    private Context context;
    private int j;

    public HistorySaldoAdapter(final Context context, final List<DompetModul> mDompet){
        super();
        this.mDompet = mDompet;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_saldo_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mDompet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView credit, date, nama, status;

        public ViewHolder(View itemView){
            super(itemView);
            credit = (TextView) itemView.findViewById(R.id.tr_harga_det);
            nama = (TextView) itemView.findViewById(R.id.tr_member_det);
            date = (TextView) itemView.findViewById(R.id.tr_date);
//            status = (TextView) itemView.findViewById(R.id.tr_status);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        DompetModul dm = mDompet.get(i);

//        simpan value dalam object
        viewHolder.credit.setText(dm.getCredit());
        viewHolder.nama.setText(dm.getNama());
        viewHolder.date.setText(dm.getDate());
//        viewHolder.status.setText(dm.getStatus());
    }
}

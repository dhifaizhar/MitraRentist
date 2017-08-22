package id.rentist.mitrarentist.adapter;

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
    private int j;

    public HistorySaldoAdapter(List<DompetModul> mDompet){
        super();
        this.mDompet = mDompet;
        DompetModul dm;

        for(j = 1;j < 5;j++){
            dm = new DompetModul();
            dm.setDate("Kamis, 07 Juli 2017");
            dm.setCredit("50.000,00 IDR");
            dm.setNama("Dudi Duri");
            if(j%2 == 0){
                dm.setStatus("Kredit");
            }else{
                dm.setStatus("Transfer Dana Tunai");
            }

            mDompet.add(dm);
        }
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
            status = (TextView) itemView.findViewById(R.id.tr_status);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        DompetModul dm = mDompet.get(i);

//        simpan value dalam object
        viewHolder.credit.setText(dm.getCredit());
        viewHolder.nama.setText(dm.getNama());
        viewHolder.date.setText(dm.getDate());
        viewHolder.status.setText(dm.getStatus());
    }
}

package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.DompetActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.TransactionActivity;
import id.rentist.mitrarentist.WorkDateActivity;
import id.rentist.mitrarentist.modul.DashboardModul;

/**
 * Created by mdhif on 10/07/2017.
 */

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private List<DashboardModul> mStat;
    private Context context;
    private int j;

    public DashboardAdapter(Context context, List<DashboardModul> mStat) {
        super();
        this.mStat = mStat;
        this.context = context;
        DashboardModul dm;

        dm = new DashboardModul();
        dm.setRentName("RENTAL SUKSES");

        mStat.add(dm);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_dashboard, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mStat.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgThumbnail;
        TextView rentName, member, waktu, loca, harga;
        ImageButton btnNewTrans, btnToSaldo, btnWorkDate;


        public ViewHolder(View itemView){
            super(itemView);
            rentName = (TextView) itemView.findViewById(R.id.rentName);
            btnNewTrans = (ImageButton) itemView.findViewById(R.id.btn_to_det_new_trans);
            btnToSaldo = (ImageButton) itemView.findViewById(R.id.btn_to_saldo);
            btnWorkDate = (ImageButton) itemView.findViewById(R.id.btn_work_date);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        DashboardModul dm = mStat.get(i);

//        simpan value dalam object
        viewHolder.rentName.setText(dm.getRentName());
        viewHolder.btnNewTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTrans = new Intent(context, TransactionActivity.class);
                iTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iTrans);
            }
        });
        viewHolder.btnToSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSaldo = new Intent(context, DompetActivity.class);
                iSaldo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iSaldo);
            }
        });
        viewHolder.btnWorkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callWorkDateForm(v);
            }
        });
    }

    private void callWorkDateForm(View v){
        Intent iWork = new Intent(context, WorkDateActivity.class);
        iWork.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(iWork);
    }
}

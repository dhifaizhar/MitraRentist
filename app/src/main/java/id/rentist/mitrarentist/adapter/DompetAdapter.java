package id.rentist.mitrarentist.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.WithdrawalActivity;
import id.rentist.mitrarentist.modul.DompetModul;

/**
 * Created by mdhif on 06/07/2017.
 */

public class DompetAdapter extends RecyclerView.Adapter<DompetAdapter.ViewHolder> {
    private List<DompetModul> mDompet;
    private int j;

    public DompetAdapter(List<DompetModul> mDompet){
        super();
        this.mDompet = mDompet;
        DompetModul dm;

        dm = new DompetModul();
        dm.setCredit("7.000.000 IDR");
        dm.setTunai("0 IDR");

        mDompet.add(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_dompet, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mDompet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView credit,tunai;
        public Button withdrawal;

        public ViewHolder(View itemView){
            super(itemView);
            credit = (TextView) itemView.findViewById(R.id.dm_credit);
            tunai = (TextView) itemView.findViewById(R.id.dm_tunai);
            withdrawal = (Button) itemView.findViewById(R.id.dm_btn_drawal);

            withdrawal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    Intent iWithdrawal = new Intent(v.getContext(), WithdrawalActivity.class);
                    v.getContext().startActivity(iWithdrawal);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        DompetModul dm = mDompet.get(i);

//        simpan value dalam object
        viewHolder.credit.setText(dm.getCredit());
        viewHolder.tunai.setText(dm.getTunai());
    }
}

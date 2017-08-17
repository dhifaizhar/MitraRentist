package id.rentist.mitrarentist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.WithdrawalModul;

/**
 * Created by Nugroho Tri Pambud on 7/8/2017.
 */

public class WithdrawalAdapter extends RecyclerView.Adapter<WithdrawalAdapter.ViewHolder> {
    private List<WithdrawalModul> mWithdrawal;
    private int j;

    public WithdrawalAdapter(List<WithdrawalModul> mWithdrawal) {
        super();
        this.mWithdrawal = mWithdrawal;
        WithdrawalModul wd;

        wd = new WithdrawalModul();
        wd.setCredit("50000");

        mWithdrawal.add(wd);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_withdrawal, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        WithdrawalModul wd = mWithdrawal.get(i);

//        simpan value dalam object
        viewHolder.credit.setText(wd.getCredit());
    }

    @Override
    public int getItemCount() {
        return mWithdrawal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView credit;
        public Button withdrawal;
        public ImageButton reset;
        public RadioGroup radioGroup;
        public LinearLayout formNew;

        public ViewHolder(View itemView){
            super(itemView);
//            credit = (TextView) itemView.findViewById(R.id.wd_credit);
//            withdrawal = (Button) itemView.findViewById(R.id.dm_btn_drawal);
//            reset = (ImageButton) itemView.findViewById(R.id.reset_button);
//            radioGroup = (RadioGroup) itemView.findViewById(R.id.rek_ragrup);
//            formNew = (LinearLayout) itemView.findViewById(R.id.con_baru);

//            reset.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    credit.setText("0");
//                }
//            });

//            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                    int id = radioGroup.getCheckedRadioButtonId();
//                    View radioButton = radioGroup.findViewById(id);
//                    if(radioButton.getId()==R.id.rad_terdaftar)
//                    {
//                        formNew.setVisibility(View.GONE);
//                    }
//                    else
//                    {
//                       formNew.setVisibility(View.VISIBLE);
//                    }
//                }
//            });

        }
    }
}

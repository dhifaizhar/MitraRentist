package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.FormVoucherActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.VoucherModul;

/**
 * Created by Nugroho Tri Pambud on 7/13/2017.
 */

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> implements View.OnClickListener{
    private final List<VoucherModul> mVoucher;
    private Context context;

    public VoucherAdapter(Context context, List<VoucherModul> mVoucher) {
        super();
        this.mVoucher = mVoucher;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.voucher_item_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        VoucherModul vou = mVoucher.get(i);

        viewHolder.title.setText(vou.getName());
        if(!vou.getNominal().equals("0")){
            viewHolder.discount.setText(vou.getNominal());
        }else{
            viewHolder.discount.setText(vou.getPercen() + "%");
        }
        viewHolder.startDate.setText(vou.getStartDate());
        viewHolder.endDate.setText(vou.getEndDate());
        viewHolder.asCategory.setText(vou.getAsCategory());
        viewHolder.type.setText(vou.getType());
//        viewHolder.amount.setText(vou.getAmount());
//        viewHolder.status.setText(vou.getStatus());

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iVou = new Intent(context, FormVoucherActivity.class);
                iVou.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iVou);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mVoucher.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, discount, startDate, endDate, amount, status, asCategory, type, btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.vou_title);
            discount = (TextView) itemView.findViewById(R.id.vou_discount);
            startDate = (TextView) itemView.findViewById(R.id.vou_date);
            endDate = (TextView) itemView.findViewById(R.id.vou_endDate);
            asCategory = (TextView) itemView.findViewById(R.id.vou_as_category);
            type = (TextView) itemView.findViewById(R.id.vou_type);
            btnEdit = (TextView) itemView.findViewById(R.id.vou_edit);



//            amount = (TextView) itemView.findViewById(R.id.vou_amount);
//            status = (TextView) itemView.findViewById(R.id.vou_status);


        }
    }
}

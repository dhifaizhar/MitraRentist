package id.rentist.mitrarentist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.VoucherModul;

/**
 * Created by Nugroho Tri Pambud on 7/13/2017.
 */

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private final List<VoucherModul> mVoucher;
    private int j;

    public VoucherAdapter(List<VoucherModul> mVoucher) {
        super();
        this.mVoucher = mVoucher;
        VoucherModul vou;

        for (j = 1; j < 5;j++){
            vou = new VoucherModul();

            if (j%2 ==0){
                vou.setTitle("Weekend Promo");
                vou.setDiscount("Rp 20000");
                vou.setStartDate("07-07-2017");
                vou.setEndDate("21-07-2017");
                vou.setAmount("100");
                vou.setStatus("AKTIF");
            } else {
                vou.setTitle("Liburan Telah Tiba");
                vou.setDiscount("Rp 10000");
                vou.setStartDate("10-07-2017");
                vou.setEndDate("10-08-2017");
                vou.setAmount("50");
                vou.setStatus("INAKTIF");
            }
            this.mVoucher.add(vou);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.voucher_item_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        VoucherModul vou = mVoucher.get(i);

        viewHolder.title.setText(vou.getTitle());
        viewHolder.discount.setText(vou.getDiscount());
        viewHolder.startDate.setText(vou.getStartDate());
        viewHolder.endDate.setText(vou.getEndDate());
        viewHolder.amount.setText(vou.getAmount());
        viewHolder.status.setText(vou.getStatus());


    }

    @Override
    public int getItemCount() {
        return mVoucher.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, discount, startDate, endDate, amount, status;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.vou_title);
            discount = (TextView) itemView.findViewById(R.id.vou_discount);
            startDate = (TextView) itemView.findViewById(R.id.vou_startDate);
            endDate = (TextView) itemView.findViewById(R.id.vou_endDate);
            amount = (TextView) itemView.findViewById(R.id.vou_amount);
            status = (TextView) itemView.findViewById(R.id.vou_status);


        }
    }
}

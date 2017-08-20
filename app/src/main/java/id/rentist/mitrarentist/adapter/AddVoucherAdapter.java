package id.rentist.mitrarentist.adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.VoucherModul;

/**
 * Created by Nugroho Tri Pambud on 7/14/2017.
 */

public class AddVoucherAdapter extends RecyclerView.Adapter<AddVoucherAdapter.ViewHolder> {

    private List<VoucherModul> mVoucher;

    public AddVoucherAdapter(){
        super();
        this.mVoucher = new ArrayList<VoucherModul>();
        VoucherModul vou;

        vou = new VoucherModul();
        vou.setTitle("");
        vou.setDiscount("");
        vou.setStartDate("");
        vou.setEndDate("");
        vou.setAmount("");

        mVoucher.add(vou);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_form_voucher, viewGroup, false);
        AddVoucherAdapter.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        VoucherModul vou = mVoucher.get(i);

        viewHolder.title.setText(vou.getTitle());
        viewHolder.discount.setText(vou.getDiscount());
        viewHolder.startDate.setText(vou.getStartDate());
        viewHolder.endDate.setText(vou.getEndDate());
        viewHolder.amount.setText(vou.getAmount());
    }

    @Override
    public int getItemCount() {
        return mVoucher.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, discount, startDate, endDate, amount, status;
        public Button add;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.vou_title);
            discount = (TextView) itemView.findViewById(R.id.vou_discount);
            startDate = (TextView) itemView.findViewById(R.id.vou_startDate);
            endDate = (TextView) itemView.findViewById(R.id.vou_endDate);
            amount = (TextView) itemView.findViewById(R.id.vou_amount);
            add = (Button) itemView.findViewById(R.id.btn_add);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Voucher ditambahkan", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

        }
    }
}

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
import id.rentist.mitrarentist.tools.Tools;

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
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final VoucherModul vou = mVoucher.get(i);

        viewHolder.title.setText(vou.getName());
        if(!vou.getNominal().equals("0")){
            viewHolder.discount.setText(vou.getNominal() + " IDR");
        }else{
            viewHolder.discount.setText(vou.getPercen() + " %");
        }
        viewHolder.startDate.setText(Tools.dateFineFormat(vou.getStartDate()));
        viewHolder.endDate.setText(Tools.dateFineFormat(vou.getEndDate()));
        if(vou.getType().equals("both")){
            viewHolder.type.setText("mobile, web");
        }else{
            viewHolder.type.setText(vou.getType());
        }
        viewHolder.code.setText(vou.getCode());
        viewHolder.desc.setText(vou.getDesc());
        viewHolder.amount.setText(vou.getAmount());

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Integer id = vou.getId();
                Intent iVou = new Intent(context, FormVoucherActivity.class);
                iVou.putExtra("action","update");
                iVou.putExtra("id_vou", vou.getId());
                iVou.putExtra("start_date", viewHolder.startDate.getText());
                iVou.putExtra("end_date", viewHolder.endDate.getText());
                iVou.putExtra("name", vou.getName());
                iVou.putExtra("code", vou.getCode());
                iVou.putExtra("desc", vou.getDesc());
                iVou.putExtra("type", vou.getType());
                iVou.putExtra("quantity", vou.getAmount());
                iVou.putExtra("category", Integer.parseInt(vou.getAsCategory()));
                iVou.putExtra("nominal", vou.getNominal());
                iVou.putExtra("percent", vou.getPercen());
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
        private TextView title, discount, startDate, endDate, code, desc, amount, status, asCategory, type, btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.vou_title);
            discount = (TextView) itemView.findViewById(R.id.vou_discount);
            startDate = (TextView) itemView.findViewById(R.id.vou_date);
            endDate = (TextView) itemView.findViewById(R.id.vou_endDate);
            code = (TextView) itemView.findViewById(R.id.vou_code);
            desc = (TextView) itemView.findViewById(R.id.vou_desc);
//            asCategory = (TextView) itemView.findViewById(R.id.vou_as_category);
            type = (TextView) itemView.findViewById(R.id.vou_type);
            btnEdit = (TextView) itemView.findViewById(R.id.vou_edit);
            amount = (TextView) itemView.findViewById(R.id.vou_quantity);
//            status = (TextView) itemView.findViewById(R.id.vou_status);


        }
    }
}

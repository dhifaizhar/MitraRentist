package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.PriceModul;
import id.rentist.mitrarentist.tools.PricingTools;
import id.rentist.mitrarentist.tools.Tools;

/**
 * Created by Nugroho Tri Pambud on 10/11/2017.
 */

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    private final List<PriceModul> mPrice;
    private Context context;


    public PriceAdapter(Context context, List<PriceModul> mPrice) {
        super();
        this.mPrice = mPrice;
        this.context = context;
    }

    @Override
    public PriceAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.price_item_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PriceAdapter.ViewHolder viewHolder, int i) {
        final PriceModul price = mPrice.get(i);
        if (price.getRangeName().equals("BASECOST")){
            viewHolder.range_name.setText("Harga Dasar");

            viewHolder.period.setVisibility(View.GONE);
            viewHolder.period_text.setVisibility(View.GONE);
        } else {
            viewHolder.range_name.setText(price.getRangeName());
            viewHolder.period.setText(Tools.dateFormat(price.getStartDate().substring(0,10).replace("-","/"))
                    + " sd " + Tools.dateFormat(price.getEndDate().substring(0,10).replace("-","/")));
        }

        viewHolder.price.setText(price.getPrice());
        if (!price.getDriverPrice().equals("0")){
            viewHolder.con_driver_price.setVisibility(View.VISIBLE);
            viewHolder.driver_price.setText(PricingTools.PriceStringFormat(price.getDriverPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return mPrice.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView range_name, price, driver_price, startDate, endDate, period, period_text;
        LinearLayout con_driver_price;
        public ViewHolder(View itemView) {
            super(itemView);
            range_name = (TextView) itemView.findViewById(R.id.as_advance_rangename);
            price = (TextView) itemView.findViewById(R.id.as_advance_price);
            period_text = (TextView) itemView.findViewById(R.id.period);
            period = (TextView) itemView.findViewById(R.id.as_advance_period);
            driver_price = (TextView) itemView.findViewById(R.id.as_driver_price);
            con_driver_price = (LinearLayout) itemView.findViewById(R.id.con_driver_price);
        }
    }
}

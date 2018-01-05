package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemDeliveryPrice;
import id.rentist.mitrarentist.tools.PricingTools;

/**
 * Created by Nugroho Tri Pambud on 12/12/2017.
 */

public class DeliveryPriceAdapter extends RecyclerView.Adapter<DeliveryPriceAdapter.ViewHolder> implements View.OnClickListener {

    private List<ItemDeliveryPrice> mDelivPrice;
    private Context context;
    private String from;
    private static final String TAG = "DeliveryPriceAdapter";

    public DeliveryPriceAdapter(final Context context, final List<ItemDeliveryPrice> mDelivPrice, String from) {
        super();
        this.mDelivPrice = mDelivPrice;
        this.context = context;
        this.from = from;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliv_price_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final ItemDeliveryPrice item = mDelivPrice.get(i);

        if (from.equals("asset")){
            viewHolder.frommenu.setVisibility(View.GONE);
        }else{
            viewHolder.frommenu.setVisibility(View.GONE);
            viewHolder.fromasset.setVisibility(View.GONE);
        }

        String[] asset_category = context.getResources().getStringArray(R.array.asset_category_entries);
        String categoryText = "";

        if(!item.getCategory().equals("")){
            viewHolder.row_cat.setVisibility(View.VISIBLE);
            String str = item.getCategory();
            String[] items = str.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            Log.e(TAG, items.toString());
            if (items.length > 0){
                for (i = 1;i <= items.length;i++){
                    categoryText += asset_category[Integer.parseInt(items[i-1])-1];
                    if(i < items.length){
                        categoryText += ", ";
                    }
                }
            }
            viewHolder.category.setText(categoryText);
        }else{
            viewHolder.cardDet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("set-delivery");
                    intent.putExtra("id_delivery",item.getId());
                    intent.putExtra("delivery_detail",
                            "Jarak Maksimal: " + item.getMaxDistance() + " KM\n " +
                                    "Jarak Gratis Ongkir: " + item.getDistaceFree() + " KM\n" +
                                    "Biaya per KM: " + PricingTools.PriceStringFormat(item.getPricePerKM())
                    );
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }

        viewHolder.distance.setText(item.getMaxDistance() + " KM");
        viewHolder.free.setText(item.getDistaceFree() + " KM");
        viewHolder.price.setText(PricingTools.PriceStringFormat(item.getPricePerKM()));

    }

    @Override
    public int getItemCount() {
        return mDelivPrice.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView category, distance, price, free;
        private CardView cardDet;
        LinearLayout row_cat, fromasset, frommenu;

        public ViewHolder(View itemView){
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.dp_category);
            distance = (TextView) itemView.findViewById(R.id.dp_max_distance);
            price = (TextView) itemView.findViewById(R.id.dp_price);
            row_cat = (LinearLayout) itemView.findViewById(R.id.row_category);
            fromasset = (LinearLayout) itemView.findViewById(R.id.from_asset);
            frommenu = (LinearLayout) itemView.findViewById(R.id.from_menu);
            cardDet = (CardView) itemView.findViewById(R.id.card_det);
            free = (TextView) itemView.findViewById(R.id.dp_free);

        }
    }
}

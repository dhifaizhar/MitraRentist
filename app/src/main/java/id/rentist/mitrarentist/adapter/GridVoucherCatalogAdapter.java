package id.rentist.mitrarentist.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.VoucherDetailActivity;
import id.rentist.mitrarentist.modul.VoucherCatalogModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by Nugroho Tri Pambud on 12/1/2017.
 */

public class GridVoucherCatalogAdapter extends RecyclerView.Adapter<GridVoucherCatalogAdapter.ViewHolder> {
    private List<VoucherCatalogModul> itemVoucher;
    private SessionManager sm;
    private Context context;
    private static final String TAG = "VoucherCatalogAdapter";
    private static final String TOKEN = "secretissecret";

    public GridVoucherCatalogAdapter(Context applicationContext, List<VoucherCatalogModul> mVoucher) {
        super();
        this.itemVoucher = mVoucher;
        this.context = applicationContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_voucher_catalog_bakcup, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name, price, btnBuy;
        private ImageView imgThumbnail;
        private CardView cardVoucherDet;

        public ViewHolder(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            price = (TextView) itemView.findViewById(R.id.item_price);
            btnBuy = (TextView) itemView.findViewById(R.id.btn_buy);
            cardVoucherDet = (CardView) itemView.findViewById(R.id.card_gridview_voucher);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.item_img);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final VoucherCatalogModul vou = itemVoucher.get(i);

        viewHolder.name.setText(vou.getName());
        viewHolder.price.setText(vou.getPrice() + " Poin");



        viewHolder.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder scheduleAlert = new AlertDialog.Builder(v.getRootView().getContext());
                scheduleAlert.setMessage("Beli Kupon ?");
                scheduleAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        purchaseVoucher(vou.getId());
                    }
                });
                scheduleAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                    }
                });
                scheduleAlert.show();
            }
        });

        viewHolder.cardVoucherDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iVou = new Intent(context, VoucherDetailActivity.class);
                iVou.putExtra("id_voucher", vou.getId());
                iVou.putExtra("voucher_name", vou.getName());
                iVou.putExtra("voucher_poin", vou.getPrice());
                iVou.putExtra("voucher_desc", vou.getDescription());
                iVou.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   context.startActivity(iVou);

            }
        });

    }

    private void purchaseVoucher(final String id) {
        sm = new SessionManager(context);
        String id_tenant = sm.getPreferences("id_tenant");

        String URL = AppConfig.URL_VOUCHER_PURCHASE + id_tenant;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {
                            Toast.makeText(context, "Kupon Berhasil dibeli",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Voucher List Fetch Error : " +  error.toString());
                Toast.makeText(context, "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_voucher_catalog", id);
                Log.e(TAG, "Value Object : " + keys.toString());
                return keys;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        queue.add(strReq);
    }

    @Override
    public int getItemCount() {
        return itemVoucher.size();
    }
}

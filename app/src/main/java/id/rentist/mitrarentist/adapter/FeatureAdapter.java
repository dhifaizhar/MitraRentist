package id.rentist.mitrarentist.adapter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.FormFeatureActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemFeatureModul;
import id.rentist.mitrarentist.tools.AppConfig;

/**
 * Created by mdhif on 12/10/2017.
 */

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> implements View.OnClickListener   {
    private List<ItemFeatureModul> mFeature;
    private Context context;
    private AsyncTask mFeatureTask = null;
    private AlertDialog.Builder showAlert;
    private AlertDialog alertDialog;
    private ProgressDialog pDialog;
    private static final String TAG = "FeatureAdapter";
    private static final String TOKEN = "secretissecret";

    public FeatureAdapter(final Context context, final List<ItemFeatureModul> mFeature){
        super();
        this.mFeature = mFeature;
        this.context = context;
        pDialog = new ProgressDialog(this.context);
        pDialog.setCancelable(false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mFeature.size();
    }

    @Override
    public void onClick(View v) {}

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nama, price, qty;
        private CardView cardView;
        private LinearLayout linearAction;
        private ImageButton editBtn;
        private ImageView hideBtn, deleteBtn;

        public ViewHolder(View itemView){
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.fr_name);
            price = (TextView) itemView.findViewById(R.id.fr_price);
            qty = (TextView) itemView.findViewById(R.id.fr_qty);
            cardView = (CardView) itemView.findViewById(R.id.card_view_feature);
//            linearAction = (LinearLayout) itemView.findViewById(R.id.action_feature);
            editBtn = (ImageButton) itemView.findViewById(R.id.fr_btn_edit);
            deleteBtn = (ImageView) itemView.findViewById(R.id.fr_btn_delete);
            hideBtn = (ImageView) itemView.findViewById(R.id.fr_hide);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i ){
        final ItemFeatureModul feature = mFeature.get(i);
        final int iPosition = i;
//        simpan value dalam object
        viewHolder.nama.setText(feature.getName());
        viewHolder.price.setText(feature.getPrice() + " IDR");
        viewHolder.qty.setText("Stok : " + feature.getQty());
//        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                viewHolder.linearAction.setVisibility(v.VISIBLE);
//                return true;
//            }
//        });
//        viewHolder.linearAction.setVisibility(View.VISIBLE);
//        viewHolder.hideBtn.setVisibility(View.GONE);
//        viewHolder.editBtn.setVisibility(View.GONE);

//        viewHolder.hideBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewHolder.linearAction.setVisibility(v.GONE);
//            }
//        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iFeature = new Intent(context, FormFeatureActivity.class);
                iFeature.putExtra("action","update");
                iFeature.putExtra("id_feature",feature.getId());
                iFeature.putExtra("fname",feature.getName());
                iFeature.putExtra("fprice",feature.getPrice());
                iFeature.putExtra("fqty",feature.getQty());
                iFeature.putExtra("id_asset_cat",feature.getIdAssetCat());
                context.startActivity(iFeature);
            }
        });
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICK","DELETE");
                deleteFeatureItem(feature.getId(), iPosition);
            }
        });
    }

    private void deleteFeatureItem(final String id, final int position) {
        showAlert = new AlertDialog.Builder(this.context);
        showAlert.setMessage("Hapus fitur tambahan ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                pDialog.setMessage("loading ...");
                showProgress(true);
                new FeatureAdapter.deleteFeatureTask(String.valueOf(id), position).execute();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        alertDialog = showAlert.create();
        alertDialog.show();
    }

    private class deleteFeatureTask  extends AsyncTask<String, String, String> {
        private final String mFeature;
        private final int mPosition;
        private String errorMsg, responseFeature;

        private deleteFeatureTask(String feature, int position) {
            mFeature = feature;
            mPosition = position;
        }

        @Override
        protected String doInBackground(String... params) {
            String feature_url = AppConfig.URL_DELETE_FEATURE;
            Log.e(TAG, "Feature Fetch URL: " + feature_url);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, feature_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseFeature = response;
                    Log.e(TAG, "FResponse: " + responseFeature);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Feature Fetch Error : " + errorMsg);
                    Toast.makeText(context, "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_feature_item", mFeature);
                    Log.e(TAG, "Delete Data : " + String.valueOf(keys));
                  return keys;
//                    return null;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", TOKEN);
                    return keys;
                }
            };

            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return responseFeature;
        }

        @Override
        protected void onPostExecute(String feature) {
            mFeatureTask = null;
            showProgress(false);

            if(feature != null){
                Log.e(TAG, "Response Array: " + String.valueOf(feature));
                try {
                    JSONArray arr = new JSONArray(feature);
                    Log.e(TAG, "Response Array: " + String.valueOf(arr));

                    if(arr.length() > 0){
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Toast.makeText(context,"Data berhasil dihapus", Toast.LENGTH_LONG).show();
                            removeAt(mPosition);
                            Log.e(TAG, "Response : " + String.valueOf(obj));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"Gagal menghapus data", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(context,"Gagal menghapus data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mFeatureTask = null;
            showProgress(false);
        }

    }

    private void removeAt(int position) {
        mFeature.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mFeature.size());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if(show){
            if (!pDialog.isShowing()){
                pDialog.show();
            }
        }else{
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }
}

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

import id.rentist.mitrarentist.DetailAsetActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.modul.ItemAsetModul;
import id.rentist.mitrarentist.tools.AppConfig;

/**
 * Created by mdhif on 19/06/2017.
 */

public class AsetAdapter extends RecyclerView.Adapter<AsetAdapter.ViewHolder> {

    private final List<ItemAsetModul> mAset;
    private Context context;
    private AsyncTask mAsetTask = null;

    AlertDialog.Builder showAlert;
    AlertDialog alertDialog;
    ProgressDialog pDialog;
    private static final String TAG = "AssetAdapter";
    private static final String TOKEN = "secretissecret";
    String category, category_url;

    public AsetAdapter(final Context context, final List<ItemAsetModul> mAset, final String category){
        super();
        this.mAset = mAset;
        this.context = context;
        this.category = category;
        pDialog = new ProgressDialog(this.context);
        pDialog.setCancelable(false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.aset_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return mAset.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mark, year, status, subcat, plat;
        private ImageView imgThumbnail;
        private ImageButton deleteAsset;
        private CardView cardDetAset;

        public ViewHolder(View itemView){
            super(itemView);
            mark = (TextView) itemView.findViewById(R.id.as_mark_det);
            plat = (TextView) itemView.findViewById(R.id.as_aset_plat);
            subcat = (TextView) itemView.findViewById(R.id.as_subcat_det);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.as_thumb_aset);
            year = (TextView) itemView.findViewById(R.id.as_year_det);
            status = (TextView) itemView.findViewById(R.id.as_status_det);
            cardDetAset = (CardView) itemView.findViewById(R.id.card_view_aset);
            deleteAsset = (ImageButton) itemView.findViewById(R.id.btn_delete);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i ){
        final ItemAsetModul as = mAset.get(i);
        final int iPosition = i;

//        simpan value dalam object
        viewHolder.mark.setText(as.getMark());
        viewHolder.imgThumbnail.setImageResource(as.getThumbnail());

        viewHolder.status.setText(as.getStatus());
        viewHolder.subcat.setText(as.getSubCat());
        if (!category.equals("10") && !category.equals("3")){
            viewHolder.year.setText(as.getYear());
            viewHolder.plat.setText(as.getPlat());
        }

        viewHolder.cardDetAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAset = new Intent(context, DetailAsetActivity.class);
                iAset.putExtra("id_asset", as.getAssetId());
                iAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(iAset);
            }
        });
        viewHolder.deleteAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAssetItem(as.getAssetId(), iPosition);
            }
        });
    }

    private void deleteAssetItem(final int id, final int position) {
        showAlert = new AlertDialog.Builder(context);
        showAlert.setMessage("Hapus aset ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                pDialog.setMessage("loading ...");
                showProgress(true);
                new AsetAdapter.deleteAssetTask(String.valueOf(id), position).execute();
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

    private class deleteAssetTask  extends AsyncTask<String, String, String> {
        private final String mAsset;
        private final int mPosition;
        private String errorMsg, responseAsset;

        private deleteAssetTask(String asset, int position) {
            mAsset = asset;
            mPosition = position;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            switch (category) {
                case "1":
                    category_url = AppConfig.URL_DELETE_MOBIL;
                    break;
                case "2":
                    category_url = AppConfig.URL_ADD_MOTOR;
                    break;
                case "3":
                    category_url = AppConfig.URL_DELETE_YACHT;
                    break;
                case "10":
                    category_url = AppConfig.URL_DELETE_BICYCLE;
                    break;
            }
            Log.e(TAG, "URL : " + category_url);

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, category_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseAsset = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Asset Fetch Error : " + errorMsg);
                    Toast.makeText(context, "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_asset", mAsset);
                    Log.e(TAG, "Delete Data : " + String.valueOf(keys));
//                    return keys;
                    return null;
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

            return responseAsset;
        }

        @Override
        protected void onPostExecute(String user) {
            mAsetTask = null;
            showProgress(false);

            if(user != null){
                try {
                    JSONArray arr = new JSONArray(user);
                    Log.e(TAG, "Response Array: " + String.valueOf(arr));

                    if(arr.length() > 0){
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);

                            Log.e(TAG, "Response : " + String.valueOf(obj));

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context,"Data berhasil dihapus", Toast.LENGTH_LONG).show();
                removeAt(mPosition);
            }else{
                Toast.makeText(context,"Gagal menghapus data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAsetTask = null;
            showProgress(false);
        }

    }

    private void removeAt(int position) {
        mAset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mAset.size());
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

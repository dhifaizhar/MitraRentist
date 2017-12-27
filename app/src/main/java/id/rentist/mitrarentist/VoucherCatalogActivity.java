package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.GridVoucherCatalogAdapter;
import id.rentist.mitrarentist.modul.VoucherCatalogModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class VoucherCatalogActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<VoucherCatalogModul> mVou = new ArrayList<>();
    private SessionManager sm;
    private SpinKitView pBar;
    private ProgressDialog pDialog;

    String tenant;
    LinearLayout noData;

    private static final String TAG = "VoucherCatalogActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_catalog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Katalog Kupon");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        sm = new SessionManager(getApplicationContext());
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        noData = (LinearLayout) findViewById(R.id.no_data);

        getVoucherData();

    }

    private void getVoucherData(){
        pDialog.setMessage("loading ...");
        showProgress(true);

        String URL = AppConfig.URL_VOUCHER_CATALOG;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false);

                        if (response != null) {
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                JSONArray data = responseObject.getJSONArray("data");

                                Log.e(TAG, "Voucher : " + data);
                                mVou.clear();
                                if(data.length() > 0){
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject voucher = data.getJSONObject(i);

                                        VoucherCatalogModul itemModul = new VoucherCatalogModul();
                                        itemModul.setId(voucher.getString("id"));
                                        itemModul.setName(voucher.getString("name"));
                                        itemModul.setPrice(voucher.getString("price"));
                                        itemModul.setDescription(voucher.getString("description"));
                                        itemModul.setQuantity(voucher.getString("quantity"));
                                        itemModul.setCreatedDate(voucher.getString("createdAt"));

                                        mVou.add(itemModul);
                                    }

                                    mRecyclerView = (RecyclerView) findViewById(R.id.vc_recyclerView);
                                    mRecyclerView.setHasFixedSize(true);
                                    mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
                                    mAdapter = new GridVoucherCatalogAdapter(getApplicationContext(), mVou);
                                    mRecyclerView.setLayoutManager(mLayoutManager);
                                    mRecyclerView.setAdapter(mAdapter);
                                    noData.setVisibility(View.GONE);

                                }else{
                                    noData.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Tidak Ada Kupon" , Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e(TAG, "Get Voucher List Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

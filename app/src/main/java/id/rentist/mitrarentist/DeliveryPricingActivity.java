package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.DeliveryPriceAdapter;
import id.rentist.mitrarentist.modul.ItemDeliveryPrice;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class DeliveryPricingActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private List<ItemDeliveryPrice> mDelivPrice = new ArrayList<>();
    private ProgressDialog pDialog;
    SessionManager sm;
    LinearLayout noData;

    String tenant, category;

    private static final String TAG = "AssetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_pricing);
        setTitle("Daftar Biaya Pengiriman");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        sm = new SessionManager(getApplicationContext());
        noData = (LinearLayout) findViewById(R.id.no_data);
        TextView toFormAdd = (TextView) findViewById(R.id.toFormAdd);
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));

        getDataList();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataList();
            }
        });

        toFormAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryPricingActivity.this, FormDeliveryPriceActivity.class);
                intent.putExtra("from", "list");
                startActivity(intent);
            }
        });
    }

    private void getDataList() {
        pDialog.setMessage("Memuat Data...");
        showProgress(true);
        String URL = AppConfig.URL_LIST_DELIVERY_PRICE ;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        showProgress(false);

                        if (response != null) {
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                JSONArray dataArray = responseObject.getJSONArray("data");
                                mDelivPrice.clear();
                                if (dataArray.length() > 0) {
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject itemObject = dataArray.getJSONObject(i);
                                        Log.e(TAG, "Data : " + i +"|"+ itemObject);

                                        ItemDeliveryPrice delivModul = new ItemDeliveryPrice();
                                        delivModul.setCategory(itemObject.getString("id_asset_category"));
                                        delivModul.setMaxDistance(itemObject.getString("max_distance"));
                                        delivModul.setPricePerKM(itemObject.getString("price_per_km"));
                                        delivModul.setDistaceFree(itemObject.getString("max_distance_free"));
                                        mDelivPrice.add(delivModul);
                                    }

                                    mRecyclerView = (RecyclerView) findViewById(R.id.dp_recyclerView);
                                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    mAdapter = new DeliveryPriceAdapter(getApplicationContext(),mDelivPrice,"menu");
                                    mRecyclerView.setLayoutManager(mLayoutManager);
                                    mRecyclerView.setAdapter(mAdapter);
                                    noData.setVisibility(View.GONE);
                                } else {
                                    noData.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                noData.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Deliver List Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_tenant", tenant);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent iAdd = new Intent(DeliveryPricingActivity.this, FormDeliveryPriceActivity.class);
            iAdd.putExtra("from", "list");
            startActivity(iAdd);
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataList();
    }
}

package id.rentist.mitrarentist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

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

public class ListDeliveryPriceActivity extends AppCompatActivity {
    RecyclerView mRecycleView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private SpinKitView pBar;
    private SessionManager sm;
    private List<ItemDeliveryPrice> mItem = new ArrayList<>();
    private Intent iDelivery;

    String tenant;
    LinearLayout no_delivery;
    TextView add_delivery;

    private static final String TAG = "ListDeliveryActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_delivery_price);
        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        mRecycleView = (RecyclerView) findViewById(R.id.rv_delivery);
        no_delivery = (LinearLayout) findViewById(R.id.no_delivery);
        add_delivery = (TextView) findViewById(R.id.add_delivery);

        no_delivery.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
        iDelivery = getIntent();

        add_delivery.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent iDeliveryPrce = new Intent(ListDeliveryPriceActivity.this, FormDeliveryPriceActivity.class);
                iDeliveryPrce.putExtra("from", "aset");
                iDeliveryPrce.putExtra("id_asset_category", iDelivery.getStringExtra("id_asset_category"));
                startActivity(iDeliveryPrce);
            }
        });

        getDeliveryPrice(tenant, iDelivery.getStringExtra("id_asset_category"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("set-delivery"));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String id_delivery = intent.getStringExtra("id_delivery");
            String delivery_detail = intent.getStringExtra("delivery_detail");

            Intent returnIntent = new Intent();
            returnIntent.putExtra("id_delivery", id_delivery);
            returnIntent.putExtra("delivery_detail", delivery_detail);
            ListDeliveryPriceActivity.this.setResult(RESULT_OK, returnIntent);

            finish();
        }
    };

    private void getDeliveryPrice(final String id_tenant, final String id_asset_category){
        String URL = AppConfig.URL_LIST_DELIVERY_PRICE ;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pBar.setVisibility(View.GONE);

                        Log.e(TAG, response);
                        if (response != null) {
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                JSONArray dataArray = responseObject.getJSONArray("data");
                                List<ItemDeliveryPrice> mDelivPrice = new ArrayList<>();

                                mDelivPrice.clear();
                                if (dataArray.length() > 0) {
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject itemObject = dataArray.getJSONObject(i);
                                        Log.e(TAG, "Data : " + i +"|"+ itemObject);

                                        ItemDeliveryPrice delivModul = new ItemDeliveryPrice();
                                        delivModul.setCategory("");
                                        delivModul.setMaxDistance(itemObject.getString("max_distance"));
                                        delivModul.setPricePerKM(itemObject.getString("price_per_km"));
                                        delivModul.setId(itemObject.getString("id"));
                                        mDelivPrice.add(delivModul);
                                    }
                                    RecyclerView.Adapter mAdapter;
                                    RecyclerView.LayoutManager mLayoutManager;
                                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    mAdapter = new DeliveryPriceAdapter(getApplicationContext(),mDelivPrice,"asset");
                                    mRecycleView.setLayoutManager(mLayoutManager);
                                    mRecycleView.setAdapter(mAdapter);
                                    mRecycleView.setVisibility(View.VISIBLE);
                                    no_delivery.setVisibility(View.GONE);

                                } else {
                                    mRecycleView.setVisibility(View.GONE);

                                    no_delivery.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                no_delivery.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Deliver List Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_tenant", id_tenant);
                keys.put("id_asset_category", id_asset_category);
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
    public void onResume(){
        super.onResume();
        getDeliveryPrice(tenant, iDelivery.getStringExtra("id_asset_category"));

    }
}

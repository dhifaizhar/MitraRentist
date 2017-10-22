package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.AsyncTask;
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

import id.rentist.mitrarentist.adapter.FeatureAdapter;
import id.rentist.mitrarentist.modul.ItemFeatureModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FeatureActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    AsyncTask mFeatureTask = null;
    private List<ItemFeatureModul> mFeature = new ArrayList<>();
    private SpinKitView pBar;
    SessionManager sm;
    JSONObject dataObject, objectFeature, objectFeatureDetail;
    JSONArray dataArray;
    Intent iFeature;

    private static final String TAG = "FeatureActivity";
    private static final String TOKEN = "secretissecret";
    private String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        setTitle("Fitur Tambahan");

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getFeatureDataList(tenant);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRecyclerView != null) {
                    mFeature.clear();
                    mRecyclerView.setAdapter(null);
                }
                getFeatureDataList(tenant);
            }
        });
    }

    private void getFeatureDataList(String tenant) {
        pBar.setVisibility(View.VISIBLE);

        if (mFeatureTask != null) {
            return;
        }

        new getFeatureListTask(tenant).execute();
    }

    private class getFeatureListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseFeature;

        private getFeatureListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String newURL = AppConfig.URL_LIST_FEATURE + mTenant;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseFeature = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Feature Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    // Posting parameters to login url
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
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
            String aId, aName, aPrice, aQty, aUseQty;
            Integer dataLength;

            if (feature != null) {
                try {
                    dataObject = new JSONObject(feature);
                    dataArray = new JSONArray(dataObject.getString("data"));
                    dataLength = dataArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < dataArray.length(); i++) {
                            errorMsg = "-";
                            objectFeature = dataArray.getJSONObject(i);
                            try {
                                objectFeatureDetail = new JSONObject(objectFeature.getString("id_additional_feature"));
                                if(objectFeatureDetail.length() > 0){
                                    aName = objectFeatureDetail.getString("feature_name");
                                }else{
                                    aName = "Unknown Item";
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                                aName = "Unknown Item";
                            }

                            aId = objectFeature.getString("id");
                            aQty = objectFeature.getString("quantity");
                            aUseQty = objectFeature.getString("used_quantity");
                            aPrice = objectFeature.getString("price");
                            Log.e(TAG, "What Data : " + String.valueOf(objectFeature));

                            ItemFeatureModul featureModul = new ItemFeatureModul();
                            featureModul.setId(aId);
                            featureModul.setName(aName);
                            featureModul.setPrice(aPrice);
                            featureModul.setQty(aQty);
                            featureModul.setUseQty(aUseQty);
                            mFeature.add(featureModul);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.fr_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new FeatureAdapter(FeatureActivity.this,mFeature);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Anda belum memiliki Fitur Tambahan";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Anda belum memiliki Fitur Tambahan";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mFeatureTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
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
            iFeature = new Intent(FeatureActivity.this, FormFeatureActivity.class);
            iFeature.putExtra("action","add");
            startActivity(iFeature);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRecyclerView != null) {
            mFeature.clear();
            mRecyclerView.setAdapter(null);
        }
        getFeatureDataList(tenant);
    }

}

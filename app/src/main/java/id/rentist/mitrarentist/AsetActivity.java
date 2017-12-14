package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.adapter.GridAsetAdapter;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class AsetActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private SessionManager sm;

    String tenant;

    private static final String TAG = "AsetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aset);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Kategori Aset");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sm = new SessionManager(getApplicationContext());
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        Log.e("ASET","SumCat : " + sm.getIntPreferences("sum_cat").toString());

        new AsetActivity.getDataTask(tenant).execute();
        getGridAset();
    }

    public void getGridAset() {
        mRecyclerView = (RecyclerView) findViewById(R.id.ac_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        mAdapter = new GridAsetAdapter(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class getDataTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseData;

        private getDataTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DASHBOARD_DATA + mTenant;
            Log.d(TAG, "Request to : "+ URL);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseData = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Dashboard Data Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
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

            return responseData;
        }

        @Override
        protected void onPostExecute(String user) {
            Log.d(TAG, "response");

            if(user != null){
                try {
                    JSONObject dataObject = new JSONObject(user);
                    JSONObject assetObject = new JSONObject(String.valueOf(dataObject.getJSONObject("asset")));
                    Log.d(TAG, "Response : " + dataObject.toString());

                    sm.setIntPreferences("sum_car", assetObject.getInt("mobil"));
                    sm.setIntPreferences("sum_motor", assetObject.getInt("motor"));
                    sm.setIntPreferences("sum_yacht", assetObject.getInt("yacht"));
                    sm.setIntPreferences("sum_medic", assetObject.getInt("medical"));
                    sm.setIntPreferences("sum_photography", assetObject.getInt("photography"));
                    sm.setIntPreferences("sum_toys", assetObject.getInt("toys"));
                    sm.setIntPreferences("sum_adventure", assetObject.getInt("watersport"));
                    sm.setIntPreferences("sum_maternity", assetObject.getInt("maternity"));
                    sm.setIntPreferences("sum_electronic", assetObject.getInt("electronic"));
                    sm.setIntPreferences("sum_bicycle", assetObject.getInt("bicycle"));
                    sm.setIntPreferences("sum_office", assetObject.getInt("officetools"));
                    sm.setIntPreferences("sum_fashion", assetObject.getInt("fashion"));
                    getGridAset();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void onRestart() {
        super.onRestart();
        new AsetActivity.getDataTask(tenant).execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delivery_pricing_option, menu);
        getMenuInflater().inflate(R.menu.menu_preference_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delivery_pricing) {
            Intent iDeliv = new Intent(AsetActivity.this, DeliveryPricingActivity.class);
            startActivity(iDeliv);
        }

        if (id == R.id.action_preference) {
            Intent iSetup = new Intent(AsetActivity.this, SetupCategoryActivity.class);
            startActivityForResult(iSetup, 2);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        getGridAset();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            getGridAset();
        }

    }

}

package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormFeatureActivity extends AppCompatActivity {
    AsyncTask mDetailFeatureTask = null;
    ProgressDialog pDialog;
    SessionManager sm;
    Intent formFeature;

    int featureId;
    String tenant, fId, category = "1";
    TextView name, price, qty;

    private static final String TAG = "FormFeatureActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_feature);
        setTitle("Fitur Tambahan");

        formFeature = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        name = (TextView)findViewById(R.id.fr_feature_name);
        price = (TextView)findViewById(R.id.fr_price);
        qty = (TextView)findViewById(R.id.fr_qty);

        // set content control value
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        if(formFeature.getStringExtra("action").equals("update")){
            fId = formFeature.getStringExtra("id_feature");
            Log.e(TAG, "Id Tenant Form Feature : " + fId);
            name.setText(formFeature.getStringExtra("fname"));
            price.setText(formFeature.getStringExtra("fprice"));
            qty.setText(formFeature.getStringExtra("fqty"));
        }

        // set action
    }

    private void formFeatureTenant(String tenant, String category, String id) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        if(formFeature.getStringExtra("action").equals("add")){
            new featureAddTask(tenant, category).execute();
        }else if(formFeature.getStringExtra("action").equals("update")){
            new featureUpdateTask(tenant, id).execute();
        }
    }

    private class featureAddTask extends AsyncTask<String, String, String>{
        final String mTenant;
        final String mCategory;
        private String errorMsg, responseFeature;

        private featureAddTask(String tenant, String category) {
            mTenant = tenant;
            mCategory = category;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_FEATURE + mCategory;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseFeature = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant Feature Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("feature_name", name.getText().toString());
                    keys.put("price", price.getText().toString());
                    keys.put("quantity", qty.getText().toString());
                    Log.e(TAG, "Key Body : " + keys.toString());
                    return keys;
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
            mDetailFeatureTask = null;
            showProgress(false);

            if(feature != null){
                Toast.makeText(getApplicationContext(),"Sukses menyimpan data.", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mDetailFeatureTask = null;
            showProgress(false);
        }

    }

    private class featureUpdateTask extends AsyncTask<String, String, String>{
        final String mTenant;
        final String idFeature;
        private String errorMsg, responseFeature;

        private featureUpdateTask(String tenant, String id) {
            mTenant = tenant;
            idFeature = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_FEATURE;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseFeature = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant Feature Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_feature_item", idFeature);
                    keys.put("id_tenant", mTenant);
                    keys.put("feature_name", name.getText().toString());
                    keys.put("price", price.getText().toString());
                    keys.put("quantity", qty.getText().toString());
                    Log.e(TAG, "Key Body : " + keys.toString());
                    return keys;
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
            mDetailFeatureTask = null;
            showProgress(false);

            if(feature != null){
                Toast.makeText(getApplicationContext(),"Sukses mengubah data.", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mDetailFeatureTask = null;
            showProgress(false);
        }
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
        this.finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            formFeatureTenant(tenant, category, fId);
        }

        return super.onOptionsItemSelected(item);
    }

}

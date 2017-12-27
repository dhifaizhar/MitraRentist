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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormFeatureActivity extends AppCompatActivity {
    AsyncTask mDetailFeatureTask = null;
    ProgressDialog pDialog;
    SessionManager sm;
    Intent formFeature;
    Map<Integer,String> nameMap;

    int featureId;
    String tenant, fId, category = "1", fIdName;
    TextView name, price, qty, aCatValue;
    Spinner featureName, aCat;
    Button bSaveButton;


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
        featureName = (Spinner) findViewById(R.id.fr_name);
        aCat = (Spinner) findViewById(R.id.fr_asset_cat);
        aCatValue = (EditText) findViewById(R.id.fr_asset_cat_val);

        aCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aCatValue.setText(String.valueOf(position+1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                aCatValue.setText("1");
            }
        });

        getfeatureName();

        // set content control value
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        if(formFeature.getStringExtra("action").equals("update")){
            fId = formFeature.getStringExtra("id_feature");
            name.setText(formFeature.getStringExtra("fname"));
            price.setText(formFeature.getStringExtra("fprice"));
            qty.setText(formFeature.getStringExtra("fqty"));
            aCatValue.setText(formFeature.getStringExtra("id_asset_cat"));
            aCat.setSelection(Integer.parseInt(formFeature.getStringExtra("id_asset_cat"))-1);
            Log.e(TAG, "Id Tenant Form Feature : " + formFeature.getStringExtra("id_asset_cat"));

        }

        // set action
        bSaveButton = (Button) findViewById(R.id.btn_save);
        bSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formFeatureTenant(tenant, category, fId);
            }
        });
    }

    public void getfeatureName() {

        String URL = AppConfig.URL_FEATURE_NAME;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String data) {
                try {
                    JSONArray respArray = new JSONArray(data);
                    if(respArray.length() > 0){
                        String[] nameArray = new String[respArray.length()];
                        nameMap = new HashMap<Integer,String>();
                        for (int i = 0; i < respArray.length(); i++) {
                            JSONObject respObject = respArray.getJSONObject(i);
                            nameMap.put(i,respObject.getString("id"));
                            nameArray[i] = respObject.getString("feature_name");
                        }

                        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, nameArray);
                        nameAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
                        featureName.setAdapter(nameAdapter);

                        featureName.setEnabled(true);
                        featureName.setClickable(true);

                        featureName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                fIdName = nameMap.get(featureName.getSelectedItemPosition());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Feature Name Fetch Error : " + error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("token", TOKEN);
                return keys;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void formFeatureTenant(String tenant, String category, String id) {
        Boolean valid = formValidation();
        if(valid.equals(true)) {
            pDialog.setMessage("loading ...");
            showProgress(true);
            if (formFeature.getStringExtra("action").equals("add")) {
                new featureAddTask(tenant, category).execute();
            } else if (formFeature.getStringExtra("action").equals("update")) {
                new featureUpdateTask(tenant, id).execute();
            }
        }
    }

    private boolean formValidation(){
        Boolean valid = true;
        price.setError(null);
        qty.setError(null);

        if(price.getText().toString().isEmpty()){
            price.setError(getString(R.string.error_field_required));
            valid = false;
        }

        if(qty.getText().toString().isEmpty()){
            qty.setError(getString(R.string.error_field_required));
            valid = false;
        }

        return valid;
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
                    keys.put("feature_name", fIdName);
                    keys.put("price", price.getText().toString());
                    keys.put("quantity", qty.getText().toString());
                    keys.put("id_asset_category", String.valueOf(aCatValue.getText()));
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
                    keys.put("feature_name", fIdName);
                    keys.put("price", price.getText().toString());
                    keys.put("quantity", qty.getText().toString());
                    keys.put("id_asset_category", String.valueOf(aCatValue.getText()));
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
//        getMenuInflater().inflate(R.menu.menu_save_option, menu);
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

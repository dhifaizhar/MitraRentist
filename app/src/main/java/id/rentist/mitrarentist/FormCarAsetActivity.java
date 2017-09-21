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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class FormCarAsetActivity extends AppCompatActivity {
    private AsyncTask mAddAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    Intent iFormAsset;

    ImageView aImg;
    TextView aMerk, aType, aPlat, aYear, aColor, aRegNum,
            aEngCap, aFuel, aSeat, aAsuracePrice, aLowPrice,
            aNormalPrice, aHighPrice;
    Integer idAsset;
    String aLatitude, aLongitude, aAddress, aRentPackage, tenant, category;
    CheckBox aAc, aAb, aDriver, aAssurace;
    RadioGroup aTransmisionGroup;
    RadioButton aTransmisionButton;
    Button btnImgUpload;

    private static final String TAG = "FormAssetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_car_aset);
        setTitle("Aset Form");

        iFormAsset = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btnImgUpload = (Button) findViewById(R.id.btnUploadFoto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(iFormAsset.getStringExtra("action").equals("update")){
            contentcontrol();
        }

        btnImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void contentcontrol() {
        aMerk = (TextView) findViewById(R.id.as_merk);
        aType = (TextView) findViewById(R.id.as_type);
        aPlat = (TextView) findViewById(R.id.as_plat);
        aYear = (TextView) findViewById(R.id.as_year);
        aColor = (TextView) findViewById(R.id.as_colour);

        //set value
        aMerk.setText(iFormAsset.getStringExtra("merk"));
        aType.setText(iFormAsset.getStringExtra("type"));
        aPlat.setText(iFormAsset.getStringExtra("plat"));
        aYear.setText(iFormAsset.getStringExtra("year"));
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

        if (id == R.id.action_save) {
            tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
            category = iFormAsset.getStringExtra("cat");
            idAsset = iFormAsset.getIntExtra("id_asset",0);
            if(iFormAsset.getStringExtra("action").equals("update")){
                updateDataAset(category);
            }else{
                addDataAset(tenant);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDataAset(String tenant) {
        aTransmisionGroup = (RadioGroup) findViewById(R.id.transmission_group);
        int transmissionId = aTransmisionGroup.getCheckedRadioButtonId();
        aTransmisionButton = (RadioButton) findViewById(transmissionId);
        aMerk = (TextView) findViewById(R.id.as_merk);
        aType = (TextView) findViewById(R.id.as_type);
        aPlat = (TextView) findViewById(R.id.as_plat);
        aYear = (TextView) findViewById(R.id.as_year);
        aColor = (TextView) findViewById(R.id.as_colour);
        aRegNum = (TextView) findViewById(R.id.as_regnum);
        aEngCap = (TextView) findViewById(R.id.as_engcap);
        aPlat = (TextView) findViewById(R.id.as_plat);
        aFuel = (TextView) findViewById(R.id.as_fuel);
        aSeat = (TextView) findViewById(R.id.as_seat);
        aAsuracePrice = (TextView) findViewById(R.id.as_assurance_price);
        aLowPrice = (TextView) findViewById(R.id.as_lprice);
        aNormalPrice = (TextView) findViewById(R.id.as_nprice);
        aHighPrice = (TextView) findViewById(R.id.as_hprice);
        aAc = (CheckBox) findViewById(R.id.as_ck_ac);
        aAb = (CheckBox) findViewById(R.id.as_ck_ab);
        aDriver = (CheckBox) findViewById(R.id.as_ck_driver);
        aAssurace = (CheckBox) findViewById(R.id.as_ck_assurance);
        aImg = (ImageView) findViewById(R.id.thumb_aset);
        aAddress = "BALI,INDONESIA";
        aLatitude = "0";
        aLongitude = "0";
        aRentPackage = "-";

        pDialog.setMessage("loading ...");
        showProgress(true);
        new addAsetTask(tenant).execute();
    }

    private class addAsetTask extends AsyncTask<String, String, String> {

        private final String mTenant;
        private String errorMsg, responseAsset;

        private addAsetTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_MOBIL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseAsset = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Asset Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("brand", aMerk.getText().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("year", aYear.getText().toString());
                    keys.put("no_bpkb", aRegNum.getText().toString());
                    keys.put("colour", aColor.getText().toString());
                    keys.put("engine_capacity", aEngCap.getText().toString());
                    keys.put("license_plat", aPlat.getText().toString());
                    keys.put("seat", aSeat.getText().toString());
                    keys.put("air_bag", String.valueOf(aAb.isChecked()));
                    keys.put("air_conditioner", String.valueOf(aAc.isChecked()));
                    keys.put("transmission", aTransmisionButton.getText().toString());
                    keys.put("fuel", aFuel.getText().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("insurance_price", aAsuracePrice.getText().toString());
                    keys.put("price_low", aLowPrice.getText().toString());
                    keys.put("price_normal", aNormalPrice.getText().toString());
                    keys.put("price_high", aHighPrice.getText().toString());
                    keys.put("driver_included", String.valueOf(aDriver.isChecked()));
                    keys.put("address", aAddress);
                    keys.put("rent_package", aRentPackage);
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
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

            return responseAsset;
        }

        @Override
        protected void onPostExecute(String aset) {
            mAddAssetTask = null;
            showProgress(false);

            if(aset != null){
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAddAssetTask = null;
            showProgress(false);
        }
    }

    private void updateDataAset(String category) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new updateAsetTask(category).execute();
    }

    private class updateAsetTask extends AsyncTask<String, String, String> {
        private final String mCategory;
        private String errorMsg, responseAsset;

        private updateAsetTask(String category) {
            mCategory = category;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_UPDATE_ASSET + mCategory;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseAsset = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Asset Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_item", String.valueOf(idAsset));
                    keys.put("brand", aMerk.getText().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("year", aYear.getText().toString());
                    keys.put("colour", aColor.getText().toString());
                    Log.e(TAG, "Asset Keys: " + String.valueOf(keys));
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

            return responseAsset;
        }

        @Override
        protected void onPostExecute(String aset) {
            mAddAssetTask = null;
            showProgress(false);

            if(aset != null){
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAddAssetTask = null;
            showProgress(false);
        }
    }
}

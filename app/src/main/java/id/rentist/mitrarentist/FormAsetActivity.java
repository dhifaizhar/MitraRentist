package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class FormAsetActivity extends AppCompatActivity {
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private AsyncTask mAddAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    ImageView aImg;
    TextView aMerk, aType, aPlat, aYear, aColor, aRegNum, aEngCap, aFuel, aSeat, aAsuracePrice;
    CheckBox aAc, aAb, aDriver, aAssurace;
    RadioGroup aTransmisionGroup;
    RadioButton aTransmisionButton;
    Button btnImgUpload;

    private static final String TAG = "FormAssetActivity";
    private static final String TOKEN = "secretissecret";
    String errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_aset);
        setTitle("Aset Form");

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
                addDataAset(tenant);
            }
        });
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
        aAc = (CheckBox) findViewById(R.id.as_ck_ac);
        aAb = (CheckBox) findViewById(R.id.as_ck_ab);
        aDriver = (CheckBox) findViewById(R.id.as_ck_driver);
        aAssurace = (CheckBox) findViewById(R.id.as_ck_assurance);
        aImg = (ImageView) findViewById(R.id.thumb_aset);
        btnImgUpload = (Button) findViewById(R.id.btnUploadFoto);
        pDialog.setMessage("loading ...");
        showProgress(true);
        new postAsetTask(tenant).execute();
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

    private class postAsetTask extends AsyncTask<String, String, String> {

        private final String mTenant;
        private String errorMsg, responseAsset;

        private postAsetTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_MOBIL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseAsset = response;
                    Toast.makeText(getApplicationContext(), "Add Asset : " + response,
                            Toast.LENGTH_LONG).show();
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
                    // Posting parameters to login url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("id_asset_category", "1");
                    keys.put("merk", aMerk.getText().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("year", aYear.getText().toString());
                    keys.put("no_bpkb", aRegNum.getText().toString());
                    keys.put("colour", aColor.getText().toString());
                    keys.put("engine_capacity", aEngCap.getText().toString());
                    keys.put("lisence_plat", aPlat.getText().toString());
                    keys.put("seat", aSeat.getText().toString());
                    keys.put("air_bag", String.valueOf(aAb.isEnabled()));
                    keys.put("air_conditioner", String.valueOf(aAc.isEnabled()));
                    keys.put("transmission", aTransmisionButton.getText().toString());
                    keys.put("fuel", aFuel.getText().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isEnabled()));
                    keys.put("insurance_price", aAsuracePrice.getText().toString());
                    keys.put("driver_included", String.valueOf(aDriver.isEnabled()));
                    keys.put("status", "inactive");
                    Log.e(TAG, "Form Asset Fetch Data : \n"
                            + String.valueOf(keys)
                    );
                    return keys;
                }

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

            return responseAsset;
        }

        @Override
        protected void onPostExecute(String aset) {
            mAddAssetTask = null;
            showProgress(false);

            if(aset != null){
                Intent iAset = new Intent(FormAsetActivity.this, AsetActivity.class);
                startActivity(iAset);
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

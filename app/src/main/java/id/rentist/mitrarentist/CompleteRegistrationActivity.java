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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;

public class CompleteRegistrationActivity extends AppCompatActivity {
    AsyncTask mCRegisterTask = null;
    ProgressDialog pDialog;
    JSONObject tenantObject, responseMessage;
    private Intent formTenant;

    String tenant;
    TextView rBank, rBankName, rBankRek;
    Button btnSave;

    private static final String TAG = "CompRegistActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);
        setTitle("");

        formTenant = getIntent();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        rBank = (TextView)findViewById(R.id.reg_bank);
        rBankName = (TextView)findViewById(R.id.reg_bank_name);
        rBankRek = (TextView)findViewById(R.id.reg_bank_rek);
        btnSave = (Button) findViewById(R.id.btn_save);

        // set content control value
        tenant = formTenant.getStringExtra("id_tenant");

        // set action
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formCompRegisterTenant(tenant);
            }
        });
    }

    private void formCompRegisterTenant(String tenant) {
        pDialog.setMessage("registering account ...");
        showProgress(true);
        new compRegisterTask(tenant).execute();
    }

    private class compRegisterTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responseComp;

        private compRegisterTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_UPDATE_TENANT + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseComp = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant User Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("bank_account", rBank.getText().toString());
                    keys.put("bank_name", rBankName.getText().toString());
                    keys.put("account_name", rBankRek.getText().toString());
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

            return responseComp;
        }

        @Override
        protected void onPostExecute(String user) {
            mCRegisterTask = null;
            showProgress(false);

            if(user != null){
                Toast.makeText(getApplicationContext(),"Sukses melengkapi informasi akun.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CompleteRegistrationActivity.this, LoginActivity.class));
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal menambah indormasi akun.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mCRegisterTask = null;
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

}

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
import android.widget.AutoCompleteTextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;

public class RegistrationActivity extends AppCompatActivity {
    public TextView forgotPassword;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mRegistFormView;
    AsyncTask mRegisterTask = null;
    ProgressDialog pDialog;
    JSONObject registOnject, tenantObject, responseMessage;

    TextView rName, rOwner, rEmail, rPhone, rAddress, rBank, rBankName, rBankRek, rPass, rConfPass;
    Spinner rRole;
    Button btnSave;

    private static final String TAG = "RegistActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Pendaftaran Mitra");

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
        rRole = (Spinner) findViewById(R.id.reg_role);
        rName = (TextView)findViewById(R.id.reg_rent_name);
        rOwner = (TextView)findViewById(R.id.reg_own_name);
        rPhone = (TextView)findViewById(R.id.reg_phone);
        rAddress = (TextView)findViewById(R.id.reg_branch);
        rEmail = (TextView)findViewById(R.id.reg_email);
        rBank = (TextView)findViewById(R.id.reg_bank);
        rBankName = (TextView)findViewById(R.id.reg_bank_name);
        rBankRek = (TextView)findViewById(R.id.reg_bank_rek);
        rPass = (TextView)findViewById(R.id.reg_pass);
        rConfPass = (TextView)findViewById(R.id.reg_conf_pass);
        btnSave = (Button) findViewById(R.id.btn_save);

        // set content control value

        // set action
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rPass.getText().toString().equals(rConfPass.getText().toString())){
                    formRegisterTenant();
                }else{
                    Toast.makeText(getApplicationContext(), "Konfirmasi Password tidak sesuai.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void formRegisterTenant() {
        pDialog.setMessage("registering account ...");
        showProgress(true);
        new postRegisterTask().execute();
    }

    private class postRegisterTask extends AsyncTask<String, String, String>{
        private String errorMsg, responseRegist;

        private postRegisterTask() {}

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_REGISTER;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseRegist = response;
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
                    keys.put("rental_name", rName.getText().toString());
                    keys.put("owner_name", rOwner.getText().toString());
                    keys.put("rental_type", rRole.getSelectedItem().toString());
                    keys.put("branch", rAddress.getText().toString());
                    keys.put("email", rEmail.getText().toString());
                    keys.put("password", rPass.getText().toString());
                    keys.put("phone", rPhone.getText().toString());
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

            return responseRegist;
        }

        @Override
        protected void onPostExecute(String user) {
            mRegisterTask = null;
            showProgress(false);
            String aId;

            if(user != null){
                try {
                    registOnject = new JSONObject(user);
                    Log.d(TAG, String.valueOf(registOnject));
                    tenantObject = new JSONObject(registOnject.getString("data"));
                    Log.d(TAG, String.valueOf(tenantObject));

                    aId = tenantObject.getString("id");

                    Toast.makeText(getApplicationContext(),"Sukses mendaftarkan akun.", Toast.LENGTH_LONG).show();
                    Intent iComp = new Intent(RegistrationActivity.this, SelectCategoryActivity.class);
                    iComp.putExtra("id_tenant",aId);
                    startActivity(iComp);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Gagal mendaftarkan akun.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
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

}

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
import android.widget.CheckBox;
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
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.FormValidation;

public class RegistrationActivity extends AppCompatActivity {
    AsyncTask mRegisterTask = null;
    ProgressDialog pDialog;
    JSONObject registObject, tenantObject, responseMessage;
    FormValidation formValidation;
    View focusView;

    String tenant, email, phone, password, cpassword;
    TextView rName, rOwner, rEmail, rPhone, rPass, rConfPass;
    CountryCodePicker countryCode;
    Spinner rRole;
    CheckBox checkBoxAgreement;
    Button btnSave;

    private static final String TAG = "RegistActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Pendaftaran Mitra");

        formValidation = new FormValidation(RegistrationActivity.this);
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
        rEmail = (TextView)findViewById(R.id.reg_email);
        rPass = (TextView)findViewById(R.id.reg_pass);
        rConfPass = (TextView)findViewById(R.id.reg_conf_pass);
        checkBoxAgreement = (CheckBox) findViewById(R.id.checkBoxAgreement);
        btnSave = (Button) findViewById(R.id.btn_save);
        countryCode =(CountryCodePicker) findViewById(R.id.country_code);

        // set content control value

        // set action
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formRegisterTenant();
            }
        });
    }

    private void formRegisterTenant() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        rEmail.setError(null);
        rPhone.setError(null);
        rPass.setError(null);
        rConfPass.setError(null);

        email = rEmail.getText().toString();
        phone = countryCode.getSelectedCountryCode() +rPhone.getText().toString();
        password = rPass.getText().toString();
        cpassword = rConfPass.getText().toString();

        if(formValidation.isEmailValid(email)){
            if(formValidation.isPhoneValid(phone)){
                if(formValidation.isPasswordValid(password)){
                    if(formValidation.isConfirmPasswordValid(password,cpassword)){
                        if(checkBoxAgreement.isChecked()){
                            pDialog.setMessage("registering account ...");
                            showProgress(true);
                            mRegisterTask = new postRegisterTask().execute();
                        }else{
                            Toast.makeText(getApplicationContext(),"Mohon konfirmasi bahwa anda telah membaca Syarat dan Kebijakan Rentist", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        rConfPass.setError(getString(R.string.error_invalid_confirm_password));
                        focusView = rConfPass;
                        focusView.requestFocus();
                    }
                }else{
                    rPass.setError(getString(R.string.error_invalid_password));
                    focusView = rPass;
                    focusView.requestFocus();
                }
            }else{
                rPhone.setError(getString(R.string.error_invalid_phone));
                focusView = rPhone;
                focusView.requestFocus();
            }
        }else{
            rEmail.setError(getString(R.string.error_invalid_email));
            focusView = rEmail;
            focusView.requestFocus();
        }
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
                    keys.put("email", rEmail.getText().toString());
                    keys.put("password", rPass.getText().toString());
                    keys.put("phone", rPhone.getText().toString());
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
                    registObject = new JSONObject(user);
                    Log.d(TAG, String.valueOf(registObject));
                    try{
                        tenantObject = new JSONObject(registObject.getString("data"));
                        Log.d(TAG, String.valueOf(tenantObject));

                        aId = tenantObject.getString("id");

                        Toast.makeText(getApplicationContext(),"Sukses mendaftarkan akun.", Toast.LENGTH_LONG).show();
                        Intent iComp = new Intent(RegistrationActivity.this, AktivasiActivity.class);
                        iComp.putExtra("action","registration");
                        iComp.putExtra("email_rental",tenantObject.getString("email"));
                        iComp.putExtra("telepon",tenantObject.getString("phone"));
                        iComp.putExtra("id_tenant",aId);
                        startActivity(iComp);
                        finish();
                    }catch (JSONException e){
                        e.printStackTrace();
                        Log.d(TAG, "JSON Error : " + e);

                        tenantObject = new JSONObject(user);
                        Log.d(TAG, String.valueOf(tenantObject));

                        rEmail.setError(tenantObject.getString("info"));
                        focusView = rEmail;
                        focusView.requestFocus();
                    }
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

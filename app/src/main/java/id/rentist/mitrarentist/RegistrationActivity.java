package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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

public class  RegistrationActivity extends AppCompatActivity {
    //private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;

    AsyncTask mRegisterTask = null;
    ProgressDialog pDialog;
    JSONObject registObject, tenantObject, tenantAccObject, responseMessage;
    FormValidation formValidation;
    View focusView;

    String tenant, name, owner, email, phone, password, cpassword;
    TextView rName, rOwner, rEmail, rPhone, rPass, rConfPass, termsService;
    CountryCodePicker countryCode;
    Spinner rRole;
    CheckBox checkBoxAgreement;
    Button btnSave;
    ImageView emailCheckIc, phoneCheckIc;

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

        //Firebase.setAndroidContext(this);

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
        termsService = (TextView)findViewById(R.id.terms_service);
        emailCheckIc = (ImageView)findViewById(R.id.email_check);
        phoneCheckIc = (ImageView)findViewById(R.id.phone_check);

        // set content control value
        rEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    verifyEmail();
                }
                return true;
            }
        });

        rEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verifyEmail();
                }
            }
        });

        rPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    verifyPhone();
                }
                return true;
            }
        });

        rPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verifyPhone();
                }
            }
        });

        // set action
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formRegisterTenant();
            }
        });

        termsService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTerms = new Intent(RegistrationActivity.this, TermsPolicyActivity.class);
                startActivity(iTerms);
            }
        });

//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.d(TAG, "onAuthStateChanged : signed_in: " + user.getUid());
//                } else {
//                    Log.d(TAG, "onAuthStateChanged : signed_out ");
//                }
//            }
//        };
    }

    private void formRegisterTenant() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        rName.setError(null);
        rOwner.setError(null);
        rEmail.setError(null);
        rPhone.setError(null);
        rPass.setError(null);
        rConfPass.setError(null);

        name = rName.getText().toString();
        owner = rOwner.getText().toString();
        email = rEmail.getText().toString();
        phone = countryCode.getSelectedCountryCode() + rPhone.getText().toString();
        password = rPass.getText().toString();
        cpassword = rConfPass.getText().toString();

        if(!TextUtils.isEmpty(name)){
            if(!TextUtils.isEmpty(owner)){
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
            }else{
                rOwner.setError(getString(R.string.error_field_required));
                focusView = rOwner;
                focusView.requestFocus();
            }
        }else{
            rName.setError(getString(R.string.error_field_required));
            focusView = rName;
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
                    keys.put("rental_name", name);
                    keys.put("owner_name", owner);
                    keys.put("rental_typ" +
                            "e", rRole.getSelectedItem().toString());
                    keys.put("email", email);
                    keys.put("password", password);
                    keys.put("phone", phone);
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

            Log.d(TAG + "-user: ", user);
            if(user != null){
                try {
                    registObject = new JSONObject(user);
                    Log.d(TAG, String.valueOf(registObject));
                    try{
                        tenantObject = new JSONObject(registObject.getString("data"));
                        tenantAccObject = new JSONObject(registObject.getString("tenant_acc"));
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

                        if(tenantObject.has("info")){
                            rEmail.setError(tenantObject.getString("info"));
                            focusView = rEmail;
                            focusView.requestFocus();
                        }
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

//    private void registerFireBase(final String akun) {
//        String url = "https://rentistid-174904.firebaseio.com/mitra-rentist.json";
//        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
//            @Override
//            public void onResponse(String s) {
//                Log.d(TAG, "Firebase Object : " + s);
//                Firebase reference = new Firebase("https://rentistid-174904.firebaseio.com/mitra-rentist");
//
//                try {
//                    JSONObject akunObject = new JSONObject(akun);
//                    Log.d(TAG, "Firebase Object : " + akun);
//
//                    if(s.equals("null")) {
//                        reference.child(akunObject.getString("phone")).child("email").setValue(akunObject.getString("email"));
//                        reference.child(akunObject.getString("phone")).child("id_tenant").setValue(akunObject.getString("id_tenant"));
//                        reference.child(akunObject.getString("phone")).child("nama_user").setValue(akunObject.getString("name"));
//                        Log.d(TAG, "Firebase Regist : registration successful");
//                    }
//                    else {
//                        try {
//                            JSONObject obj = new JSONObject(s);
//
//                            if (!obj.has(phone)) {
//                                reference.child(akunObject.getString("phone")).child("email").setValue(akunObject.getString("email"));
//                                reference.child(akunObject.getString("phone")).child("id_tenant").setValue(akunObject.getString("id_tenant"));
//                                reference.child(akunObject.getString("phone")).child("nama_user").setValue(akunObject.getString("name"));
//                                Log.d(TAG, "Firebase Regist : registration successful");
//                            } else {
//                                Log.d(TAG, "Firebase Regist : username already exists");
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d(TAG, "JSON Firebase Error : " + e);
//                }
//            }
//
//        },new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                System.out.println("FB " + volleyError );
//            }
//        });
//
//        RequestQueue rQueue = Volley.newRequestQueue(RegistrationActivity.this);
//        rQueue.add(request);
//    }

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
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }

    private void verifyEmail() {
        String URL = AppConfig.URL_VERIFY_EMAIL;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseEmail) {

                if(responseEmail != null){
                    Log.e(TAG, "Email Verify : OK");
                    try {
                        JSONObject registEmail = new JSONObject(responseEmail);
                        try{
                            String status = registEmail.getString("status");
                            if(status.equals("ok")){
                                if(formValidation.isEmailValid(rEmail.getText().toString())) {
                                    emailCheckIc.setVisibility(View.VISIBLE);
                                } else{
                                    emailCheckIc.setVisibility(View.GONE);
                                    rEmail.setError(getString(R.string.error_invalid_email));
                                }
                            }else{
                                emailCheckIc.setVisibility(View.GONE);
                                rEmail.setError("Email sudah terdaftar");
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error : " + e);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "JSON Error : " + e);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Email sudah ada.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Check Email Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("email", rEmail.getText().toString());
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

        requestQueue.add(stringRequest);
    }

    private void verifyPhone() {
        String URL = AppConfig.URL_VERIFY_PHONE;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String responsePhone) {

                if(responsePhone != null){
                    Log.e(TAG, "Phone Verify : OK");
                    try {
                        JSONObject registPhone = new JSONObject(responsePhone);
                        try{
                            String status = registPhone.getString("status");
                            if(status.equals("ok")){
                                if(formValidation.isPhoneValid(countryCode.getSelectedCountryCode() + rPhone.getText().toString())) {
                                    phoneCheckIc.setVisibility(View.VISIBLE);
                                }else{
                                    phoneCheckIc.setVisibility(View.GONE);
                                    rPhone.setError(getString(R.string.error_invalid_phone));
                                }
                            }else{
                                phoneCheckIc.setVisibility(View.GONE);
                                rPhone.setError("Nomor Telepon sudah terdaftar");
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error : " + e);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "JSON Error : " + e);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Nomor Telepon sudah ada.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Check Phone Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
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

        requestQueue.add(stringRequest);

    }
    }


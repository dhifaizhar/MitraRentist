package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AktivasiActivity extends AppCompatActivity {
    AsyncTask mActiveTask = null, mResendCodeTask = null;
    private View mLoginFormView;
    private SessionManager sm;
    private ProgressDialog pDialog;
    private JSONObject userObject, tenantObject, responseMessage;
    Intent actIntent;

    TextView refresh, countdown, counterZero;
    String userMail, phone, activation;
    EditText mCode;
    Button aktif_btn;
    Boolean status = false;

    private static final String TAG = "ActivationActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktifasi);
        setTitle("");

        actIntent = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // action retrieve data aset
        aktif_btn = (Button) findViewById(R.id.aktif_button);
        refresh = (TextView) findViewById(R.id.getNewCode);
        countdown = (TextView) findViewById(R.id.resendCountdown);
        counterZero = (TextView) findViewById(R.id.counterZero);

        if(actIntent.hasExtra("action")){
            userMail = actIntent.getStringExtra("email_rental");
            phone = actIntent.getStringExtra("telepon");
            countDown();
        }else{
            userMail = sm.getPreferences("email_rental");
            phone = sm.getPreferences("telepon");
            countdown.setVisibility(View.GONE);
            status = true;
        }
        mCode = (EditText)findViewById(R.id.aktif_code);


        aktif_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                activationUser(userMail);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals(true)){
                    refreshCodeActivation(phone);
                    countDown();
                }
            }
        });
    }

    private void countDown(){
        status = false;
        countdown.setVisibility(View.VISIBLE);
        refresh.setClickable(false);
        refresh.setTextColor(Color.parseColor("#CCCCCC"));
        new CountDownTimer(61500, 1000){
            int counter = 60;
            public void onTick(long millisUntilFinished){
                countdown.setText("( " + String.valueOf(counter) + " )");
                counter--;
            }
            public  void onFinish(){
                refresh.setClickable(true);
                refresh.setTextColor(Color.parseColor("#FFFFFF"));
                countdown.setVisibility(View.GONE);
                status = true;
            }
        }.start();
    }

    private void refreshCodeActivation(String phone) {
        pDialog.setMessage("request new code ...");
        showProgress(true);
        new requstCodeTask(phone).execute();
    }

    private class requstCodeTask extends AsyncTask<String, String, String> {
        private final String mPhone;
        String errorMsg, responseResendCode;

        requstCodeTask(String phone) {
            mPhone = phone;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_RESEND_CODE;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.equals("resend is limited")){
                        showProgress(false);
                        countdown.setVisibility(View.GONE);
                        refresh.setVisibility(View.GONE);
                        counterZero.setVisibility(View.VISIBLE);
                        counterZero.setText("Anda sudah tidak dapat melakukan kirim ulang kode verifikasi, periksa email anda jika tetap tidak mendapatkan SMS verifikasi");
                    }else{
                        Toast.makeText(getApplicationContext(),"Sukses meminta kode baru.", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Resend Code User Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("phone", mPhone);
                    Log.e(TAG, "Resend Code User Fetch Error : " + String.valueOf(keys));
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

            return responseResendCode;
        }

//        @Override
//        protected void onPostExecute(String code) {
//            mResendCodeTask = null;
//            showProgress(false);
//
//            if(code != null){
//                Toast.makeText(getApplicationContext(),"Sukses meminta kode baru.", Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(getApplicationContext(),"Gagal meminta kode baru", Toast.LENGTH_LONG).show();
//            }
//        }

        @Override
        protected void onCancelled() {
            mResendCodeTask = null;
            showProgress(false);
        }

    }

    private void activationUser(String email) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new activationUserTask(email).execute();
    }

    private class activationUserTask extends AsyncTask<String, String, String> {
        private final String mEmail;
        String errorMsg, responseActivation;

        activationUserTask(String email) {
            mEmail = email;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_ACTIVATION;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseActivation = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Activated User Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("token", mCode.getText().toString());
                    keys.put("email", mEmail);
                    Log.e(TAG, "Activated User Fetch Error : " + String.valueOf(keys));
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

            return responseActivation;
        }

        @Override
        protected void onPostExecute(String activation) {
            mActiveTask = null;
            showProgress(false);
            String mStat;
            Integer dataLength;

            if(activation != null){
                try {
                    JSONArray jsonArray = new JSONArray(activation);
                    Log.d(TAG, String.valueOf(jsonArray));
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        JSONObject jsonobject = jsonArray.getJSONObject(0);
                        mStat = jsonobject.getString("is_activated");
                        sm.setPreferences("status",mStat);

                        Toast.makeText(getApplicationContext(),"Sukses mengaktifkan akun.", Toast.LENGTH_LONG).show();
                        if(actIntent.hasExtra("action")){
                            startActivity(new Intent(AktivasiActivity.this, LoginActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(AktivasiActivity.this, DashboardActivity.class));
                            finish();
                        }
                    }else{
                        errorMsg = "Gagal aktifasi akun";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Gagal aktifasi akun", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mActiveTask = null;
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

    public boolean onSupportNavigateUp() {
        logout();
        onBackPressed();
        return true;
    }

    public void logout(){
        final Intent iLogin = new Intent(this, LoginActivity.class);
//        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
//        showAlert.setMessage("Akhiri sesi pengguna aplikasi ?");
//        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
                sm.clearPreferences();
                startActivity(iLogin);
                finish();
//            }
//        });
//        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // close dialog
//            }
//        });
//
//        AlertDialog alertDialog = showAlert.create();
//        alertDialog.show();
    }
}

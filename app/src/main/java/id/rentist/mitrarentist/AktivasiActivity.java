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
import android.widget.EditText;
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
    private AsyncTask mActiveTask = null;
    private View mLoginFormView;
    private SessionManager sm;
    private ProgressDialog pDialog;
    private JSONObject userObject, tenantObject, responseMessage;
    Intent actIntent;

    String userMail, activation;
    EditText mCode;
    Button aktif_btn;

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
        if(actIntent.getStringExtra("action").equals("registration")){
            userMail = actIntent.getStringExtra("email_rental");
        }else{
            userMail = sm.getPreferences("email_rental");
        }
        mCode = (EditText)findViewById(R.id.aktif_code);

        aktif_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                activationUser(userMail);
            }
        });
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
                        if(actIntent.getStringExtra("action").equals("registration")){
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
        onBackPressed();
        return true;
    }
}

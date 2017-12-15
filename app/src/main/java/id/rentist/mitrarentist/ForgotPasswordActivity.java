package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.FormValidation;
import id.rentist.mitrarentist.tools.SessionManager;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    AsyncTask mForgotTask = null;
    SessionManager sm;
    FormValidation formValidation;
    EditText vEmail;

    private static final String TAG = "ForgotPasswordActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("");

        formValidation = new FormValidation(ForgotPasswordActivity.this);
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_forgot);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        vEmail = (EditText) findViewById(R.id.email_forgot);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button forgot_button = (Button) findViewById(R.id.forgot_button);
        forgot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReset();
            }
        });
    }

    private void attemptReset() {
        vEmail.setError(null);
        String email = vEmail.getText().toString();
        View focusView;

        if (formValidation.isEmailValid(email)) {
            pDialog.setMessage("sending email ...");
            showProgress(true);
            Log.e(TAG, "Email Reset: " + email);
            new ForgotPasswordActivity.postReset(email).execute();
        } else {
            focusView = vEmail;
            focusView.requestFocus();
            Log.e(TAG, "Email Reset: " + email);
            Toast.makeText(getApplicationContext(),"Masukan email yang valid", Toast.LENGTH_LONG).show();
        }
    }

    private class postReset extends AsyncTask<String, String, String> {
        private final String mEmail;
        private String errorMsg, responseReset;

        private postReset(String email) {
            mEmail = email;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_RESET_PASSWORD, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseReset = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Reset Password Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Please, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("email", mEmail);
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

            return responseReset;
        }

        @Override
        protected void onPostExecute(String reset) {
            mForgotTask = null;
            showProgress(false);

            if(reset != null){
                Toast.makeText(getApplicationContext(),"Email sukses dikirim", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal mengirim email, masukan email valid", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mForgotTask = null;
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

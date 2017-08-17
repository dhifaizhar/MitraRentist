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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormAddKebijakanActivity extends AppCompatActivity {
    private AsyncTask mAddPolicyTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
//    RecyclerView.Adapter mAdapter;
//    RecyclerView.LayoutManager mLayoutManager;

    TextView kTitle, kDesc;
    Button btnAddPolicy;

    private static final String TAG = "FormPolicyActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_kebijakan);
        setTitle("Tambah Kebijakan");

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnAddPolicy = (Button) findViewById(R.id.btn_add);
        btnAddPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
                addYourPolicy(tenant);
            }
        });
    }

    private void addYourPolicy(String tenant) {
        kTitle = (TextView) findViewById(R.id.kbj_title);
        kDesc = (TextView) findViewById(R.id.kbj_desc);

        pDialog.setMessage("loading ...");
        showProgress(true);
        new postPolicyTask(tenant).execute();
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
        return true;
    }

    private class postPolicyTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responsePolicy;

        private postPolicyTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_POLICY, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responsePolicy = response;
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
                    keys.put("title", kTitle.getText().toString());
                    keys.put("description", kDesc.getText().toString());
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

            return responsePolicy;
        }

        @Override
        protected void onPostExecute(String policy) {
            mAddPolicyTask = null;
            showProgress(false);

            if(policy != null){
                Intent iPolicy = new Intent(FormAddKebijakanActivity.this, KebijakanActivity.class);
                startActivity(iPolicy);
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAddPolicyTask = null;
            showProgress(false);
        }
    }
}

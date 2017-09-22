package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class FormKebijakanActivity extends AppCompatActivity {
    private AsyncTask mPolicyTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent formKebijakan;

    Integer id;
    String tenant, kId;
    TextView kTitle, kDesc;
    Button btnSavePolicy;

    private static final String TAG = "FormPolicyActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_kebijakan);
        setTitle("Form Kebijakan");

        formKebijakan = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        controlContent();


    }

    private void controlContent() {
        kTitle = (TextView) findViewById(R.id.kbj_title);
        kDesc = (TextView) findViewById(R.id.kbj_desc);
        btnSavePolicy = (Button) findViewById(R.id.btn_add);

        if(formKebijakan.getStringExtra("action").equals("update")){
            id = formKebijakan.getIntExtra("id", 0);
            kId = id.toString();
            kTitle.setText(formKebijakan.getStringExtra("title"));
            kDesc.setText(formKebijakan.getStringExtra("desc"));
        }

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));


        btnSavePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveKebijakan(tenant, kId);
            }
        });
    }

    private void saveKebijakan(String tenant, String id) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        if(formKebijakan.getStringExtra("action").equals("add")){
            new FormKebijakanActivity.postPolicyTask(tenant).execute();
        }else if(formKebijakan.getStringExtra("action").equals("update")){
            new FormKebijakanActivity.putUpdateKebijakanTask(tenant, id).execute();
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
            mPolicyTask = null;
            showProgress(false);

            if(policy != null){
//                Intent iPolicy = new Intent(FormKebijakanActivity.this, KebijakanActivity.class);
//                startActivity(iPolicy);
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mPolicyTask = null;
            showProgress(false);
        }
    }

    public class putUpdateKebijakanTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idKebijakan;
        private String errorMsg, responseKebijakan;

        public putUpdateKebijakanTask(String tenant, String id) {
            mTenant = tenant;
            idKebijakan = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_UPDATE_POLICY + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseKebijakan = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Voucher Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("id_kebijakan", idKebijakan);
                    keys.put("title", kTitle.getText().toString());
                    keys.put("description", kDesc.getText().toString());
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

            return responseKebijakan;
        }

        @Override
        protected void onPostExecute(String user) {
            mPolicyTask = null;
            showProgress(false);

            if(user != null){
                Toast.makeText(getApplicationContext(),"Sukses mengubah data.", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mPolicyTask = null;
            showProgress(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(formKebijakan.getStringExtra("action").equals("update")) {
            getMenuInflater().inflate(R.menu.menu_delete_option, menu);
        }

        return true;
    }

    private class putDeletePolicyTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private final String idKebijakan;
        private String errorMsg, responseKebijakan;

        public putDeletePolicyTask(String tenant, String id) {
            mTenant = tenant;
            idKebijakan = id;
        }
        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, AppConfig.URL_DELETE_POLICY + mTenant, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseKebijakan = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Voucher Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_kebijakan", idKebijakan);
                    Log.e(TAG, "Delete Data : " + String.valueOf(keys));
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

            return responseKebijakan;
        }

        @Override
        protected void onPostExecute(String voucher) {
            mPolicyTask = null;
            showProgress(false);

            if(voucher != null){
                Toast.makeText(getApplicationContext(),"Data berhasil dihapus", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal menghapus data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mPolicyTask = null;
            showProgress(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            deleteDataKebijakan(tenant, kId);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDataKebijakan(final String tenant, final String id) {
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Hapus kebijakan ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                pDialog.setMessage("loading ...");
                showProgress(true);
                new FormKebijakanActivity.putDeletePolicyTask(tenant, id).execute();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        AlertDialog alertDialog = showAlert.create();
        alertDialog.show();
    }


}

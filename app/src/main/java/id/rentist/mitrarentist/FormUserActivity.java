package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormUserActivity extends AppCompatActivity {
    private AsyncTask mDetailUserTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent formUser;

    String tenant, aId;
    TextView nama, email;
    Spinner role;
    ImageView profilPic;
    FloatingActionButton fab;

    private static final String TAG = "FormUserActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);
        setTitle("User Form");

        formUser = getIntent();
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
        //initialize view
        profilPic = (ImageView)findViewById(R.id.fus_thumb);
        nama = (TextView)findViewById(R.id.fus_name);
        role = (Spinner) findViewById(R.id.fus_role);
        email = (TextView)findViewById(R.id.fus_email);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // set content control value
        aId = formUser.getStringExtra("id_user");
        Log.e(TAG, "Id Tenant Form User : " + aId);
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        nama.setText(formUser.getStringExtra("name"));
        email.setText(formUser.getStringExtra("email"));
        if(formUser.getStringExtra("role").equals("SuperAdmin")){
            role.setSelection(0);
        }else if(formUser.getStringExtra("role").equals("Operational")){
            role.setSelection(1);
        }else if(formUser.getStringExtra("role").equals("Executive")){
            role.setSelection(2);
        }

        // set action
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formUserTenant(tenant, aId);
            }
        });
    }

    private void formUserTenant(String tenant, String id) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getFormUserTask(tenant, id).execute();
    }

    private class getFormUserTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idUser;
        private String errorMsg, responseUser;

        private getFormUserTask(String tenant, String id) {
            mTenant = tenant;
            idUser = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DETAIL_USER + idUser;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseUser = response;
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
                    keys.put("name", nama.getText().toString());
                    keys.put("role", role.getSelectedItem().toString());
                    keys.put("email", email.getText().toString());
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

            return responseUser;
        }

        @Override
        protected void onPostExecute(String user) {
            mDetailUserTask = null;
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
            mDetailUserTask = null;
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

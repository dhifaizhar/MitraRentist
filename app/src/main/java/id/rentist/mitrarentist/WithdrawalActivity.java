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
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class WithdrawalActivity extends AppCompatActivity {
    private AsyncTask mAddPolicyTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    private Intent iWithdrawal;

    ArrayList<String> transId = new ArrayList<String>();

    String balance;//, trans[];
    TextView credit, description;
    Button withdrawal;
    ImageButton reset;

    private static final String TAG = "WithdrawalActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        setTitle("Withdrawal");

        iWithdrawal = getIntent();
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
        credit = (TextView)findViewById(R.id.wd_credit);
        description = (TextView)findViewById(R.id.wd_desc);
        withdrawal = (Button)findViewById(R.id.btn_withdrawal);
//        reset = (ImageButton)findViewById(R.id.reset_button);

        credit.setEnabled(false);

        transId = iWithdrawal.getStringArrayListExtra("transId");

        Log.e(TAG, "Trans id array : " + transId);

        // set content control value
        balance = iWithdrawal.getStringExtra("balance");
        credit.setText(balance);
//        reset.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                credit.setText("0");
//            }
//        });
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
                sendWithdrawal(tenant);
            }
        });
    }

    private void sendWithdrawal(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new postWithdrawalTask(tenant).execute();
    }

    private class postWithdrawalTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responseWithdrawal, msg, trans, resMsg, s;


        private postWithdrawalTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_WITHDRAWAL, new Response.Listener<String>()
            {

                @Override
                public void onResponse(String response) {
                    responseWithdrawal = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Withdrawal Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("nominal", iWithdrawal.getStringExtra("balance"));
                    keys.put("description", description.getText().toString());
//
                    if (transId.size() > 0) {
                        trans = "";

                            for (int i = 0; i < transId.size(); i++) {
                            trans +=  transId.get(i);
                                if (i != transId.size()-1){
                                    trans += ",";
                                }

                        }
                        Log.e(TAG, "ID TRANS : " + trans);
                        keys.put("id_transaction_detail", trans);
                    }
                    Log.e(TAG, "Param : " + keys);

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

            return responseWithdrawal;
        }

        @Override
        protected void onPostExecute(String wd) {
            mAddPolicyTask = null;
            showProgress(false);

            if(wd != null){
//                Intent iWd = new Intent(WithdrawalActivity.this, DompetActivity.class);
//                startActivity(iWd);
                try {
                    JSONObject wdObject = new JSONObject(wd);
                    Log.e(TAG, "Response : " + wdObject);


                    resMsg = wdObject.getString("response");
                    if (resMsg.equals("Already Request")){
                        msg = "Anda sudah melakukan pengajuan";
                    } else {
                        msg = "Pengajuan berhasil dilakukan";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
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
}

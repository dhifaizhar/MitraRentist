package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.HistoryWithdrawalAdapter;
import id.rentist.mitrarentist.modul.WithdrawalModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class HistoryWithdrawalActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<WithdrawalModul> mWithdrawal = new ArrayList<>();
    private AsyncTask mWithdrawalTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    String tenant;

    private static final String TAG = "HistoryWDActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_withdrawal);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Riwayat Withdrawal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getHistoryWithdrawalDataList(tenant);
    }

    private void getHistoryWithdrawalDataList(String tenant) {
        pDialog.setMessage("loading...");
        showProgress(true);
        new HistoryWithdrawalActivity.getHistoryWithdrawalListTask(tenant).execute();
    }

    private class getHistoryWithdrawalListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseWD;

        private getHistoryWithdrawalListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String newURL = AppConfig.URL_HISTORY_WITHDRAWAL + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseWD = response;
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    // Posting parameters to login url
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
            Log.e(TAG, String.valueOf(mTenant + " | " + TOKEN));

            return responseWD;
        }

        @Override
        protected void onPostExecute(String withdrawal) {
            mWithdrawalTask = null;
            showProgress(false);
            String aNominal, aStatus, aDate, aDesc;
//            Integer dataLength, aId;

            if (withdrawal != null) {

                try {
                    JSONArray jsonArray = new JSONArray(withdrawal);
                    if( jsonArray.length() > 0){
                        Log.e(TAG, "Withdrawal : " + jsonArray.toString());

                        for (int i = 0; i <  jsonArray.length(); i++) {
                            errorMsg = "-";

                            JSONObject jsonobject = jsonArray.getJSONObject(i);
//                            aId = jsonobject.getInt("id");
                            aNominal = jsonobject.getString("nominal");
                            aDesc = jsonobject.getString("description");
                            aStatus = jsonobject.getString("status");
                            aDate = jsonobject.getString("createdAt").substring(0,10);
                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                            WithdrawalModul wd = new WithdrawalModul();
//                            dm.setId(aId.toString());
                            wd.setDate(aDate);
                            wd.setNominal(aNominal);
                            wd.setDesc(aDesc);
                            wd.setStatus(aStatus);
                            mWithdrawal.add(wd);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.hwd_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new HistoryWithdrawalAdapter(getApplicationContext(), mWithdrawal);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Anda belum memiliki Transaksi";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Anda belum memiliki Transaksi";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e(TAG, "Withdrawal : Null");

            };


        }

        @Override
        protected void onCancelled() {
            mWithdrawalTask = null;
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

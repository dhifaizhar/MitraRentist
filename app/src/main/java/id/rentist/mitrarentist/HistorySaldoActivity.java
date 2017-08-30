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

import id.rentist.mitrarentist.adapter.HistorySaldoAdapter;
import id.rentist.mitrarentist.modul.DompetModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class HistorySaldoActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<DompetModul> mSaldo = new ArrayList<>();
    private AsyncTask mSaldoTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    String tenant;

    private static final String TAG = "HistorySaldoActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_saldo);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Riwayat Saldo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getAssetCarDataList(tenant);

    }

    private void getAssetCarDataList(String tenant) {
        pDialog.setMessage("loading...");
        showProgress(true);
        new getSaldoListTask(tenant).execute();
    }

    private class getSaldoListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseSaldo;

        private getSaldoListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String newURL = AppConfig.URL_HISTORY_SALDO + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseSaldo = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Saldo Fetch Error : " + errorMsg);
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

            return responseSaldo;
        }

        @Override
        protected void onPostExecute(String saldo) {
            mSaldoTask = null;
            showProgress(false);
            String aName, aCredit, aStatus, aDate;
            Integer dataLength, aId;


            if (saldo != null) {
                Log.e(TAG, "Saldo Result : " + saldo);

                try {
                    JSONObject jsonObject = new JSONObject(saldo);
                    JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("Withdrawal")));
                    Log.e(TAG, "Saldo : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";

//                            JSONObject jsonobject = jsonArray.getJSONObject(i);
//                            aId = jsonobject.getInt("id");
//                            aName = jsonobject.getString("fullname");
//                            aCredit = jsonobject.getString("creadit");
//                            aStatus = jsonobject.getString("status");
//                            aDate = jsonobject.getString("cteatedDate");
//                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                            DompetModul dm = new DompetModul();
                            dm.setDate("07 Jul 2017 18:18:18");
                            dm.setCredit("50.000,00 IDR");
                            dm.setNama("Kredit");
                            dm.setStatus("done");
                            mSaldo.add(dm);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.hsaldo_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new HistorySaldoAdapter(getApplicationContext(), mSaldo);
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
            }
        }

        @Override
        protected void onCancelled() {
            mSaldoTask = null;
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

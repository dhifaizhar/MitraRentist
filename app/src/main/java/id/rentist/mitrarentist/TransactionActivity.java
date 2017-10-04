package id.rentist.mitrarentist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.TransaksiAdapter;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class TransactionActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<ItemTransaksiModul> mTrans = new ArrayList<>();
    private AsyncTask mTransactionTask = null;

    private SpinKitView pBar;
    private SessionManager sm;

    private static final String TAG = "TransactionActivity";
    private static final String TOKEN = "secretissecret";
    String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Permintaan Baru");

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getNewTransactionDataList(tenant);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getNewTransactionDataList(String tenant) {
        pBar.setVisibility(View.VISIBLE);
        new TransactionActivity.getNewTransactionDataListTask(tenant).execute();
    }

    public class getNewTransactionDataListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseTrans;

        public getNewTransactionDataListTask(String tenant) {
            this.mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String newURL = AppConfig.URL_TRANSACTION_NEW + mTenant;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseTrans = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Transaction Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    // Posting parameters to history url
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

            return responseTrans;
        }

        @Override
        protected void onPostExecute(String transaction) {
            mTransactionTask = null;
//            showProgress(false);
            pBar.setVisibility(View.GONE);

            String aIdTrans, aCodeTrans, aTitle, aMember, aStartDate, aEndDate, aNominal, aAsetName;

            if (transaction != null) {
                try {
                    JSONArray jsonArray = new JSONArray(transaction);

                    if(jsonArray.length() > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject transObject = jsonArray.getJSONObject(i);
                            Log.e(TAG, "Transaction Data : " + String.valueOf(transObject));

                            JSONObject idTrans = transObject.getJSONObject("id_transaction");
                            JSONArray items = transObject.getJSONArray("item");
                            JSONObject item;

                            aIdTrans = transObject.getString("id");
                            aAsetName = "- Item Kosong -";

                            if(items.length() > 0){
                                if (items.length() == 1){
                                    item = items.getJSONObject(0);
                                    aAsetName = item.getString("brand") + " " + item.getString("type") + " | " + item.getString("license_plat");

                                } else {

                                }
                            }

                            aCodeTrans = idTrans.getString("transaction_code");
                            aNominal = transObject.getString("nominal") + " IDR";
                            aMember = transObject.getString("firstname") + " " + transObject.getString("lastname");
                            aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                            aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);

                            ItemTransaksiModul itemTrans = new ItemTransaksiModul();
                            itemTrans.setCodeTrans(aCodeTrans);
                            itemTrans.setIdTrans(aIdTrans);
                            itemTrans.setAsetName(aAsetName);
                            itemTrans.setMember(aMember);
                            itemTrans.setPrice(aNominal);
                            itemTrans.setStartDate(aStartDate);
                            itemTrans.setEndDate(aEndDate);

                            mTrans.add(itemTrans);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.tr_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new TransaksiAdapter(getApplicationContext(), mTrans);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Tidak ada transaksi baru";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Transaksi Tidak Ditemukan";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mTransactionTask = null;
//            showProgress(false);
            pBar.setVisibility(View.GONE);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            getNewTransactionDataList(tenant);
        }

        return super.onOptionsItemSelected(item);
    }
}

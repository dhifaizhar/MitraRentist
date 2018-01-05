package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.VoucherAdapter;
import id.rentist.mitrarentist.modul.VoucherModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;

public class VoucherActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Intent iFormVou;
    private List<VoucherModul> mVoucher = new ArrayList<>();
    private AsyncTask mVoucherTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private SpinKitView pBar;

    private Intent iVoucherAdd;
    LinearLayout noData;

    private static final String TAG = "VoucherActivity";
    private static final String TOKEN = "secretissecret";
    private String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        sm = new SessionManager(getApplicationContext());
//        pBar = (SpinKitView)findViewById(R.id.progressBar);
//        FadingCircle fadingCircle = new FadingCircle();
//        pBar.setIndeterminateDrawable(fadingCircle);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Voucher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
//        pBar.setVisibility(View.VISIBLE);
        pDialog.setMessage("loading ...");
        showProgress(true);
        getVoucherDataList(tenant);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mVoucher.clear();
                getVoucherDataList(tenant);
            }
        });

        noData = (LinearLayout) findViewById(R.id.no_data);
        TextView toFormAdd = (TextView) findViewById(R.id.toFormAdd);
        toFormAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VoucherActivity.this, FormVoucherActivity.class);
                intent.putExtra("action","add");
                intent.putExtra("dateStat", "false");
                startActivity(intent);
            }
        });
    }

    private void getVoucherDataList(String tenant) {
//        pDialog.setMessage("loading ...");
//        showProgress(true);
//        new getVoucherListTask(tenant).execute();

        final String mTenant;
        String errorMsg;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String newURL = AppConfig.URL_LIST_VOUCHER + tenant;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mSwipeRefreshLayout.setRefreshing(false);
                showProgress(false);
//                pBar.setVisibility(View.GONE);
                int vId;
                String vName,vCode,startDate,endDate,vDesc,vType,vNominal,vPercen, vSdate;
                Integer dataLength;

                if (response != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        Log.e(TAG, "User : " + jsonArray);
                        dataLength = jsonArray.length();
                        mVoucher.clear();
                        if(dataLength > 0){
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                vId = jsonobject.getInt("id");
                                vName = jsonobject.getString("voucher_name");
                                vCode = jsonobject.getString("voucher_code");
                                vDesc = jsonobject.getString("description");
                                startDate = jsonobject.getString("start_date").substring(0,10);
                                endDate = jsonobject.getString("end_date").substring(0,10);
                                vType = jsonobject.getString("type");
                                vNominal = jsonobject.getString("nominal");
                                vPercen = jsonobject.getString("percentage");
                                Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                                VoucherModul vModul = new VoucherModul();
                                vModul.setId(vId);
                                vModul.setName(vName);
                                vModul.setCode(vCode);
                                vModul.setStartDate(startDate);
                                vModul.setEndDate(endDate);
                                vModul.setDesc(vDesc);
                                vModul.setAmount(jsonobject.getString("quantity"));
                                vModul.setNominal(vNominal);
                                vModul.setPercen(vPercen);
                                vModul.setType(vType);
                                vModul.setAsCategory(jsonobject.getString("id_asset_category"));

                                mVoucher.add(vModul);
                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.v_recyclerView);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mAdapter = new VoucherAdapter(getApplicationContext(), mVoucher);

                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                            noData.setVisibility(View.GONE);

                        }else{
                            noData.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Anda belum memiliki Voucher", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Voucher Tidak Ditemukan", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e(TAG, "Voucher Fetch Error : " + error.toString());
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

        requestQueue.add(stringRequest);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_option, menu);
        getMenuInflater().inflate(R.menu.menu_catalog, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            iFormVou = new Intent(VoucherActivity.this, FormVoucherActivity.class);
            iFormVou.putExtra("action","add");
            iFormVou.putExtra("dateStat", "false");
            startActivity(iFormVou);
        }

        if (id == R.id.action_catalog) {
            iFormVou = new Intent(VoucherActivity.this, VoucherCatalogActivity.class);
            startActivity(iFormVou);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestart(){
        super.onRestart();
//        pBar.setVisibility(View.VISIBLE);
        getVoucherDataList(tenant);
    }

    private class getVoucherListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseVoucher;

        private getVoucherListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String newURL = AppConfig.URL_LIST_VOUCHER + mTenant;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseVoucher = response;
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

            return responseVoucher;
        }

        @Override
        protected void onPostExecute(String user) {
            mVoucherTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
            int vId;
            String vName,vCode,startDate,endDate,vDesc,vType,vNominal,vPercen, vSdate;
            Integer dataLength;

            if (user != null) {
                try {
                    JSONArray jsonArray = new JSONArray(user);
                    Log.e(TAG, "User : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        mVoucher.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            vId = jsonobject.getInt("id");
                            vName = jsonobject.getString("voucher_name");
                            vCode = jsonobject.getString("voucher_code");
                            vDesc = jsonobject.getString("description");
                            startDate = Tools.dateFormat(jsonobject.getString("start_date").substring(0,10));
                            endDate = Tools.dateFormat(jsonobject.getString("end_date").substring(0,10));
                            vType = jsonobject.getString("type");
                            vNominal = jsonobject.getString("nominal");
                            vPercen = jsonobject.getString("percentage");
                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                            VoucherModul vModul = new VoucherModul();
                            vModul.setId(vId);
                            vModul.setName(vName);
                            vModul.setCode(vCode);
                            vModul.setStartDate(startDate);
                            vModul.setEndDate(endDate);
                            vModul.setDesc(vDesc);
                            vModul.setAmount(jsonobject.getString("quantity"));
                            vModul.setNominal(vNominal);
                            vModul.setPercen(vPercen);
                            vModul.setType(vType);
                            vModul.setAsCategory(jsonobject.getString("id_asset_category"));

                            mVoucher.add(vModul);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.v_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new VoucherAdapter(getApplicationContext(), mVoucher);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Anda belum memiliki Voucher";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Anda belum memiliki Voucher";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mVoucherTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
        }
    }

}

package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.AsyncTask;
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
import id.rentist.mitrarentist.tools.PricingTools;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;

public class TransactionaNewActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<ItemTransaksiModul> mTrans = new ArrayList<>();
    private AsyncTask mTransactionTask = null;

    private LinearLayout noTransImage;
    private TextView noTransText;
    private SpinKitView pBar;
    private SessionManager sm;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String TAG = "TransactionaNewActivity";
    private static final String TOKEN = "secretissecret";
    String tenant, trans_sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Pesanan Baru");

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        noTransImage = (LinearLayout) findViewById(R.id.no_trans);
        noTransText = (TextView) findViewById(R.id.no_trans_text);
        pBar.setIndeterminateDrawable(fadingCircle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
//        getNewTransactionDataList(tenant);
        getTransaction();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTrans.clear();
                getTransaction();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getTransaction() {
        String newURL = AppConfig.URL_TRANSACTION_NEW + tenant;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.GET, newURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        transactionData(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Trans Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        queue.add(strReq);
    }

    private void transactionData(String transaction) {
        pBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);

        String errorMsg, aIdTrans, aCodeTrans, aThumb, aMember, aStartDate, aEndDate, aNominal, aAsetName, aIdMember;

        if (transaction != null) {
            try {
                JSONArray jsonArray = new JSONArray(transaction);
                trans_sum = String.valueOf(jsonArray.length());
                setTitle("Pesanan Baru (" + trans_sum + ")");

                if(jsonArray.length() > 0){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        errorMsg = "-";
                        JSONObject transObject = jsonArray.getJSONObject(i);
                        Log.e(TAG, "Transaction Data : " + String.valueOf(transObject));

                        ItemTransaksiModul itemTrans = new ItemTransaksiModul();
                        String aVoucherCode = "", aVoucherDisc = "";

                        JSONObject idTrans = transObject.getJSONObject("id_transaction");
                        JSONObject memberObject = transObject.getJSONObject("id_member");
                        JSONArray items = transObject.getJSONArray("item");
                        JSONObject item;

                        aIdTrans = transObject.getString("id");
                        aAsetName = "- Item Kosong -";
                        String aAsetThumb = "null";

                        if(items.length() > 0){
                            if (items.length() == 1){
                                item = items.getJSONObject(0);
                                aAsetThumb = item.getString("main_image");
                                aAsetName = item.getString("name");

//                                if (item.getString("id_asset_category").equals("3")){
//                                    aAsetName = item.getString("type") + " " + item.getString("sub_type");
//                                }else {
//                                    aAsetName = item.getString("brand") + " " + item.getString("type");
//                                }
                            }
                        }

                        if (transObject.has("id_voucher")){
                            JSONObject voucherObject = transObject.getJSONObject("id_voucher");
                            aVoucherCode = voucherObject.getString("voucher_code");
                            if (voucherObject.getInt("nominal") == 0){
                                aVoucherDisc = "(Disk. " + voucherObject.getInt("percentage") + "%)";
                            }else{
                                aVoucherDisc = "(Disk. " + PricingTools.PriceFormat(voucherObject.getInt("nominal")) + ")";
                            }
                        }

                        JSONArray additional = transObject.getJSONArray("additional");
                        ArrayList<String> idAdditional = new ArrayList<String>();
                        if(additional.length() > 0) {
                            for (int j = 0; j < additional.length(); j++) {
                                JSONObject add = additional.getJSONObject(j);
                                JSONObject add_feature = add.getJSONObject("id_feature_item");
                                idAdditional.add(add_feature.getString("id_additional_feature"));
                            }
                        }

                        aCodeTrans = idTrans.getString("transaction_code");
                        aNominal = transObject.getString("nominal");
                        aIdMember = memberObject.getString("id");
                        aMember = memberObject.getString("firstname") + " " + memberObject.getString("lastname");
                        aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                        aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);
                        aThumb = memberObject.getString("profil_pic");

                        itemTrans.setStatus("new");
                        itemTrans.setCodeTrans(aCodeTrans);
                        itemTrans.setIdTrans(aIdTrans);
                        itemTrans.setAsetThumb(aAsetThumb);
                        itemTrans.setAsetName(aAsetName);
                        itemTrans.setIdMember(aIdMember);
                        itemTrans.setMember(aMember);
                        itemTrans.setPrice(aNominal);
                        itemTrans.setThumbnail(aThumb);
                        itemTrans.setStartDate(Tools.dateFormat(aStartDate));
                        itemTrans.setEndDate(Tools.dateFormat(aEndDate));
                        itemTrans.setPickTime(transObject.getString("pickup_time"));
                        itemTrans.setLat(transObject.getString("latitude"));
                        itemTrans.setLong(transObject.getString("longitude"));
                        itemTrans.setAddress(transObject.getString("address"));
                        itemTrans.setNote(transObject.getString("notes"));
                        itemTrans.setIdAddtional(idAdditional.toString());
                        itemTrans.setOrderDate(transObject.getString("createdAt"));
                        itemTrans.setInsurance(transObject.getString("insurance"));
                        itemTrans.setDriverIncluded(transObject.getString("with_driver"));
                        itemTrans.setVoucherCode(aVoucherCode);
                        itemTrans.setVoucherDisc(aVoucherDisc);

                        mTrans.add(itemTrans);
                    }

                    mRecyclerView = (RecyclerView) findViewById(R.id.tr_recyclerView);
                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mAdapter = new TransaksiAdapter(getApplicationContext(), mTrans);

                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                    noTransImage.setVisibility(View.GONE);


                }else{
                    errorMsg = "Tidak ada pesanan baru";
                    noTransImage.setVisibility(View.VISIBLE);
                    noTransText.setText(errorMsg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errorMsg = "Transaksi Tidak Ditemukan";
                Log.e(TAG, "JSON Error : " + e);
                noTransImage.setVisibility(View.VISIBLE);
                noTransText.setText(errorMsg);
            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            mTrans.clear();
            getNewTransactionDataList(tenant);
        }

    }

    @Override
    public void onRestart(){
        super.onRestart();
        mTrans.clear();
        getTransaction();

    }

    public void getNewTransactionDataList(String tenant) {
        pBar.setVisibility(View.VISIBLE);
        new TransactionaNewActivity.getNewTransactionDataListTask(tenant).execute();
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
            pBar.setVisibility(View.GONE);

            String aIdTrans, aCodeTrans, aThumb, aMember, aStartDate, aEndDate, aNominal, aAsetName, aIdMember;

            if (transaction != null) {
                try {
                    JSONArray jsonArray = new JSONArray(transaction);

                    if(jsonArray.length() > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject transObject = jsonArray.getJSONObject(i);
                            Log.e(TAG, "Transaction Data : " + String.valueOf(transObject));

                            JSONObject idTrans = transObject.getJSONObject("id_transaction");
                            JSONObject memberObject = transObject.getJSONObject("id_member");
                            JSONArray items = transObject.getJSONArray("item");
                            JSONObject item;

                            aIdTrans = transObject.getString("id");
                            aAsetName = "- Item Kosong -";
                            String aAsetThumb = "null";

                            if(items.length() > 0){
                                if (items.length() == 1){
                                    item = items.getJSONObject(0);
                                    aAsetThumb = item.getString("main_image");
                                    if (item.getString("id_asset_category").equals("3")){
                                        aAsetName = item.getString("type") + " " + item.getString("sub_type");
                                    }else {
                                        aAsetName = item.getString("brand") + " " + item.getString("type");
                                    }
                                }
                            }

                            JSONArray additional = transObject.getJSONArray("additional");
                            ArrayList<String> idAdditional = new ArrayList<String>();
                            if(additional.length() > 0) {
                                for (int j = 0; j < additional.length(); j++) {
                                    JSONObject add = additional.getJSONObject(j);
                                    JSONObject add_feature = add.getJSONObject("id_feature_item");
                                    idAdditional.add(add_feature.getString("id_additional_feature"));
                                }
                            }

                            aCodeTrans = idTrans.getString("transaction_code");
                            aNominal = transObject.getString("nominal");
                            aIdMember = memberObject.getString("id");
                            aMember = memberObject.getString("firstname") + " " + memberObject.getString("lastname");
                            aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                            aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);
                            aThumb = memberObject.getString("profil_pic");

                            ItemTransaksiModul itemTrans = new ItemTransaksiModul();
                            itemTrans.setCodeTrans(aCodeTrans);
                            itemTrans.setIdTrans(aIdTrans);
                            itemTrans.setAsetThumb(aAsetThumb);
                            itemTrans.setAsetName(aAsetName);
                            itemTrans.setIdMember(aIdMember);
                            itemTrans.setMember(aMember);
                            itemTrans.setPrice(aNominal);
                            itemTrans.setThumbnail(aThumb);
                            itemTrans.setStartDate(Tools.dateFormat(aStartDate));
                            itemTrans.setEndDate(Tools.dateFormat(aEndDate));
                            itemTrans.setPickTime(transObject.getString("pickup_time"));
                            itemTrans.setLat(transObject.getString("latitude"));
                            itemTrans.setLong(transObject.getString("longitude"));
                            itemTrans.setAddress(transObject.getString("address"));
                            itemTrans.setNote(transObject.getString("notes"));
                            itemTrans.setIdAddtional(idAdditional.toString());

                            mTrans.add(itemTrans);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.tr_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new TransaksiAdapter(getApplicationContext(), mTrans);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Tidak ada pesanan baru";
                        noTransImage.setVisibility(View.VISIBLE);
                        noTransText.setText(errorMsg);
//                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Transaksi Tidak Ditemukan";
                    noTransImage.setVisibility(View.VISIBLE);
                    noTransText.setText(errorMsg);

//                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mTransactionTask = null;
            pBar.setVisibility(View.GONE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_refresh_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_refresh) {
//            mTrans.clear();
//            getNewTransactionDataList(tenant);
//        }

        return super.onOptionsItemSelected(item);
    }


}

package id.rentist.mitrarentist.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.adapter.TransactionAcceptAdapter;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class TransactionAcceptFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private List<ItemTransaksiModul> mTrans;
    private AsyncTask mHistoryTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private View view;
    private SpinKitView pBar;

    private LinearLayout noTransImage;
    private TextView noTransText;

    private static final String TAG = "TransactionActivity";
    private static final String TOKEN = "secretissecret";
    String tenant;

    public TransactionAcceptFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transaction_accept, container, false);
        mTrans = new ArrayList<ItemTransaksiModul>();
        sm = new SessionManager(getActivity());

        pBar = (SpinKitView) view.findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        noTransImage = (LinearLayout) view.findViewById(R.id.no_trans);
        noTransText = (TextView) view.findViewById(R.id.no_trans_text);


        // action retrieve data aset
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getTransaction();

//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
//                new IntentFilter("transaction-accept"));
        return view;
    }

//    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent i) {
//            // Get extra data included in the Intent
//            Intent iTrans = new Intent(getActivity(), TransDetailActivity.class);
//            iTrans.putExtra("status", "accepted");
//            iTrans.putExtra("id_trans", i.getStringExtra("id_trans"));
//            iTrans.putExtra("code_trans", i.getStringExtra("code_trans"));
//            iTrans.putExtra("price", i.getStringExtra("price"));
//            iTrans.putExtra("aset", i.getStringExtra("aset"));
//            iTrans.putExtra("member", i.getStringExtra("member"));
//            iTrans.putExtra("startDate", i.getStringExtra("startDate"));
//            iTrans.putExtra("endDate", i.getStringExtra("endDate"));
//            iTrans.putExtra("driver",  i.getStringExtra("driver"));
//            iTrans.putExtra("driver_name",  i.getStringExtra("driver_name"));
//            startActivityForResult(iTrans, 2);
//        }
//    };

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode == RESULT_OK) {
//            mTrans.clear();
//            getTransaction();
//        }
//
//    }

    private void getHistoryAccList(String tenant) {
        pBar.setVisibility(View.VISIBLE);

        new TransactionAcceptFragment.getHistoryAccListTask(tenant).execute();
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

    public class getHistoryAccListTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responseHistory;

        public getHistoryAccListTask(String tenant) {
            this.mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String newURL = AppConfig.URL_TRANSACTION + mTenant;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseHistory = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "History Fetch Error : " + errorMsg);
                    Toast.makeText(getActivity(), "Connection error, try again.",
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

            return responseHistory;
        }

        @Override
        protected void onPostExecute(String history) {
            mHistoryTask = null;
//            showProgress(false);
            pBar.setVisibility(View.GONE);

            String aIdTrans, aCodeTrans, aMember, aStartDate, aEndDate, aNominal, aAsetName;
            Boolean aDriverIncluded;

            if (history != null) {
                try {
                    JSONObject jsonObject = new JSONObject(history);
                    JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("accepted")));
                    if(jsonArray.length() > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject transObject = jsonArray.getJSONObject(i);
                            ItemTransaksiModul itemTrans = new ItemTransaksiModul();

                            Log.e(TAG, "Accepted Data : " + String.valueOf(transObject));

                            JSONObject idTrans = transObject.getJSONObject("id_transaction");
                            JSONArray items = transObject.getJSONArray("item");
                            JSONObject memberObject = transObject.getJSONObject("id_member");
                            JSONObject item;

                            aIdTrans = transObject.getString("id");
                            aAsetName = "- Item Kosong -";

                            if(items.length() > 0){
                                if (items.length() == 1){
                                    item = items.getJSONObject(0);
                                    aAsetName = item.getString("brand") + " " + item.getString("type") + " | " + item.getString("license_plat");

                                    String driverStat = item.getString("driver_included");
                                    aDriverIncluded = item.getInt("id_asset_category") == 1 && driverStat != "false";
                                    itemTrans.setDriverIncluded(aDriverIncluded);

                                } else {

                                }
                            }

                            aCodeTrans = idTrans.getString("transaction_code");
                            aNominal = transObject.getString("nominal");
                            aMember = memberObject.getString("firstname") + " " + memberObject.getString("lastname");
                            aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                            aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);

                            itemTrans.setIdTrans(aIdTrans);
                            itemTrans.setCodeTrans(aCodeTrans);
                            itemTrans.setAsetName(aAsetName);
                            itemTrans.setMember(aMember);
                            itemTrans.setPrice(aNominal);
                            itemTrans.setStartDate(aStartDate);
                            itemTrans.setEndDate(aEndDate);
//

                            mTrans.add(itemTrans);
                        }

                        mRecyclerView = (RecyclerView) view.findViewById(R.id.haccept_recyclerViewFrag);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mAdapter = new TransactionAcceptAdapter(getActivity(),mTrans);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Transaksi Diterima Tidak Ditemukan";
                        noTransImage.setVisibility(View.VISIBLE);
                        noTransText.setText(errorMsg);
//                      Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Transaksi Tidak Ditemukan";
                    noTransImage.setVisibility(View.VISIBLE);
                    noTransText.setText(errorMsg);
//                  Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }



        @Override
        protected void onCancelled() {
            mHistoryTask = null;
//            showProgress(false);
            pBar.setVisibility(View.GONE);

        }
    }

    private void getTransaction() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TRANSACTION + tenant, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        transactionData(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Driver Fetch Error : " +  error.toString());
                Toast.makeText(getActivity(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
//        pBar.setVisibility(View.GONE);
        queue.add(strReq);
    }

    private void transactionData(String responseJson) {
        String aIdTrans, aCodeTrans, aMember, aStartDate, aEndDate, aNominal, aAsetName, aThumb, aDriverName = "", errorMsg;
        Boolean aDriverIncluded;

        try {
            JSONObject jsonObject = new JSONObject(responseJson);
            JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("accepted")));
            if(jsonArray.length() > 0){
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject transObject = jsonArray.getJSONObject(i);
                    ItemTransaksiModul itemTrans = new ItemTransaksiModul();

                    Log.e(TAG, "Accepted Data : " + String.valueOf(transObject));

                    JSONObject idTrans = transObject.getJSONObject("id_transaction");
                    JSONArray items = transObject.getJSONArray("item");
                    JSONObject memberObject = transObject.getJSONObject("id_member");
                    JSONObject item;

                    if (transObject.has("id_driver")) {
                        JSONObject driverObject = transObject.getJSONObject("id_driver");
                        aDriverName = driverObject.getString("fullname");
                    }

                    if (transObject.has("id_tenant_user")) {
                        JSONObject userObject = transObject.getJSONObject("id_tenant_user");
                        aDriverName = userObject.getString("name");
                    }

                    aIdTrans = transObject.getString("id");
                    aAsetName = "- Item Kosong -";

                    if(items.length() > 0){
                        if (items.length() == 1){
                            item = items.getJSONObject(0);
                            if (item.getString("id_asset_category").equals("3")){
                                aAsetName = item.getString("type") + " " + item.getString("subtype");
                            }else {
                                if (item.getInt("id_asset_category") == 1 ){
                                    String driverStat = item.getString("driver_included");
                                    aDriverIncluded = item.getInt("id_asset_category") == 1 && driverStat != "false";
                                    itemTrans.setDriverIncluded(aDriverIncluded);
                                }
                                aAsetName = item.getString("brand") + " " + item.getString("type");
                            }

                        } else {

                        }
                    }

                    aCodeTrans = idTrans.getString("transaction_code");
                    aNominal = transObject.getString("nominal");
                    aMember = memberObject.getString("firstname") + " " + memberObject.getString("lastname");
                    aThumb = memberObject.getString("profil_pic");
                    aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                    aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);

                    itemTrans.setIdTrans(aIdTrans);
                    itemTrans.setCodeTrans(aCodeTrans);
                    itemTrans.setAsetName(aAsetName);
                    itemTrans.setMember(aMember);
                    itemTrans.setThumbnail(aThumb);
                    itemTrans.setPrice(aNominal);
                    itemTrans.setStartDate(aStartDate);
                    itemTrans.setEndDate(aEndDate);
                    itemTrans.setDriverName(aDriverName);

                    mTrans.add(itemTrans);
                }

                mRecyclerView = (RecyclerView) view.findViewById(R.id.haccept_recyclerViewFrag);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new TransactionAcceptAdapter(getActivity(),mTrans);
                mRecyclerView.setAdapter(mAdapter);

            }else{
                errorMsg = "Tidak Ada Transaksi Diterima";
                noTransImage.setVisibility(View.VISIBLE);
                noTransText.setText(errorMsg);
//                      Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
            }

            Log.e(TAG, "TAB Transaction Data : " + jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

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

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.adapter.HistoryAcceptAdapter;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class HistoryAcceptFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private List<ItemTransaksiModul> mTrans;
    private AsyncTask mHistoryTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private View view;

    private static final String TAG = "HistoryActivity";
    private static final String TOKEN = "secretissecret";
    String tenant;


    public HistoryAcceptFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_accept, container, false);
        mTrans = new ArrayList<ItemTransaksiModul>();
        sm = new SessionManager(getActivity());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // action retrieve data aset
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getHistoryAccList(tenant);

        return view;
    }

    private void getHistoryAccList(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new HistoryAcceptFragment.getHistoryAccListTask(tenant).execute();
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
            String newURL = AppConfig.URL_HISTORY_TRANS + mTenant;
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
            showProgress(false);
            String aIdTrans, aTitle, aMember, aStartDate, aEndDate, aNominal, aAsetName;
            Integer aId, dataLength;

            if (history != null) {
                try {
                    JSONObject jsonObject = new JSONObject(history);
                    JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("canceled")));
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject transObject = jsonArray.getJSONObject(i);
                            Log.e(TAG, "Canceled Data : " + String.valueOf(transObject));

                            JSONObject idTrans = transObject.getJSONObject("id_transaction");
                            JSONArray items = transObject.getJSONArray("item");
                            JSONObject item = new JSONObject();

                            aAsetName = "- Item Kosong -";

                            if(items.length() > 0){
                                if (items.length() == 1){
                                    item = items.getJSONObject(0);
                                    aAsetName = item.getString("brand") + " " + item.getString("type") + " | " + item.getString("license_plat");

                                } else {

                                }
                            }

                            aIdTrans = idTrans.getString("transaction_code");
                            aNominal = transObject.getString("nominal") + " IDR";
                            aMember = transObject.getString("firstname") + " " + transObject.getString("lastname");
                            aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                            aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);

                            ItemTransaksiModul itemTrans = new ItemTransaksiModul();
                            itemTrans.setIdTrans(aIdTrans);
                            itemTrans.setAsetName(aAsetName);
                            itemTrans.setMember(aMember);
                            itemTrans.setPrice(aNominal);
                            itemTrans.setStartDate(aStartDate);
                            itemTrans.setEndDate(aEndDate);

                            mTrans.add(itemTrans);
                        }

                        mRecyclerView = (RecyclerView) view.findViewById(R.id.haccept_recyclerViewFrag);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mAdapter = new HistoryAcceptAdapter(getActivity(),mTrans);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Transaksi Dibatalkan Tidak Ditemukan";
                        Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Riwayat Tidak Ditemukan";
                    Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mHistoryTask = null;
            showProgress(false);
        }
    }
}

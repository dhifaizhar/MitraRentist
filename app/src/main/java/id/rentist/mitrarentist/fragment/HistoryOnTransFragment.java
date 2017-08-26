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
import id.rentist.mitrarentist.adapter.HistoryOnTransAdapter;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by mdhif on 13/07/2017.
 */

public class HistoryOnTransFragment extends Fragment {
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

    public HistoryOnTransFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history_on_transaksi, container, false);
        mTrans = new ArrayList<ItemTransaksiModul>();
        sm = new SessionManager(getActivity());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // action retrieve data aset
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getHistoryOngList(tenant);

        return view;
    }

    private void getHistoryOngList(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getHistoryOngListTask(tenant).execute();
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

    private class getHistoryOngListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseHistory;

        private getHistoryOngListTask(String tenant) {
            mTenant = tenant;
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
            String aTitle, aMember, aDate;
            Integer aId, dataLength;


            if (history != null) {
                try {
                    JSONObject jsonObject = new JSONObject(history);
                    JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("ongoing")));
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            Log.e(TAG, "On Going Data : " + String.valueOf(jsonobject));
//                            aTitle = jsonobject.getString("merk") + " " + jsonobject.getString("type");
                            aMember = jsonobject.getString("firstname") + " " + jsonobject.getString("lastname");
                            aDate = jsonobject.getString("start_date").replace("-","/") + " - " + jsonobject.getString("end_date").replace("-","/");

                            ItemTransaksiModul itemTrans = new ItemTransaksiModul();
                            itemTrans.setTitle("Daihatsu Xenia");
                            itemTrans.setMember(aMember);
                            itemTrans.setDate(aDate);
                            itemTrans.setPrice("900.000 IDR");

                            mTrans.add(itemTrans);
                        }

                        mRecyclerView = (RecyclerView) view.findViewById(R.id.htranson_recyclerViewFrag);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mAdapter = new HistoryOnTransAdapter(getActivity(),mTrans);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Transaksi Berlangsung Tidak Ditemukan";
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

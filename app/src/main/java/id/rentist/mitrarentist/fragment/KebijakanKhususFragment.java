package id.rentist.mitrarentist.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import id.rentist.mitrarentist.FormAddKebijakanActivity;
import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.adapter.KebijakanKhususAdapter;
import id.rentist.mitrarentist.modul.KebijakanModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class KebijakanKhususFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FloatingActionButton mFab;
    private List<KebijakanModul> mKebijakan;
    private AsyncTask mKebijakanTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private View view;

    private static final String TAG = "KebijakanActivity";
    private static final String TOKEN = "secretissecret";
    private String tenant;

    public KebijakanKhususFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mKebijakan = new ArrayList<KebijakanModul>();
        view = inflater.inflate(R.layout.fragment_kebijakan_khusus, container, false);
        sm = new SessionManager(getActivity());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // action retrieve data kebijakan
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getKebijikanList(tenant);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAddKebijakan = new Intent(v.getContext(), FormAddKebijakanActivity.class);
                v.getContext().startActivity(iAddKebijakan);
            }
        });

        return view;
    }

    private void getKebijikanList(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getKebijakanListTask(tenant).execute();
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

    private class getKebijakanListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseKebijakan;

        private getKebijakanListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String newURL = AppConfig.URL_LIST_KEBIJAKAN + mTenant;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseKebijakan = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Kebijakan Fetch Error : " + errorMsg);
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

            return responseKebijakan;
        }

        @Override
        protected void onPostExecute(String kebijakan) {
            mKebijakanTask = null;
            showProgress(false);
            String aTitle, aDesc, aDate;
            Integer aId, dataLength;

            if (kebijakan != null) {
                try {
                    JSONArray jsonArray = new JSONArray(kebijakan);
                    Log.e(TAG, "Kebijakan : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            KebijakanModul kebijakanModul = new KebijakanModul();
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            Log.e(TAG, "Complete Data : " + String.valueOf(jsonobject));
                            aTitle = jsonobject.getString("title");
                            aDesc = jsonobject.getString("description");
                            aDate = jsonobject.getString("createdAt");
                            kebijakanModul.setTitle(aTitle);
                            kebijakanModul.setDesc(aDesc);
                            mKebijakan.add(kebijakanModul);
                        }
                        mRecyclerView = (RecyclerView) view.findViewById(R.id.kbjk_recyclerViewFrag);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mAdapter = new KebijakanKhususAdapter(getActivity(), mKebijakan);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Riwayat Selesai Tidak Ditemukan";
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
            mKebijakan = null;
            showProgress(false);
        }
    }
}

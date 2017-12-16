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
import id.rentist.mitrarentist.adapter.KebijakanUmumAdapter;
import id.rentist.mitrarentist.modul.KebijakanModul;
import id.rentist.mitrarentist.tools.AppConfig;

public class KebijakanUmumFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private List<KebijakanModul> mKebijakan =new ArrayList<>();

    private AsyncTask mKebijakanTask = null;
    RecyclerView.Adapter mAdapter;

    private ProgressDialog pDialog;
    private View view;

    private static final String TAG = "KebijakanActivity";
    private static final String TOKEN = "secretissecret";
    public KebijakanUmumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<KebijakanModul> mKebijakan = new ArrayList<KebijakanModul>();
        view = inflater.inflate(R.layout.fragment_kebijakan_umum, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        getKebijikanList();

        return view;
    }

    private void getKebijikanList() {
        new KebijakanUmumFragment.getKebijakanListTask().execute();
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
        private String errorMsg, responseKebijakan;

        private getKebijakanListTask() {

        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String newURL = AppConfig.URL_LIST_KEBIJAKAN + "0";
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
            Integer aId, dataLength;

            if (kebijakan != null) {
                try {
                    JSONArray jsonArray = new JSONArray(kebijakan);
                    Log.e(TAG, "Kebijakan : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            Log.e(TAG, "Complete Data : " + String.valueOf(jsonobject));

                            KebijakanModul kebijakanModul = new KebijakanModul();
                            kebijakanModul.setId(jsonobject.getInt("id"));
                            kebijakanModul.setTitle(jsonobject.getString("title"));
                            kebijakanModul.setDesc(jsonobject.getString("description"));
                            mKebijakan.add(kebijakanModul);
                        }
                        mRecyclerView = (RecyclerView) view.findViewById(R.id.kbju_recyclerViewFrag);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mAdapter = new KebijakanUmumAdapter(getActivity(), mKebijakan);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Data Kebijakan Umum Tidak Ditemukan";
                        Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Data Kebijakan Umum Tidak Ditemukan";
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

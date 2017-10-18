package id.rentist.mitrarentist.Tab;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.fragment.HistoryCancelFragment;
import id.rentist.mitrarentist.fragment.HistoryCompTransFragment;
import id.rentist.mitrarentist.fragment.HistoryOnTransFragment;
import id.rentist.mitrarentist.fragment.TransactionAcceptFragment;
import id.rentist.mitrarentist.fragment.TransactionRejectFragment;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by mdhif on 07/07/2017.
 */

public class TransactionTabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles={"Diterima","Berlangsung", "Selesai", "Dibatalkan", "Ditolak"};
    private SessionManager sm;
    private SpinKitView pBar;
    private AsyncTask mTransactionTask = null;

    private String acceptData;

    private static final String TAG = "TransactionActivity";
    private static final String TOKEN = "secretissecret";

    // CHANGE STARTS HERE
    private int current_position=0;

    public void set_current_position(int i) {
        current_position = i;
    }
    // CHANGE ENDS HERE

    public TransactionTabAdapter(FragmentManager fm, Context c){
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        sm = new SessionManager(mContext);
        String tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
//        new getTransactionDataTask(tenant).execute();

        switch (position){
            case 0 :
                frag = new TransactionAcceptFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("data", acceptData);
//                frag.setArguments(bundle);
                break;
            case 1 :
                frag = new HistoryOnTransFragment(); break;
            case 2 :
                frag = new HistoryCompTransFragment(); break;
            case 3 :
                frag = new HistoryCancelFragment(); break;
            case 4 :
                frag = new TransactionRejectFragment(); break;
        }

        Bundle b = new Bundle();
        b.putInt("position", position);
        if(frag != null){
            frag.setArguments(b);
        }
        return frag;
    }

    private class getTransactionDataTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseHistory;

        getTransactionDataTask(String tenant) {
            this.mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
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
                    Toast.makeText(mContext, "Connection error, try again.",
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
            mTransactionTask = null;
//            pBar.setVisibility(View.GONE);

            String aIdTrans, aCodeTrans, aTitle, aMember, aStartDate, aEndDate, aNominal, aAsetName;

            if (history != null) {
                try {
                    JSONObject jsonObject = new JSONObject(history);
                    JSONArray acceptArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("accepted")));
                    JSONArray ongoingArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("ongoing")));
                    JSONArray completedArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("completed")));
                    JSONArray cancelArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("canceled")));
                    JSONArray rejectArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("rejected")));
                    Log.e(TAG, "Transaction Data : " + jsonObject);

                    if(acceptArray.length() > 0) {
                        acceptData = acceptArray.toString();
                    }else {
                        acceptData = "null";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Transaksi Tidak Ditemukan";
//                  Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mTransactionTask = null;
//            showProgress(false);
//            pBar.setVisibility(View.GONE);

        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position){
        if (position == current_position) {
            return "Diterima";
        }else if(position == current_position+1){
            return "Berlangsung";
        }else if(position == current_position+2){
            return "Selesai";
        }else if(position == current_position+3){
            return "Dibatalkan";
        }else if(position == current_position+4){
            return "Ditolak";
        }

        return null;
    }
}

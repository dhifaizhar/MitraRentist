package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.Tab.SlidingTabLayout;
import id.rentist.mitrarentist.Tab.TransactionTabAdapter;
import id.rentist.mitrarentist.fragment.TransactionAcceptFragment;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class TransactionActivity extends AppCompatActivity {
    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;
    TabLayout mTabLayout;
    private AsyncTask mTransactionTask = null;
    private SessionManager sm;
    private SpinKitView pBar;
    String tenant;

    private static final String TAG = "TransactionActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        setTitle("Transaksi");

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView) findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TransactionTabAdapter fragTrans= new TransactionTabAdapter(getSupportFragmentManager(),this);
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(fragTrans);

        mTabLayout = (TabLayout) findViewById(R.id.stl_tab_hs);
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorWhite));
        mTabLayout.setupWithViewPager(mViewPager);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
//        new getTransactionDataTask(tenant);
    }

    private class getTransactionDataTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseHistory;

        getTransactionDataTask(String tenant) {
            this.mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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

            return responseHistory;
        }

        @Override
        protected void onPostExecute(String history) {
            mTransactionTask = null;
            pBar.setVisibility(View.GONE);

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

                    // Accepted Transaction
                    Bundle acceptBundle = new Bundle();

                    if(acceptArray.length() > 0) {
                        acceptBundle.putString("data", acceptArray.toString());
                    }else {
                        acceptBundle.putString("data", "null");
                    }

                    // set Fragmentclass Arguments
                    TransactionAcceptFragment fragAccept = new TransactionAcceptFragment();
                    fragAccept.setArguments(acceptBundle);

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
            pBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_option, menu);
        getMenuInflater().inflate(R.menu.menu_search_option, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //ubah dengan fungsi
            return true;
        }

        if (id == R.id.action_refresh) {
            //ubah dengan fungsi
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Intent intent = new Intent(TransactionActivity.this,DashboardActivity.class);
        setResult(RESULT_OK, intent);
        this.finish();

        return true;
    }
}

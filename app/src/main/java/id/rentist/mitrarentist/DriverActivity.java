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

import id.rentist.mitrarentist.adapter.DriverAdapter;
import id.rentist.mitrarentist.modul.ItemDriverModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class DriverActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private List<ItemDriverModul> mDriver = new ArrayList<>();
    private AsyncTask mDriverTask = null;
    private ProgressDialog pDialog;
    private SpinKitView pBar;
    private SessionManager sm;

    Intent iDriver;
    LinearLayout noData;

    private static final String TAG = "DriverActivity";
    private static final String TOKEN = "secretissecret";
    private String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        setTitle("Pengemudi Terdaftar");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        sm = new SessionManager(getApplicationContext());
//        pBar = (SpinKitView)findViewById(R.id.progressBar);
//        FadingCircle fadingCircle = new FadingCircle();
//        pBar.setIndeterminateDrawable(fadingCircle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        showProgress(true);
        getDriverDataList(tenant);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDriverDataList(tenant);
            }
        });

        noData = (LinearLayout) findViewById(R.id.no_data);
        TextView toFormAdd = (TextView) findViewById(R.id.toFormAdd);
        toFormAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverActivity.this, FormDriverActivity.class);
                intent.putExtra("action","add");
                startActivity(intent);
            }
        });
    }

    private void getDriverDataList(String tenant) {
//        pBar.setVisibility(View.VISIBLE);
//        new getDriverListTask(tenant).execute();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String newURL = AppConfig.URL_LIST_DRIVER + tenant;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, newURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mSwipeRefreshLayout.setRefreshing(false);
                showProgress(false);
                String aId, aName, aSIM, aThumbPhoto, aPhone, aEmail;
                Integer dataLength;

                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("driver");
                        Log.e(TAG, "User : " + jsonArray);
                        dataLength = jsonArray.length();
                        mDriver.clear();
                        if(dataLength > 0){
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                aId = jsonobject.getString("id");
                                aName = jsonobject.getString("fullname");
                                aEmail = jsonobject.getString("email");
                                aSIM = jsonobject.getString("no_sim");
                                aThumbPhoto = jsonobject.getString("profile_pic");
                                Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                                ItemDriverModul driverModul = new ItemDriverModul();
                                driverModul.setId(aId);
                                driverModul.setName(aName);
                                driverModul.setSIM(aSIM);
                                driverModul.setProfPic(aThumbPhoto);
                                driverModul.setEmail(aEmail);
                                mDriver.add(driverModul);
                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.dr_recyclerView);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mAdapter = new DriverAdapter(getApplicationContext(),mDriver);

                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                            noData.setVisibility(View.GONE);

                        }else{
                            noData.setVisibility(View.VISIBLE);
//                            Toast.makeText(getApplicationContext(),"Anda belum memiliki Driver", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Driver Tidak Ditemukan", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e(TAG, "Driver Fetch Error : " + error.toString());
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            iDriver = new Intent(DriverActivity.this, FormDriverActivity.class);
            iDriver.putExtra("action","add");
            startActivityForResult(iDriver, 2);
        }

        return super.onOptionsItemSelected(item);
    }

    private class getDriverListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseDriver;

        private getDriverListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String newURL = AppConfig.URL_LIST_DRIVER + mTenant;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseDriver = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Driver Fetch Error : " + errorMsg);
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

            return responseDriver;
        }

        @Override
        protected void onPostExecute(String user) {
            mDriverTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
            String aId, aName, aSIM, aThumbPhoto;
            Integer dataLength;

            if (user != null) {
                try {
                    JSONObject jsonObject = new JSONObject(user);
                    JSONArray jsonArray = jsonObject.getJSONArray("driver");
                    Log.e(TAG, "User : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";

                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            aId = jsonobject.getString("id");
                            aName = jsonobject.getString("fullname");
                            aSIM = jsonobject.getString("no_sim");
                            aThumbPhoto = jsonobject.getString("profile_pic");
                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                            ItemDriverModul driverModul = new ItemDriverModul();
                            driverModul.setId(aId);
                            driverModul.setName(aName);
                            driverModul.setSIM(aSIM);
                            driverModul.setProfPic(aThumbPhoto);
                            mDriver.add(driverModul);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.dr_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new DriverAdapter(getApplicationContext(),mDriver);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Anda belum memiliki Driver";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Anda belum memiliki Driver";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mDriverTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            DriverActivity.this.finish();
            Intent ii = new Intent(DriverActivity.this,DriverActivity.class);
            startActivity(ii);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        getDriverDataList(tenant);

    }

    @Override
    public void onRestart(){
        super.onRestart();
        getDriverDataList(tenant);

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
}

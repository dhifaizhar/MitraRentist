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

import id.rentist.mitrarentist.adapter.AsetAdapter;
import id.rentist.mitrarentist.modul.ItemAsetModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by mdhif on 17/09/2017.
 */

public class AsetListActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private List<ItemAsetModul> mAset = new ArrayList<>();
    private AsyncTask mAssetTask = null;
    private ProgressDialog pDialog;
    SessionManager sm;
    Intent iAset;

    String tenant, category, name_category;

    private static final String TAG = "AssetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aset_list);

        iAset = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name_category = iAset.getStringExtra("name_category");
        setTitle("Aset " + name_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAset.clear();
                getAssetDataList(tenant);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // action retrieve data aset
        category = iAset.getStringExtra("id_category");
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getAssetDataList(tenant);
    }

    private void getAssetDataList(String tenant) {
        pDialog.setMessage("loading asset...");
        showProgress(true);
        new getAssetListTask(tenant).execute();
    }

    private class getAssetListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseAsset;

        private getAssetListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_LIST_ASSET + category;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseAsset = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Asset Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    return keys;
                }

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

            return responseAsset;
        }

        @Override
        protected void onPostExecute(String aset) {
            mAssetTask = null;
            showProgress(false);
            String aName, aType, aPlat, aYear, aStatus, aSubCat;
            Integer dataLength, aId;

            if (aset != null) {
                try {
                    JSONArray jsonArray = new JSONArray(aset);
                    Log.e(TAG, "Asset : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";

                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            aId = jsonobject.getInt("id");
                            aName = jsonobject.getString("brand");
                            aType = jsonobject.getString("type");
                            aPlat = jsonobject.getString("license_plat");
                            aYear = jsonobject.getString("year");
                            aStatus = jsonobject.getString("status");
                            aSubCat = jsonobject.getString("subcategory");

                            ItemAsetModul itemModul = new ItemAsetModul();
                            itemModul.setAssetId(aId);
                            itemModul.setMark(aName + " " + aType);
                            itemModul.setMerk(aName);
                            itemModul.setType(aType);
                            itemModul.setPlat(aPlat);
                            itemModul.setSubCat(aSubCat);
                            if(category.equals("1")){
                                itemModul.setThumbnail(R.drawable.mobil_1);
                            }else if(category.equals("2")){
                                itemModul.setThumbnail(R.drawable.big_bike);
                            }
                            itemModul.setYear(aYear);
                            itemModul.setStatus(aStatus);
                            Log.e(TAG, "What Data : " + String.valueOf(itemModul));
                            mAset.add(itemModul);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.as_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new AsetAdapter(AsetListActivity.this,mAset,category);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Anda belum memiliki Aset " + name_category;
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Anda belum memiliki Aset " + name_category;
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAssetTask = null;
            showProgress(false);
        }
    }

//    private class getAssetBikeListTask extends AsyncTask<String, String, String> {
//        private final String mTenant;
//        private String errorMsg, responseAsset;
//
//        private getAssetBikeListTask(String tenant) {
//            mTenant = tenant;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LIST_MOTOR, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    responseAsset = response;
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    errorMsg = error.toString();
//                    Log.e(TAG, "Asset Fetch Error : " + errorMsg);
//                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() {
//                    // Posting parameters to login url
//                    Map<String, String> keys = new HashMap<String, String>();
//                    keys.put("id_tenant", mTenant);
//                    return keys;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    // Posting parameters to login url
//                    Map<String, String> keys = new HashMap<String, String>();
//                    keys.put("token", TOKEN);
//                    return keys;
//                }
//            };
//
//            try {
//                requestQueue.add(stringRequest);
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Log.e(TAG, String.valueOf(mTenant + " | " + TOKEN));
//
//            return responseAsset;
//        }
//
//        @Override
//        protected void onPostExecute(String aset) {
//            mAssetTask = null;
//            showProgress(false);
//            String aName, aType, aPlat, aStatus, aSeat, aTransm, aAc, aDriver;
//            Integer dataLength, aId;
//
//            if (aset != null) {
//                Log.e(TAG, "Asset Result : " + aset);
//
//                try {
//                    JSONArray jsonArray = new JSONArray(aset);
//                    Log.e(TAG, "Asset : " + jsonArray);
//                    dataLength = jsonArray.length();
//                    if(dataLength > 0){
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            errorMsg = "-";
//
//                            JSONObject jsonobject = jsonArray.getJSONObject(i);
//                            aId = jsonobject.getInt("id");
//                            aName = jsonobject.getString("merk");
//                            aType = jsonobject.getString("type");
//                            aPlat = jsonobject.getString("license_plat");
//                            aStatus = jsonobject.getString("status");
////                            aSeat = jsonobject.getString("seat");
////                            aTransm = jsonobject.getString("transmission");
////                            aAc = jsonobject.getString("air_conditioner");
////                            aDriver = jsonobject.getString("driver_included");
////                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));
//
////                            ItemAsetModul itemModul = new ItemAsetModul();
////                            itemModul.setAssetId(aId);
////                            itemModul.setTitle(aName + " " + aType + " | " + aPlat);
////                            itemModul.setThumbnail(R.drawable.mobil_1);
////                            itemModul.setRating("4/5");
////                            itemModul.setPrice("Rp " + (i*100000) + " /hari");
////                            itemModul.setStatus("[ " + aStatus + " ]");
////                            itemModul.setSeat(aSeat + " kursi");
////                            itemModul.setTransm(aTransm);
////                            itemModul.setAirCon(Boolean.parseBoolean(aAc));
////                            itemModul.setDriver(Boolean.parseBoolean(aDriver));
////                            mAset.add(itemModul);
//                        }
//
////                        mRecyclerView = (RecyclerView) findViewById(R.id.as_recyclerView);
////                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
////                        mAdapter = new AsetAdapter(getApplicationContext(),mAset);
////
////                        mRecyclerView.setLayoutManager(mLayoutManager);
////                        mRecyclerView.setAdapter(mAdapter);
//
//                    }else{
//                        errorMsg = "Anda belum memiliki Aset Motor";
//                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    errorMsg = "Anda belum memiliki Aset Motor";
//                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAssetTask = null;
//            showProgress(false);
//        }
//    }
//
//    private class getAssetYachtListTask extends AsyncTask<String, String, String> {
//        private final String mTenant;
//        private String errorMsg, responseAsset;
//
//        private getAssetYachtListTask(String tenant) {
//            mTenant = tenant;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LIST_YACHT, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    responseAsset = response;
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    errorMsg = error.toString();
//                    Log.e(TAG, "Asset Fetch Error : " + errorMsg);
//                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() {
//                    // Posting parameters to login url
//                    Map<String, String> keys = new HashMap<String, String>();
//                    keys.put("id_tenant", mTenant);
//                    return keys;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    // Posting parameters to login url
//                    Map<String, String> keys = new HashMap<String, String>();
//                    keys.put("token", TOKEN);
//                    return keys;
//                }
//            };
//
//            try {
//                requestQueue.add(stringRequest);
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Log.e(TAG, String.valueOf(mTenant + " | " + TOKEN));
//
//            return responseAsset;
//        }
//
//        @Override
//        protected void onPostExecute(String aset) {
//            mAssetTask = null;
//            showProgress(false);
//            String aName, aType, aPlat, aStatus, aSeat, aTransm, aAc, aDriver;
//            Integer dataLength, aId;
//
//            if (aset != null) {
//                Log.e(TAG, "Asset Result : " + aset);
//
//                try {
//                    JSONArray jsonArray = new JSONArray(aset);
//                    Log.e(TAG, "Asset : " + jsonArray);
//                    dataLength = jsonArray.length();
//                    if(dataLength > 0){
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            errorMsg = "-";
//
//                            JSONObject jsonobject = jsonArray.getJSONObject(i);
//                            aId = jsonobject.getInt("id");
//                            aName = jsonobject.getString("merk");
//                            aType = jsonobject.getString("type");
//                            aPlat = jsonobject.getString("license_plat");
////                            aStatus = jsonobject.getString("status");
////                            aSeat = jsonobject.getString("seat");
////                            aTransm = jsonobject.getString("transmission");
////                            aAc = jsonobject.getString("air_conditioner");
////                            aDriver = jsonobject.getString("driver_included");
////                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));
//
////                            ItemAsetModul itemModul = new ItemAsetModul();
////                            itemModul.setAssetId(aId);
////                            itemModul.setTitle(aName + " " + aType + " | " + aPlat);
////                            itemModul.setThumbnail(R.drawable.mobil_1);
////                            itemModul.setRating("4/5");
////                            itemModul.setPrice("Rp " + (i*100000) + " /hari");
////                            itemModul.setStatus("[ " + aStatus + " ]");
////                            itemModul.setSeat(aSeat + " kursi");
////                            itemModul.setTransm(aTransm);
////                            itemModul.setAirCon(Boolean.parseBoolean(aAc));
////                            itemModul.setDriver(Boolean.parseBoolean(aDriver));
////                            mAset.add(itemModul);
//                        }
//
////                        mRecyclerView = (RecyclerView) findViewById(R.id.as_recyclerView);
////                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
////                        mAdapter = new AsetAdapter(getApplicationContext(),mAset);
////
////                        mRecyclerView.setLayoutManager(mLayoutManager);
////                        mRecyclerView.setAdapter(mAdapter);
//
//                    }else{
//                        errorMsg = "Anda belum memiliki Aset Yacht";
//                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    errorMsg = "Anda belum memiliki Aset Yacht";
//                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAssetTask = null;
//            showProgress(false);
//        }
//    }

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Log.e(TAG, "Kategori Asset : " + category);
            if(category.equals("1")){
                Intent iAddAset = new Intent(AsetListActivity.this, FormCarAsetActivity.class);
                iAddAset.putExtra("action", "add");
                iAddAset.putExtra("id_category", category);
                iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iAddAset);
            }else if(category.equals("2")){
                Intent iAddAset = new Intent(AsetListActivity.this, FormMotorcycleAsetActivity.class);
                iAddAset.putExtra("action", "add");
                iAddAset.putExtra("id_category", category);
                iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iAddAset);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

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
    private SpinKitView pBar;
    SessionManager sm;
    Intent iAset, iAddAset;
    LinearLayout noAset;

    String tenant, category, name_category, category_url, refresh;

    private static final String TAG = "AssetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aset_list);

        iAset = getIntent();
        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

//        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);
        noAset = (LinearLayout) findViewById(R.id.no_aset);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name_category = iAset.getStringExtra("name_category");
        setTitle("Aset " + name_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // action retrieve data aset
        category = iAset.getStringExtra("id_category");
//        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        pBar.setVisibility(View.VISIBLE);
        getAssetDataList(category);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAset.clear();
                noAset.setVisibility(View.GONE);
                getAssetDataList(category);
            }
        });

    }

    public void getAssetDataList(String category) {
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));

        new getAssetListTask(tenant, category).execute();
    }

    private class getAssetListTask extends AsyncTask<String, String, String> {
        private final String mTenant, mCategory;
        private String errorMsg, responseAsset;

        private getAssetListTask(String tenant, String category) {
            mTenant = tenant;
            mCategory = category;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_LIST_ASSET + mCategory;
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
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
//            showProgress(false);
            String aName, aType, aPlat, aYear, aStatus, aSubCat, aSubType, aThumbnail, aVerif;
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
                            aType = jsonobject.getString("type");
                            aStatus = jsonobject.getString("status");
                            aSubCat = jsonobject.getString("subcategory");
                            aThumbnail = jsonobject.getString("main_image");
                            aVerif = jsonobject.getString("verified");

                            ItemAsetModul itemModul = new ItemAsetModul();
                            itemModul.setAssetId(aId);
                            itemModul.setThumbnail(aThumbnail);
                            itemModul.setType(aType);
                            itemModul.setSubCat(aSubCat);
                            itemModul.setVerif(aVerif);

                            int idCate = jsonobject.getInt("id_asset_category");

                            if (idCate == 1 || idCate == 2 ) {
                                aPlat = jsonobject.getString("license_plat");
                                aYear = jsonobject.getString("year");
                                itemModul.setPlat(aPlat);
                                itemModul.setYear(aYear);
                            }

                            if (idCate != 3){
                                aName = jsonobject.getString("brand");
                                itemModul.setMark(aName + " " + aType);
                                itemModul.setMerk(aName);
                            } else {
                                aSubType = jsonobject.getString("sub_type");
                                itemModul.setMark(aType + " " + aSubType);
                            }
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
                        noAset.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    noAset.setVisibility(View.VISIBLE);
                    errorMsg = "Anda belum memiliki Aset " + name_category;
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAssetTask = null;
            pBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
//            showProgress(false);
        }
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
            switch (category) {
                case "1":
                    iAddAset = new Intent(AsetListActivity.this, FormCarAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 3);
                    break;
                case "2":
                    iAddAset = new Intent(AsetListActivity.this, FormMotorcycleAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "3":
                    iAddAset = new Intent(AsetListActivity.this, FormYachtAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "4":
                    iAddAset = new Intent(AsetListActivity.this, FormMedicAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 3);
                    break;
                case "5":
                    iAddAset = new Intent(AsetListActivity.this, FormPhotographyAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "6":
                    iAddAset = new Intent(AsetListActivity.this, FormToysAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "7":
                    iAddAset = new Intent(AsetListActivity.this, FormAdventureAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "8":
                    iAddAset = new Intent(AsetListActivity.this, FormMaternityAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "9":
                    iAddAset = new Intent(AsetListActivity.this, FormElectronicAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "10":
                    iAddAset = new Intent(AsetListActivity.this, FormBicycleAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
                case "11":
                    iAddAset = new Intent(AsetListActivity.this, FormOfficeAsetActivity.class);
                    iAddAset.putExtra("action", "add");
                    iAddAset.putExtra("id_category", category);
                    iAddAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(iAddAset, 2);
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            setTitle("Aset " + data.getStringExtra("asset_name"));
            mAset.clear();
            pBar.setVisibility(View.VISIBLE);
            String cat = data.getStringExtra("asset_category");
            getAssetDataList(cat);
        }

    }
}

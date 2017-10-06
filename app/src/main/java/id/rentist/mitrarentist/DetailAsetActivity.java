package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;


public class DetailAsetActivity extends AppCompatActivity {
    private AsyncTask mDetailAssetTask = null;
    private Toolbar toolbar;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private AlertDialog.Builder showAlert;
    private AlertDialog alertDialog;
    private Intent detIntent;
    private CalendarView simpleCalendarView;
    Intent iAsetEdit;

    Integer aId, position;
    String changeStatus = "active", aName, aType, aPlat, aYear, aStatus, aCat, aSubCat;
    TextView mark, year, status, subcat;
    ImageView imgThumbnail;

    private static final String TAG = "DetailAssetActivity";
    private static final String TOKEN = "secretissecret";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);
        detIntent = getIntent();

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar_detail_aset);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        simpleCalendarView = (CalendarView) findViewById(R.id.calendar_aset);
        simpleCalendarView.setFirstDayOfWeek(2);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        imgThumbnail = (ImageView)findViewById(R.id.as_thumb_aset);
        mark = (TextView) findViewById(R.id.as_mark_det);
        subcat = (TextView) findViewById(R.id.as_subcat_det);
        year = (TextView) findViewById(R.id.as_year_det);
        status = (TextView) findViewById(R.id.as_status_det);

        // set content control value
        aId = detIntent.getIntExtra("id_asset", 0);
        getAssetDataList();
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatAset();
            }
        });


    }

    public void getAssetDataList() {
        pDialog.setMessage("loading data...");
        showProgress(true);
        new getAssetListTask(aId).execute();
    }

    private class getAssetListTask extends AsyncTask<String, String, String> {
        private final String id;
        private String errorMsg, responseAsset;

        private getAssetListTask(int aId) {
            id = String.valueOf(aId);
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_VIEW_CAR;
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
                    keys.put("id_item", id);
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

            return responseAsset;
        }

        @Override
        protected void onPostExecute(String aset) {
            mDetailAssetTask = null;
//            pBar.setVisibility(View.GONE);
            showProgress(false);
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
//                            aId = jsonobject.getInt("id");
                            aName = jsonobject.getString("brand");
                            aType = jsonobject.getString("type");
                            aPlat = jsonobject.getString("license_plat");
                            aYear = jsonobject.getString("year");
                            aStatus = jsonobject.getString("status");
                            aSubCat = jsonobject.getString("subcategory");
                            aCat = jsonobject.getString("id_asset_category");

                            setTitle(aPlat);
                            mark.setText(aName + " " + aType);
                            subcat.setText(aSubCat);
                            year.setText(aYear);
                            status.setText(aStatus);
                            if(aCat.equals("1")){
                                imgThumbnail.setImageResource(R.drawable.car_avanza);
                            }else if(aCat.equals("2")){
                                imgThumbnail.setImageResource(R.drawable.motorcycle_cbr);
                            }
                        }

                    }else{
                        errorMsg = "Gagal Memuat Data";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Gagal Memuat Data";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mDetailAssetTask = null;
//            pBar.setVisibility(View.GONE);
            showProgress(false);
        }
    }

    private void changeStatAset() {
        showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Ubah status aset ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String nowStatus = "active";
                if(nowStatus.equals("active")){
                    changeStatus = "inactive";
                }
                pDialog.setMessage("loading ...");
                showProgress(true);
                new postStatAssetTask(changeStatus).execute();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        alertDialog = showAlert.create();
        alertDialog.show();
    }

    private class postStatAssetTask extends AsyncTask<String, String, String>{
        private final String mStatus;
        private String errorMsg, responseStatus;

        private postStatAssetTask(String status) {
            mStatus = status;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_EDIT_STATUS_MOBIL;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseStatus = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Change Status Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    //keys.put("id_tenant", mTenant);
                    keys.put("id", String.valueOf(aId));
                    keys.put("status", mStatus);
                    Log.e(TAG, "Change Status Fetch KEYS : " + String.valueOf(keys));
                    return keys;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
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

            return responseStatus;
        }

        @Override
        protected void onPostExecute(String status) {
            mDetailAssetTask = null;
            showProgress(false);

            if(status != null){
                Toast.makeText(getApplicationContext(),"Data sukses diubah", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mDetailAssetTask = null;
            showProgress(false);
        }

    }

    private void deleteAssetItem(final int id) {
        showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Hapus aset ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                pDialog.setMessage("loading ...");
                showProgress(true);
                new deleteAssetTask(String.valueOf(id)).execute();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        alertDialog = showAlert.create();
        alertDialog.show();
    }

    private class deleteAssetTask  extends AsyncTask<String, String, String> {
        private final String mAsset;
        private String errorMsg, responseAsset;

        private deleteAssetTask(String asset) {
            mAsset = asset;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, AppConfig.URL_ADD_MOBIL, new Response.Listener<String>() {
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
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_item", mAsset);
                    Log.e(TAG, "Delete Data : " + String.valueOf(keys));
                    return keys;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
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

            return responseAsset;
        }

        @Override
        protected void onPostExecute(String User) {
            showProgress(false);

            if(User != null){
                Toast.makeText(getApplicationContext(),"Data berhasil dihapus", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal menghapus data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            showProgress(false);
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
        this.finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_delete_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            if(aCat.equals("1")){
                iAsetEdit = new Intent(DetailAsetActivity.this, FormCarAsetActivity.class);
                iAsetEdit.putExtra("action", "update");
                iAsetEdit.putExtra("id_asset", aId);
                iAsetEdit.putExtra("merk", aName);
                iAsetEdit.putExtra("type", aType);
                iAsetEdit.putExtra("year", aYear);
                iAsetEdit.putExtra("plat", aPlat);
                iAsetEdit.putExtra("cat", aCat);
                iAsetEdit.putExtra("subcat", aSubCat);
                startActivity(iAsetEdit);
            }else if(aCat.equals("2")){
                iAsetEdit = new Intent(DetailAsetActivity.this, FormMotorcycleAsetActivity.class);
                iAsetEdit.putExtra("action", "update");
                iAsetEdit.putExtra("id_asset", aId);
                iAsetEdit.putExtra("merk", aName);
                iAsetEdit.putExtra("type", aType);
                iAsetEdit.putExtra("year", aYear);
                iAsetEdit.putExtra("plat", aPlat);
                iAsetEdit.putExtra("cat", aCat);
                iAsetEdit.putExtra("subcat", aSubCat);
                startActivity(iAsetEdit);
            }
        }else if(id == R.id.action_delete){
            deleteAssetItem(aId);
        }

        return super.onOptionsItemSelected(item);
    }
}

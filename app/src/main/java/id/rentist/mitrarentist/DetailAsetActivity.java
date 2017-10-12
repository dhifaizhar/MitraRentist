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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.PriceAdapter;
import id.rentist.mitrarentist.modul.PriceModul;
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

    private List<PriceModul> mPrice = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    Integer aId, position;
    String changeStatus = "active", aName, aType, aSubType,  aStatus, aCat, aSubCat,
            aInsurance, aMinRentDay, aDeliveryMethod, category, category_url, aDesc, aMainImage;
    String aPlat, aYear, aNoStnk, aColor, aTransmission, aEngineCap, aFuel, aSeat, aAirBag, aAirCond, aDriver;

    TextView mark, status, subcat, insurance, min_rent_day, delivery_method, desc;
    TextView plat, year, no_stnk, color, transmission, engine_cap, fuel, seats, air_bag, air_cond, driver;

    LinearLayout rNoStnk, rColor, rTransmission, rFuel, rDriver, rEngineCap, rSeats, rAirBag, rAirCond ,rDesc;
    ImageView imgThumbnail;

    private static final String TAG = "DetailAssetActivity";
    private static final String TOKEN = "secretissecret";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);
        detIntent = getIntent();
        setTitle("Detail Aset");

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
        status = (TextView) findViewById(R.id.as_status_det);
        insurance = (TextView) findViewById(R.id.as_insurance);
        min_rent_day = (TextView) findViewById(R.id.as_min_rent_day);
        delivery_method = (TextView) findViewById(R.id.as_delivery_status);
        desc = (TextView) findViewById(R.id.as_desc);

        //Row Container
        rColor = (LinearLayout) findViewById(R.id.row_color);
        rAirBag = (LinearLayout) findViewById(R.id.row_air_bag);
        rAirCond = (LinearLayout) findViewById(R.id.row_air_cond);
        rDriver = (LinearLayout) findViewById(R.id.row_driver);
        rEngineCap = (LinearLayout) findViewById(R.id.row_engine_cap);
        rFuel = (LinearLayout) findViewById(R.id.row_fuel);
        rNoStnk = (LinearLayout) findViewById(R.id.row_no_stnk);
        rSeats = (LinearLayout) findViewById(R.id.row_seats);
        rTransmission = (LinearLayout) findViewById(R.id.row_transmission);
        rDesc= (LinearLayout) findViewById(R.id.row_desc);

        //Car & Motor
        plat = (TextView) findViewById(R.id.as_plat_det);
        year = (TextView) findViewById(R.id.as_year_det);
        no_stnk = (TextView) findViewById(R.id.as_no_stnk);
        color = (TextView) findViewById(R.id.as_color);
        transmission = (TextView) findViewById(R.id.as_transmission);
        engine_cap = (TextView) findViewById(R.id.as_engine_capacity);
        fuel = (TextView) findViewById(R.id.as_fuel);
        seats = (TextView) findViewById(R.id.as_seat);
        air_bag = (TextView) findViewById(R.id.as_air_bag);
        air_cond = (TextView) findViewById(R.id.as_air_conditioner);
        driver = (TextView) findViewById(R.id.as_driver);


        // set content control value
        aId = detIntent.getIntExtra("id_asset", 0);
        category = detIntent.getStringExtra("id_asset_category");
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
            String URL = "";
            switch (category) {
                case "1":
                    URL = AppConfig.URL_MOBIL;
                    break;
                case "2":
                    URL = AppConfig.URL_MOTOR;
                    break;
                case "3":
                    URL = AppConfig.URL_YACHT;
                    break;
                case "10":
                    URL = AppConfig.URL_BICYCLE;
                    break;
            }
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + id, new Response.Listener<String>() {
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
                            aCat = jsonobject.getString("id_asset_category");
                            aType = jsonobject.getString("type");
                            aStatus = jsonobject.getString("status");
                            aSubCat = jsonobject.getString("subcategory");
                            aInsurance = jsonobject.getString("insurance");
                            aMinRentDay = jsonobject.getString("min_rent_day");
                            aDeliveryMethod = jsonobject.getString("delivery_method");
                            aMainImage = jsonobject.getString("main_image");
                            if(aCat.equals("3")){ aName = jsonobject.getString("sub_type");}
                            else{aName = jsonobject.getString("brand");}

                            //Price
                            JSONArray priceArray = jsonobject.getJSONArray("price");
                            Log.e(TAG, "Price : " + priceArray);

                            if(priceArray.length() > 0){
                                for (int j = 0; j < priceArray.length(); j++) {
                                    JSONObject priceObject = priceArray.getJSONObject(j);

                                    PriceModul priceModul = new PriceModul();
                                    priceModul.setPrice(priceObject.getString("price") + " IDR");
                                    priceModul.setRangeName(priceObject.getString("range_name"));
                                    priceModul.setStartDate(priceObject.getString("start_date"));
                                    priceModul.setEndDate(priceObject.getString("end_date"));
                                    mPrice.add(priceModul);
                                }
                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.as_price_recyclerView);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mAdapter = new PriceAdapter(getApplicationContext(),mPrice);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                            String imageUrl = AppConfig.URL_IMAGE + aMainImage;
                            Picasso.with(getApplicationContext()).load(imageUrl).into(imgThumbnail);
                            subcat.setText(aSubCat);
                            status.setText(aStatus);
                            delivery_method.setText(aDeliveryMethod);

                            if (aInsurance.equals("true")){insurance.setText("Tersedia");
                            } else {insurance.setText("Tidak Tersedia");}

                            String minHari = aMinRentDay + " Hari";
                            min_rent_day.setText(minHari);

                            if ((aCat.equals("2")) || (aCat.equals("1"))) {
                                aPlat = jsonobject.getString("license_plat");
                                aYear = jsonobject.getString("year");
                                aNoStnk = jsonobject.getString("no_stnk");
                                aColor = jsonobject.getString("colour");
                                aTransmission = jsonobject.getString("transmission");
                                aEngineCap = jsonobject.getString("engine_capacity");
                                aFuel = jsonobject.getString("fuel");

                                rDesc.setVisibility(View.GONE);
                                plat.setVisibility(View.VISIBLE);
                                year.setVisibility(View.VISIBLE);
                                rNoStnk.setVisibility(View.VISIBLE);
                                rColor.setVisibility(View.VISIBLE);
                                rTransmission.setVisibility(View.VISIBLE);
                                rEngineCap.setVisibility(View.VISIBLE);
                                rFuel.setVisibility(View.VISIBLE);

                                mark.setText(aName + " " + aType);
                                plat.setText(aPlat);
                                year.setText(aYear);
                                no_stnk.setText(aNoStnk);
                                color.setText(aColor);
                                transmission.setText(aTransmission);
                                engine_cap.setText(aEngineCap);
                                fuel.setText(aFuel);

                                if (aCat.equals("1")){
                                    rSeats.setVisibility(View.VISIBLE);
                                    rAirCond.setVisibility(View.VISIBLE);
                                    rAirBag.setVisibility(View.VISIBLE);
                                    rDriver.setVisibility(View.VISIBLE);
                                    aAirBag = jsonobject.getString("air_bag");
                                    aAirCond = jsonobject.getString("air_conditioner");
                                    aDriver = jsonobject.getString("driver_included");
                                    aSeat = jsonobject.getString("seat");

                                    seats.setText(aSeat);
                                    if (aAirBag.equals("true")){air_bag.setText("Tersedia");
                                    } else {air_bag.setText("Tidak Tersedia");}

                                    if (aAirCond.equals("true")){air_cond.setText("Tersedia");
                                    } else {air_cond.setText("Tidak Tersedia");}

                                    if (aDriver.equals("true")){driver.setText("Tersedia");
                                    } else {driver.setText("Tidak Tersedia");}
                                } else {
                                    rSeats.setVisibility(View.GONE);
                                    rAirCond.setVisibility(View.GONE);
                                    rAirBag.setVisibility(View.GONE);
                                    rDriver.setVisibility(View.GONE);
                                }
                            } else if(aCat.equals("3")) {

                            }else {
                                aDesc = jsonobject.getString("description");

                                desc.setText(aDesc);
                                mark.setText(aName + " " + aType);

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
            switch (category) {
                case "1":
                    category_url = AppConfig.URL_DELETE_MOBIL;
                    break;
                case "2":
                    category_url = AppConfig.URL_MOTOR;
                    break;
                case "3":
                    category_url = AppConfig.URL_DELETE_YACHT;
                    break;
                case "10":
                    category_url = AppConfig.URL_DELETE_BICYCLE;
                    break;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, category_url, new Response.Listener<String>() {
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
                    keys.put("id_asset", mAsset);
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

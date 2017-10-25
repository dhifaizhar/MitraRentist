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
import id.rentist.mitrarentist.tools.CostumFormater;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;


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
    String changeStatus = "active",aAsetName, aName, aType, aSubType,  aStatus, aCat, aSubCat, aVerified,
            aInsurance, aMinRentDay, aDeliveryMethod, category, category_url, aDesc, aMainImage,
            //Car&Motor
            aPlat, aYear, aNoStnk , aColor, aTransmission, aEngineCap, aFuel, aSeat, aAirBag, aAirCond, aDriver,
            //Yacht
            aModel, aLength, aBeam, aGrossTon, aDraft, aCruisSpeed, aTopSpeed, aBuilder, aNaval, aIntDesign, aExtDesign,
            aGuest, aCabin, aCrew;

    TextView aset_name, mark, status, subcat, insurance, min_rent_day, delivery_method, desc,
            plat, year,  color, transmission, engine_cap, fuel, seats, air_bag, air_cond, driver,
            model, length, beam, gross_ton, draft, cruise_speed, top_speed, builder, naval, int_design, ext_design,
            guest, cabin, crew;

    String aPrice;// = new ArrayList<>();

    LinearLayout cCarMotor, cCarOnly, cYachtInfo, cYachtFeature, rDesc;
    ImageView imgThumbnail, no_stnk;

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
        category = detIntent.getStringExtra("id_asset_category");

        //initialize view
        imgThumbnail = (ImageView)findViewById(R.id.as_thumb_aset);
        aset_name = (TextView) findViewById(R.id.as_aset_name);
        mark = (TextView) findViewById(R.id.as_mark_det);
        subcat = (TextView) findViewById(R.id.as_subcat_det);
        status = (TextView) findViewById(R.id.as_status_det);
        insurance = (TextView) findViewById(R.id.as_insurance);
        min_rent_day = (TextView) findViewById(R.id.as_min_rent_day);
        delivery_method = (TextView) findViewById(R.id.as_delivery_status);
        desc = (TextView) findViewById(R.id.as_desc);

        //Row Container
        rDesc= (LinearLayout) findViewById(R.id.row_desc);
        cCarMotor = (LinearLayout) findViewById(R.id.con_car_motor);
        cCarOnly = (LinearLayout) findViewById(R.id.con_car_only);
        cYachtInfo = (LinearLayout) findViewById(R.id.con_yacht_info);
        cYachtFeature = (LinearLayout) findViewById(R.id.con_yacht_feature);

        //Car & Motor
        plat = (TextView) findViewById(R.id.as_plat_det);
        year = (TextView) findViewById(R.id.as_year_det);
        no_stnk = (ImageView) findViewById(R.id.as_no_stnk);
        color = (TextView) findViewById(R.id.as_color);
        transmission = (TextView) findViewById(R.id.as_transmission);
        engine_cap = (TextView) findViewById(R.id.as_engine_capacity);
        fuel = (TextView) findViewById(R.id.as_fuel);

        //Car Only
        seats = (TextView) findViewById(R.id.as_seat);
        air_bag = (TextView) findViewById(R.id.as_air_bag);
        air_cond = (TextView) findViewById(R.id.as_air_conditioner);
        driver = (TextView) findViewById(R.id.as_driver);

        //Tacht
        model = (TextView) findViewById(R.id.as_yacht_model);
        length = (TextView) findViewById(R.id.as_yacht_length);
        beam = (TextView) findViewById(R.id.as_yacht_beam);
        gross_ton = (TextView) findViewById(R.id.as_yacht_gross);
        draft = (TextView) findViewById(R.id.as_yacht_draft);
        cruise_speed = (TextView) findViewById(R.id.as_yacht_c_speed);
        top_speed = (TextView) findViewById(R.id.as_yacht_t_speed);
        builder = (TextView) findViewById(R.id.as_yacht_builder);
        naval = (TextView) findViewById(R.id.as_yacht_naval);
        int_design = (TextView) findViewById(R.id.as_yacht_int_design);
        ext_design = (TextView) findViewById(R.id.as_yacht_ext_design);
        guest = (TextView) findViewById(R.id.as_yacht_guest);
        crew = (TextView) findViewById(R.id.as_yacht_crew);
        cabin = (TextView) findViewById(R.id.as_yacht_cabin);

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
            String URL = "";
            switch (category) {
                case "1": URL = AppConfig.URL_MOBIL; break;
                case "2": URL = AppConfig.URL_MOTOR; break;
                case "3": URL = AppConfig.URL_YACHT; break;
                case "4": URL = AppConfig.URL_MEDIC; break;
                case "5": URL = AppConfig.URL_PHOTOGRAPHY; break;
                case "6": URL = AppConfig.URL_TOYS; break;
                case "7": URL = AppConfig.URL_ADVENTURE; break;
                case "8": URL = AppConfig.URL_MATERNITY; break;
                case "9": URL = AppConfig.URL_ELECTRONIC; break;
                case "10": URL = AppConfig.URL_BICYCLE; break;
                case "11": URL = AppConfig.URL_OFFICE; break;
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
        protected void onPostExecute(String aset){
            mDetailAssetTask = null;
//            pBar.setVisibility(View.GONE);
            showProgress(false);
            Integer dataLength, aId;

            if (aset != null) {
                try {
                    JSONArray jsonArray = new JSONArray(aset);
                    Log.e(TAG, "Asset Detail : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            aCat = jsonobject.getString("id_asset_category");
                            aAsetName = jsonobject.getString("name");
                            aType = jsonobject.getString("type");
                            aStatus = jsonobject.getString("status");
                            aVerified = jsonobject.getString("verified");
                            aSubCat = jsonobject.getString("subcategory");
                            aInsurance = jsonobject.getString("insurance");
                            aMinRentDay = jsonobject.getString("min_rent_day");
                            aDeliveryMethod = jsonobject.getString("delivery_method");
                            aMainImage = jsonobject.getString("main_image");
                            if(aCat.equals("3")){ aName = jsonobject.getString("sub_type");}
                            else{aName = jsonobject.getString("brand");}

                            //Price
                            JSONArray priceArray = jsonobject.getJSONArray("price");
                            aPrice = priceArray.toString();
                            Log.e(TAG, "Price : " + priceArray);

                            ArrayList<PriceModul> ePrice = new ArrayList<PriceModul>();

                            if(priceArray.length() > 0){
                                for (int j = 0; j < priceArray.length(); j++) {
                                    JSONObject priceObject = priceArray.getJSONObject(j);

                                    PriceModul priceModul = new PriceModul();
                                    priceModul.setRangeName(priceObject.getString("range_name"));
                                    priceModul.setStartDate(priceObject.getString("start_date"));
                                    priceModul.setEndDate(priceObject.getString("end_date"));
                                    priceModul.setPrice(CostumFormater.PriceStringFormat(priceObject.getString("price")));

                                    mPrice.add(priceModul);
                                }
                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.as_price_recyclerView);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mAdapter = new PriceAdapter(getApplicationContext(),mPrice);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                            String imageUrl = AppConfig.URL_IMAGE_ASSETS + aMainImage;
                            Picasso.with(getApplicationContext()).load(imageUrl).into(imgThumbnail);

                            if (aVerified.equals("true")){
                                ImageView verifIco = (ImageView) findViewById(R.id.as_verif);
                                verifIco.setVisibility(View.VISIBLE);
                            }
                            aset_name.setText(aAsetName);
                            subcat.setText(aSubCat);
                            status.setText(aStatus);
                            if (aDeliveryMethod.equals("both")){
                                delivery_method.setText("Dikirim, Diambil");
                            }else{
                                if (aDeliveryMethod.equals("pickup")){
                                    delivery_method.setText("Diambil");
                                } else {
                                    delivery_method.setText("Dikirim");
                                }}

                            if (aInsurance.equals("true")){insurance.setText("Tersedia");
                            } else {insurance.setText("Tidak Tersedia");}

                            String minHari = aMinRentDay + " Hari";
                            min_rent_day.setText(minHari);

                            switch (aCat) {
                                case "1":
                                    cCarOnly.setVisibility(View.VISIBLE);
                                    aAirBag = jsonobject.getString("air_bag");
                                    aAirCond = jsonobject.getString("air_conditioner");
                                    aDriver = jsonobject.getString("driver_included");
                                    aSeat = jsonobject.getString("seat");

                                    seats.setText(aSeat);
                                    air_bag.setText(aAirBag.equals("true")?"Tersedia":"Tidak Tersedia");
                                    air_cond.setText(aAirCond.equals("true")?"Tersedia":"Tidak Tersedia");

                                    if (aDriver.equals("both")){
                                        driver.setText("Tersedia, Tidak Tersedia");
                                    }else{
                                        driver.setText(aDriver.equals("true")?"Tersedia":"Tidak Tersedia");
                                    }
                                case "2":
                                    aPlat = jsonobject.getString("license_plat");
                                    aYear = jsonobject.getString("year");
                                    aNoStnk = jsonobject.getString("no_stnk");
                                    aColor = jsonobject.getString("colour");
                                    aTransmission = jsonobject.getString("transmission");
                                    aEngineCap = jsonobject.getString("engine_capacity");
                                    aFuel = jsonobject.getString("fuel");

                                    plat.setVisibility(View.VISIBLE);
                                    year.setVisibility(View.VISIBLE);
                                    rDesc.setVisibility(View.GONE);
                                    cCarMotor.setVisibility(View.VISIBLE);

                                    String stnkUrl = "http://assets.rentist.id/documents/" + aNoStnk;
                                    Picasso.with(getApplicationContext()).load(stnkUrl).into(no_stnk);

                                    mark.setText(aName + " " + aType);
                                    plat.setText(aPlat);
                                    year.setText(aYear);
                                    color.setText(aColor);
                                    transmission.setText(aTransmission);
                                    engine_cap.setText(aEngineCap + "cc");
                                    fuel.setText(aFuel);
                                    break;
                                case "3":
                                    cYachtInfo.setVisibility(View.VISIBLE);
                                    cYachtFeature.setVisibility(View.VISIBLE);
                                    desc.setVisibility(View.GONE);

                                    aSubType = jsonobject.getString("sub_type");
                                    aModel = jsonobject.getString("model");
                                    aLength = jsonobject.getString("length");
                                    aBeam = jsonobject.getString("beam");
                                    aGrossTon = jsonobject.getString("gross_tonnage");
                                    aDraft = jsonobject.getString("draft");
                                    aCruisSpeed = jsonobject.getString("cruising_speed");
                                    aTopSpeed = jsonobject.getString("top_speed");
                                    aBuilder = jsonobject.getString("builder");
                                    aNaval = jsonobject.getString("naval_architect");
                                    aIntDesign = jsonobject.getString("interior_designer");
                                    aExtDesign = jsonobject.getString("exterior_designer");
                                    aGuest = jsonobject.getString("guest");
                                    aCrew = jsonobject.getString("crew");
                                    aCabin = jsonobject.getString("cabin");

                                    model.setText(aModel);
                                    length.setText(aLength);
                                    beam.setText(aBeam);
                                    gross_ton.setText(aGrossTon);
                                    draft.setText(aDraft);
                                    cruise_speed.setText(aCruisSpeed);
                                    top_speed.setText(aTopSpeed);
                                    builder.setText(aBuilder);
                                    naval.setText(aNaval);
                                    int_design.setText(aIntDesign);
                                    ext_design.setText(aExtDesign);
                                    guest.setText(aGuest);
                                    crew.setText(aCrew);
                                    cabin.setText(aCabin);
                                    mark.setText(aType + " " + aSubType);
                                    break;
                                default:
                                    aDesc = jsonobject.getString("description");

                                    desc.setText(aDesc);
                                    mark.setText(aName + " " + aType);

                                    break;
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
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            Log.e(TAG, "Change Status URL: " + Tools.getIdCategoryURL(category));
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, Tools.getIdCategoryURL(category), new Response.Listener<String>() {
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
                    keys.put("id_asset", String.valueOf(aId));
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
                case "1": category_url = AppConfig.URL_DELETE_MOBIL; break;
                case "2": category_url = AppConfig.URL_DELETE_MOTOR; break;
                case "3": category_url = AppConfig.URL_DELETE_YACHT; break;
                case "4": category_url = AppConfig.URL_DELETE_MEDIC; break;
                case "5": category_url = AppConfig.URL_DELETE_PHOTOGRAPHY; break;
                case "6": category_url = AppConfig.URL_DELETE_TOYS; break;
                case "7": category_url = AppConfig.URL_DELETE_ADVENTURE; break;
                case "8": category_url = AppConfig.URL_DELETE_MATERNITY; break;
                case "9": category_url = AppConfig.URL_DELETE_ELECTRONIC; break;
                case "10": category_url = AppConfig.URL_DELETE_BICYCLE; break;
                case "11": category_url = AppConfig.URL_DELETE_OFFICE; break;
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
            switch (aCat) {
                case "1":
                    iAsetEdit = new Intent(DetailAsetActivity.this, FormCarAsetActivity.class);
                    iAsetEdit.putExtra("action", "update");
                    iAsetEdit.putExtra("id_asset", aId);
                    iAsetEdit.putExtra("name", aAsetName);
                    iAsetEdit.putExtra("merk", aName);
                    iAsetEdit.putExtra("type", aType);
                    iAsetEdit.putExtra("year", aYear);
                    iAsetEdit.putExtra("plat", aPlat);
                    iAsetEdit.putExtra("stnk", aNoStnk);
                    iAsetEdit.putExtra("color", aColor);
                    iAsetEdit.putExtra("no_stnk", aNoStnk);
                    iAsetEdit.putExtra("engine_cap", aEngineCap);
                    iAsetEdit.putExtra("fuel", aFuel);
                    iAsetEdit.putExtra("seat", aSeat);
                    iAsetEdit.putExtra("air_bag", aAirBag);
                    iAsetEdit.putExtra("air_cond", aAirCond);
                    iAsetEdit.putExtra("driver", aDriver);
                    iAsetEdit.putExtra("assurance", aInsurance);
                    iAsetEdit.putExtra("transmission", aTransmission);
                    iAsetEdit.putExtra("min_rent_day", aMinRentDay);
                    iAsetEdit.putExtra("main_image", aMainImage);
                    iAsetEdit.putExtra("delivery_method", aDeliveryMethod);
                    iAsetEdit.putExtra("cat", aCat);
                    iAsetEdit.putExtra("subcat", aSubCat);
                    iAsetEdit.putExtra("price", aPrice );
                    startActivity(iAsetEdit);
                    break;
                case "2":
                    iAsetEdit = new Intent(DetailAsetActivity.this, FormMotorcycleAsetActivity.class);
                    iAsetEdit.putExtra("action", "update");
                    iAsetEdit.putExtra("id_asset", aId);
                    iAsetEdit.putExtra("name", aAsetName);
                    iAsetEdit.putExtra("merk", aName);
                    iAsetEdit.putExtra("type", aType);
                    iAsetEdit.putExtra("year", aYear);
                    iAsetEdit.putExtra("plat", aPlat);
                    iAsetEdit.putExtra("stnk", aNoStnk);
                    iAsetEdit.putExtra("color", aColor);
                    iAsetEdit.putExtra("no_stnk", aNoStnk);
                    iAsetEdit.putExtra("engine_cap", aEngineCap);
                    iAsetEdit.putExtra("fuel", aFuel);
                    iAsetEdit.putExtra("assurance", aInsurance);
                    iAsetEdit.putExtra("transmission", aTransmission);
                    iAsetEdit.putExtra("min_rent_day", aMinRentDay);
                    iAsetEdit.putExtra("main_image", aMainImage);
                    iAsetEdit.putExtra("delivery_method", aDeliveryMethod);
                    iAsetEdit.putExtra("cat", aCat);
                    iAsetEdit.putExtra("subcat", aSubCat);
                    startActivity(iAsetEdit);
                    break;
                case "3":
                    iAsetEdit = new Intent(DetailAsetActivity.this, FormYachtAsetActivity.class);
                    iAsetEdit.putExtra("action", "update");
                    iAsetEdit.putExtra("id_asset", aId);
                    iAsetEdit.putExtra("subtype", aSubType);
                    iAsetEdit.putExtra("name", aAsetName);
                    iAsetEdit.putExtra("type", aType);
                    iAsetEdit.putExtra("model", aModel);
                    iAsetEdit.putExtra("length", aLength);
                    iAsetEdit.putExtra("beam", aBeam);
                    iAsetEdit.putExtra("gross_tone", aGrossTon);
                    iAsetEdit.putExtra("draft", aDraft);
                    iAsetEdit.putExtra("cruising_speed", aCruisSpeed);
                    iAsetEdit.putExtra("top_speed", aTopSpeed);
                    iAsetEdit.putExtra("builder", aBuilder);
                    iAsetEdit.putExtra("naval_architect", aNaval);
                    iAsetEdit.putExtra("interior_designer", aIntDesign);
                    iAsetEdit.putExtra("exterior_designer", aExtDesign);
                    iAsetEdit.putExtra("guest", aGuest);
                    iAsetEdit.putExtra("crew", aCrew);
                    iAsetEdit.putExtra("cabin", aCabin);
                    iAsetEdit.putExtra("delivery_method", aDeliveryMethod);
                    iAsetEdit.putExtra("min_rent_day", aMinRentDay);
                    iAsetEdit.putExtra("main_image", aMainImage);
                    iAsetEdit.putExtra("assurance", aInsurance);
                    iAsetEdit.putExtra("cat", aCat);
                    iAsetEdit.putExtra("subcat", aSubCat);
                    startActivity(iAsetEdit);

                    break;
                default:
                    switch (aCat) {
                        case "4": iAsetEdit = new Intent(DetailAsetActivity.this, FormMedicAsetActivity.class); break;
                        case "5": iAsetEdit = new Intent(DetailAsetActivity.this, FormPhotographyAsetActivity.class); break;
                        case "6": iAsetEdit = new Intent(DetailAsetActivity.this, FormToysAsetActivity.class); break;
                        case "7": iAsetEdit = new Intent(DetailAsetActivity.this, FormAdventureAsetActivity.class); break;
                        case "8": iAsetEdit = new Intent(DetailAsetActivity.this, FormMaternityAsetActivity.class); break;
                        case "9": iAsetEdit = new Intent(DetailAsetActivity.this, FormElectronicAsetActivity.class); break;
                        case "10": iAsetEdit = new Intent(DetailAsetActivity.this, FormBicycleAsetActivity.class); break;
                        case "11": iAsetEdit = new Intent(DetailAsetActivity.this, FormOfficeAsetActivity.class); break;
                        case "12": iAsetEdit = new Intent(DetailAsetActivity.this, FormFashionAsetActivity.class); break;
                    }

                    iAsetEdit.putExtra("action", "update");
                    iAsetEdit.putExtra("id_asset", aId);
                    iAsetEdit.putExtra("min_rent_day", aMinRentDay);
                    iAsetEdit.putExtra("main_image", aMainImage);
                    iAsetEdit.putExtra("name", aAsetName);
                    iAsetEdit.putExtra("merk", aName);
                    iAsetEdit.putExtra("type", aType);
                    iAsetEdit.putExtra("assurance", aInsurance);
                    iAsetEdit.putExtra("description", aDesc);
                    iAsetEdit.putExtra("delivery_method", aDeliveryMethod);
                    iAsetEdit.putExtra("cat", aCat);
                    iAsetEdit.putExtra("subcat", aSubCat);
                    iAsetEdit.putExtra("price", aPrice );
                    startActivityForResult(iAsetEdit, 2);
                    break;
            }
        }else if(id == R.id.action_delete){
            deleteAssetItem(aId);
        }

        return super.onOptionsItemSelected(item);
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode == RESULT_OK) {
//            ProfileActivity.this.finish();
//            Intent ii = new Intent(ProfileActivity.this,ProfileActivity.class);
//            startActivity(ii);
//
//        }
//
//    }
}

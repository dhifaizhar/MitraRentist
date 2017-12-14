package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.PriceAdapter;
import id.rentist.mitrarentist.decorator.EventDecorator;
import id.rentist.mitrarentist.decorator.HighlightWeekendsDecorator;
import id.rentist.mitrarentist.decorator.MySelectorDecorator;
import id.rentist.mitrarentist.decorator.OneDayDecorator;
import id.rentist.mitrarentist.modul.PriceModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.PricingTools;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;


public class DetailAsetActivity extends AppCompatActivity implements OnDateSelectedListener {
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private AsyncTask mDetailAssetTask = null, mWorkTask = null;
    private Toolbar toolbar;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private AlertDialog.Builder showAlert, scheduleAlert;
    private AlertDialog alertDialog,scheduleAlertDialog;
    private Intent detIntent;
    MaterialCalendarView calendarView;
    Intent iAsetEdit;

    List<CalendarDay> dayTrans, daySchedule;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private List<PriceModul> mPrice = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    Integer aId, position;
    String changeStatus = "active",aAsetName, aName, aType, aSubType,  aStatus, aCat, aSubCat, aVerified, aLongitude, aLatitude,
            aInsurance, aMinRentDay, aDeliveryMethod, category, category_url, aDesc, aWeight,
            aAssetValue, aMainImage, aSecondImage, aThirdImage, aFourthImage, aFifthImage,
            aDeposit, aDepositValue, aIdDelivery,
            //Car&Motor
            aPlat, aYear, aNoStnk , aColor, aTransmission, aEngineCap, aFuel, aSeat, aAirBag, aAirCond,
                    aDriver, aEstPrice,
            //Yacht
            aModel, aLength, aBeam, aGrossTon, aDraft, aCruisSpeed, aTopSpeed, aBuilder, aNaval, aIntDesign, aExtDesign,
            aGuest, aCabin, aCrew;

    TextView aset_name, mark, status, subcat, insurance, min_rent_day, delivery_method, desc, address, member_badge,
            plat, year,  color, transmission, engine_cap, fuel, seats, air_bag, air_cond, driver,
            model, length, beam, gross_ton, draft, cruise_speed, top_speed, builder, naval, int_design, ext_design,
            guest, cabin, crew, asset_value, weight, dimension, no_rangka, no_mesin, est_value,
            delivery_distance, delivery_price, deposit, deposit_value;

    String aPrice;// = new ArrayList<>();

    LinearLayout cCarMotor, cCarOnly, cYachtInfo, cYachtFeature, cSmallAssetFeature, rDesc, rEstValue,
        rDeliveryPrice, rDeposit;
    ImageView imgThumbnail, no_stnk;
    CarouselView aAssetImages;
    Switch swStatus;
    String[] imagesArray = {AppConfig.URL_IMAGE_ASSETS + "default.png"};

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

        controlContent();

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setArrowColor(0xFF00AEEE);
        calendarView.setSelectionColor(0xFF00AEEE);
        calendarView.setOnDateChangedListener(this);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        Calendar instance = Calendar.getInstance();
        calendarView.setSelectedDate(instance.getTime());

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR), Calendar.JANUARY, 1);

        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance2.get(Calendar.YEAR) + 1, Calendar.DECEMBER, 31);

        calendarView.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();

        calendarView.addDecorators(
                new MySelectorDecorator(this),
                new HighlightWeekendsDecorator(),
                oneDayDecorator
        );

        mWorkTask =  new getDateEvent().execute();
//        getDateEventAssset();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String dateSelected = date.getDay() + "-" +  (date.getMonth() + 1) + "-" + date.getYear();
                final String dateSend = date.getYear() +"-"+ (date.getMonth() + 1) +"-"+ date.getDay();

                scheduleAlert = new AlertDialog.Builder(DetailAsetActivity.this);
                scheduleAlert.setMessage("Ubah status aset pada  " +  dateSelected + " menjadi inaktif ?");
                scheduleAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                         setDateEvent(dateSend);

                    }
                });
                scheduleAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                    }
                });

                scheduleAlertDialog = scheduleAlert.create();
                scheduleAlertDialog.show();
            }
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        oneDayDecorator.setDate(date.getDate());
        widget.invalidateDecorators();
    }

    private void controlContent() {
        category = detIntent.getStringExtra("id_asset_category");

        //initialize view
        aAssetImages = (CarouselView) findViewById(R.id.carouselView);

        swStatus = (Switch) findViewById(R.id.sw_status);
        imgThumbnail = (ImageView)findViewById(R.id.as_thumb_aset);
        aset_name = (TextView) findViewById(R.id.as_aset_name);
        mark = (TextView) findViewById(R.id.as_mark_det);
        subcat = (TextView) findViewById(R.id.as_subcat_det);
        status = (TextView) findViewById(R.id.as_status_det);
        address = (TextView) findViewById(R.id.as_address);
        insurance = (TextView) findViewById(R.id.as_insurance);
        min_rent_day = (TextView) findViewById(R.id.as_min_rent_day);
        delivery_method = (TextView) findViewById(R.id.as_delivery_status);
        desc = (TextView) findViewById(R.id.as_desc);
        member_badge = (TextView) findViewById(R.id.as_member_badge);
        asset_value = (TextView) findViewById(R.id.as_asset_value);
        weight = (TextView) findViewById(R.id.as_weight);
        dimension = (TextView) findViewById(R.id.as_dimension);

        delivery_distance = (TextView) findViewById(R.id.as_delivery_distance);
        delivery_price = (TextView) findViewById(R.id.as_delivery_price);
        deposit = (TextView) findViewById(R.id.as_deposit);
        deposit_value = (TextView) findViewById(R.id.as_nominal_deposit);

        //Row Container
        rDesc= (LinearLayout) findViewById(R.id.row_desc);
        cCarMotor = (LinearLayout) findViewById(R.id.con_car_motor);
        cCarOnly = (LinearLayout) findViewById(R.id.con_car_only);
        cYachtInfo = (LinearLayout) findViewById(R.id.con_yacht_info);
        cYachtFeature = (LinearLayout) findViewById(R.id.con_yacht_feature);
        cSmallAssetFeature = (LinearLayout) findViewById(R.id.con_feature_small_aset);
        rEstValue = (LinearLayout) findViewById(R.id.row_est_value);
        rDeliveryPrice = (LinearLayout) findViewById(R.id.row_delivery_price);
        rDeposit = (LinearLayout) findViewById(R.id.row_deposit);

        //Car & Motor
        plat = (TextView) findViewById(R.id.as_plat_det);
        year = (TextView) findViewById(R.id.as_year_det);
        no_stnk = (ImageView) findViewById(R.id.as_no_stnk);
        color = (TextView) findViewById(R.id.as_color);
        transmission = (TextView) findViewById(R.id.as_transmission);
        engine_cap = (TextView) findViewById(R.id.as_engine_capacity);
        fuel = (TextView) findViewById(R.id.as_fuel);
        no_rangka = (TextView) findViewById(R.id.as_no_rangka);
        no_mesin = (TextView) findViewById(R.id.as_no_mesin);

        //Car Only
        seats = (TextView) findViewById(R.id.as_seat);
        air_bag = (TextView) findViewById(R.id.as_air_bag);
        air_cond = (TextView) findViewById(R.id.as_air_conditioner);
        driver = (TextView) findViewById(R.id.as_driver);
        est_value = (TextView) findViewById(R.id.as_est_value);

        //Yacht
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
        getAssetDetail();

        aAssetImages.setPageCount(imagesArray.length);
        aAssetImages.setImageListener(imageUrlListener);

        swStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatAset();
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatAset();
            }
        });

    }

    private void getAssetDetail() {
        pDialog.setMessage("loading data...");
        showProgress(true);

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
            case "12": URL = AppConfig.URL_FASHION; break;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + aId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseParse(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.e(TAG, "Get Aset List Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void responseParse(String response) {
        mDetailAssetTask = null;
        showProgress(false);
        Integer dataLength, aId;
        String errorMsg;

        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.e(TAG, "Asset Detail : " + jsonArray);
                dataLength = jsonArray.length();
                if (dataLength > 0) {
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
                        aLatitude = jsonobject.getString("latitude");
                        aLongitude = jsonobject.getString("longitude");
                        aMainImage = jsonobject.getString("main_image");
                        aSecondImage = jsonobject.getString("second_image");
                        aThirdImage = jsonobject.getString("third_image");
                        aFourthImage = jsonobject.getString("fourth_image");
                        aFifthImage = jsonobject.getString("fifth_image");

                        if (aCat.equals("3")) {
                            aName = jsonobject.getString("sub_type");
                        } else {
                            aName = jsonobject.getString("brand");
                        }

                        //Price
                        JSONArray priceArray = jsonobject.getJSONArray("price");
                        aPrice = priceArray.toString();
                        Log.e(TAG, "Price : " + priceArray);

                        if (priceArray.length() > 0) {
                            mPrice.clear();
                            for (int j = 0; j < priceArray.length(); j++) {
                                JSONObject priceObject = priceArray.getJSONObject(j);

                                PriceModul priceModul = new PriceModul();
                                priceModul.setRangeName(priceObject.getString("range_name"));
                                priceModul.setStartDate(priceObject.getString("start_date"));
                                priceModul.setEndDate(priceObject.getString("end_date"));
                                priceModul.setPrice(PricingTools.PriceStringFormat(priceObject.getString("price")));

                                mPrice.add(priceModul);
                            }
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.as_price_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new PriceAdapter(getApplicationContext(), mPrice);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                        // Images Banner
                        JSONArray images = jsonobject.getJSONArray("images");
                        imagesArray = new String[images.length()];
                        for (int k = 0; k < images.length(); k++) {
                            imagesArray[k] = AppConfig.URL_IMAGE_ASSETS + images.getString(k);
                        }
                        aAssetImages.setPageCount(images.length());
                        aAssetImages.setImageListener(imageUrlListener);

                        if (aVerified.equals("true")) {
                            ImageView verifIco = (ImageView) findViewById(R.id.as_verif);
                            verifIco.setVisibility(View.VISIBLE);
                        }

                        aset_name.setText(aAsetName);
                        subcat.setText(aSubCat);

                        if (aStatus.equals("inactive")){
                            status.setText("Non-Aktif");
                            status.setTextColor(getResources().getColor(R.color.colorInactive));
                            swStatus.setChecked(false);
                        }else{
                            status.setText("Aktif");
                            status.setTextColor(getResources().getColor(R.color.colorPrimary));
                            swStatus.setChecked(true);
                        }

                        if (aDeliveryMethod.equals("both")) {
                            delivery_method.setText("Dikirim, Diambil");
                        } else {
                            if (aDeliveryMethod.equals("pickup"))
                                delivery_method.setText("Diambil");
                            else {
                                delivery_method.setText("Dikirim");

                                if(jsonobject.has("id_delivery")){
                                    rDeliveryPrice.setVisibility(View.VISIBLE);

                                    JSONObject deliveryObject = jsonobject.getJSONObject("id_delivery");
                                    aIdDelivery = deliveryObject.getString("id");

                                    delivery_distance.setText(deliveryObject.getInt("max_distance") + " KM");
                                    delivery_price.setText(PricingTools.PriceStringFormat(deliveryObject.getString("price_per_km")));
                                }
                            }
                        }

                        if(jsonobject.has("deposit")){
                            aDeposit = jsonobject.getString("deposit");
                            aDepositValue = jsonobject.getString("nominal_deposit");
                            deposit.setText(aDeposit.equals("true") ? "Ya" : "Tidak");

                            if(aDeposit.equals("true")){
                                rDeposit.setVisibility(View.VISIBLE);
                                deposit_value.setText(PricingTools.PriceStringFormat(aDepositValue));
                            }
                        }


                        if (aInsurance.equals("true")) insurance.setText("Tersedia");
                        else insurance.setText("Tidak Tersedia");

                        address.setText(jsonobject.getString("address"));

                        member_badge.setText(jsonobject.getString("member_badge"));
                        if (member_badge.getText().equals(getResources().getString(R.string.member_badge_verified))) {
                            member_badge.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        } else if (member_badge.getText().equals(getResources().getString(R.string.member_badge_smart_con))) {
                            member_badge.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                        }

                        String minHari = aMinRentDay + " Hari";
                        min_rent_day.setText(minHari);

                        switch (aCat) {
                            case "1":
                                cCarOnly.setVisibility(View.VISIBLE);
                                rEstValue.setVisibility(View.VISIBLE);
                                aAirBag = jsonobject.getString("air_bag");
                                aAirCond = jsonobject.getString("air_conditioner");
                                aDriver = jsonobject.getString("driver_included");
                                aSeat = jsonobject.getString("seat");
                                aEstPrice = jsonobject.getString("estimated_price");

                                seats.setText(aSeat);
                                air_bag.setText(aAirBag.equals("true") ? "Tersedia" : "Tidak Tersedia");
                                air_cond.setText(aAirCond.equals("true") ? "Tersedia" : "Tidak Tersedia");
                                est_value.setText("Kurang dari " + aEstPrice);

                                if (aDriver.equals("both")) {
                                    driver.setText("Tersedia, Tidak Tersedia");
                                } else {
                                    driver.setText(aDriver.equals("true") ? "Tersedia" : "Tidak Tersedia");
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

                                String stnkUrl = AppConfig.URL_IMAGE_DOCUMENTS + aNoStnk;
                                Picasso.with(getApplicationContext()).load(stnkUrl).into(no_stnk);

                                mark.setText(aName + " " + aType);
                                plat.setText(aPlat);
                                year.setText(aYear);
                                color.setText(aColor);
                                transmission.setText(aTransmission);
                                engine_cap.setText(aEngineCap + "cc");
                                fuel.setText(aFuel);
                                no_rangka.setText(jsonobject.getString("no_rangka"));
                                no_mesin.setText(jsonobject.getString("no_mesin"));

                                break;
                            case "3":
                                cYachtInfo.setVisibility(View.VISIBLE);
                                cYachtFeature.setVisibility(View.VISIBLE);
                                rDesc.setVisibility(View.GONE);

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
                                if(!aCat.equals("10")){
                                    cSmallAssetFeature.setVisibility(View.VISIBLE);
                                    aAssetValue = jsonobject.getString("asset_value").equals("null") ? "0" : jsonobject.getString("asset_value");
                                    aWeight = jsonobject.getString("weight").equals("null") ? "0" : jsonobject.getString("weight");

                                    asset_value.setText(PricingTools.PriceStringFormat(aAssetValue));
                                    weight.setText(aWeight + " Kg");
                                    dimension.setText(jsonobject.getString("dimension").equals("null") ? "-" : jsonobject.getString("dimension") + " m");
                                }
                                aDesc = jsonobject.getString("description");

                                desc.setText(aDesc);
                                mark.setText(aName + " " + aType);

                                break;
                        }

                    }

                } else {
                    errorMsg = "Gagal Memuat Data";
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errorMsg = "Gagal Memuat Data";
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    ImageListener imageUrlListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.with(getApplicationContext()).load(imagesArray[position]).into(imageView);
        }
    };

    private void changeStatAset() {
        final String nowStatus = aStatus;

        showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Ubah status aset ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(nowStatus.equals("active")){
                    changeStatus = "inactive";
                }else{
                    changeStatus = "active";
                }
                pDialog.setMessage("loading ...");
                showProgress(true);
                new postStatAssetTask(changeStatus).execute();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(nowStatus.equals("active")){
                    swStatus.setChecked(true);
                }else{
                    swStatus.setChecked(false);
                }
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
                    keys.put("price", aPrice);
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
            getAssetDetail();

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
                case "12": category_url = AppConfig.URL_DELETE_FASHION; break;
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
                    iAsetEdit.putExtra("delivery_method", aDeliveryMethod);
                    iAsetEdit.putExtra("cat", aCat);
                    iAsetEdit.putExtra("subcat", aSubCat);
                    iAsetEdit.putExtra("price", aPrice);
                    iAsetEdit.putExtra("address", address.getText().toString());
                    iAsetEdit.putExtra("member_badge", member_badge.getText().toString());
                    iAsetEdit.putExtra("longitude", aLongitude);
                    iAsetEdit.putExtra("latitude", aLatitude);
                    iAsetEdit.putExtra("images", imagesArray);
                    iAsetEdit.putExtra("main_image", aMainImage);
                    iAsetEdit.putExtra("second_image", aSecondImage);
                    iAsetEdit.putExtra("third_image", aThirdImage);
                    iAsetEdit.putExtra("fourth_image", aFourthImage);
                    iAsetEdit.putExtra("fifth_image", aFifthImage);
                    iAsetEdit.putExtra("no_rangka", no_rangka.getText().toString());
                    iAsetEdit.putExtra("no_mesin", no_mesin.getText().toString());
                    iAsetEdit.putExtra("est_price", aEstPrice);

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
                    iAsetEdit.putExtra("price", aPrice);
                    iAsetEdit.putExtra("address", address.getText().toString());
                    iAsetEdit.putExtra("member_badge", member_badge.getText().toString());
                    iAsetEdit.putExtra("longitude", aLongitude);
                    iAsetEdit.putExtra("latitude", aLatitude);
                    iAsetEdit.putExtra("images", imagesArray);
                    iAsetEdit.putExtra("second_image", aSecondImage);
                    iAsetEdit.putExtra("third_image", aThirdImage);
                    iAsetEdit.putExtra("fourth_image", aFourthImage);
                    iAsetEdit.putExtra("fifth_image", aFifthImage);
                    iAsetEdit.putExtra("no_rangka", no_rangka.getText().toString());
                    iAsetEdit.putExtra("no_mesin", no_mesin.getText().toString());
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
                    iAsetEdit.putExtra("price", aPrice);
                    iAsetEdit.putExtra("address", address.getText().toString());
                    iAsetEdit.putExtra("member_badge", member_badge.getText().toString());
                    iAsetEdit.putExtra("longitude", aLongitude);
                    iAsetEdit.putExtra("latitude", aLatitude);
                    iAsetEdit.putExtra("images", imagesArray);
                    iAsetEdit.putExtra("second_image", aSecondImage);
                    iAsetEdit.putExtra("third_image", aThirdImage);
                    iAsetEdit.putExtra("fourth_image", aFourthImage);
                    iAsetEdit.putExtra("fifth_image", aFifthImage);
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
                    iAsetEdit.putExtra("cat", aCat);
                    iAsetEdit.putExtra("subcat", aSubCat);
                    iAsetEdit.putExtra("price", aPrice);
                    iAsetEdit.putExtra("address", address.getText().toString());
                    iAsetEdit.putExtra("member_badge", member_badge.getText().toString());
                    iAsetEdit.putExtra("images", imagesArray);
                    iAsetEdit.putExtra("longitude", aLongitude);
                    iAsetEdit.putExtra("latitude", aLatitude);
                    iAsetEdit.putExtra("second_image", aSecondImage);
                    iAsetEdit.putExtra("third_image", aThirdImage);
                    iAsetEdit.putExtra("fourth_image", aFourthImage);
                    iAsetEdit.putExtra("fifth_image", aFifthImage);
                    if(!aCat.equals("10")){
                        iAsetEdit.putExtra("asset_value", aAssetValue.toString());
                        iAsetEdit.putExtra("weight", aWeight);
                        iAsetEdit.putExtra("dimension", dimension.getText().toString());
                    }
                    iAsetEdit.putExtra("delivery_method", aDeliveryMethod);
                    if(aDeliveryMethod.equals("deliver")){
                        iAsetEdit.putExtra("id_delivery", aIdDelivery);
                        iAsetEdit.putExtra("delivery_detail", "Jarak Maksimal: " + delivery_distance.getText().toString() +
                            ", Biaya per KM: " + delivery_price.getText().toString());
                    }
                    iAsetEdit.putExtra("deposit", aDeposit);
                    if (aDeposit.equals("true")) iAsetEdit.putExtra("nominal_deposit", aDepositValue);

                    startActivityForResult(iAsetEdit, 2);
                    break;
            }
        }else if(id == R.id.action_delete){
            deleteAssetItem(aId);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getAssetDetail();
    }

    public void setDateEvent(String date) {
        new setDateEventTask(date).execute();
    }

    private class setDateEventTask extends AsyncTask<String, String, String> {
        private final String mdate;
        private String errorMsg, responseEvent;

        private setDateEventTask(String date) {mdate = date;}

        @Override
        protected String doInBackground(String... voids) {
            Log.e(TAG, String.valueOf(sm.getIntPreferences("id_tenant"))
                    + " | " + String.valueOf(aId)
                    + " | " + aCat);

            String URL = AppConfig.URL_SET_EVENT;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Berhasil diubah",
                            Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Event Fetch Error : " + error.toString());
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", String.valueOf(sm.getIntPreferences("id_tenant")));
                    keys.put("id_asset", String.valueOf(aId));
                    keys.put("id_asset_category", detIntent.getStringExtra("id_asset_category"));
                    keys.put("start_date", mdate);
                    keys.put("end_date", mdate);
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
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return responseEvent;
        }

        @Override
        protected void onPostExecute(String event) {
            mWorkTask = null;
            new getDateEvent().execute();

        }
    }

    private class getDateEvent extends AsyncTask<String, String, String> {

        private String errorMsg, responseEvent="";

        private getDateEvent() {}

        @Override
        protected String doInBackground(String... voids) {
            Log.e(TAG, String.valueOf(sm.getIntPreferences("id_tenant"))
                    + " | " + String.valueOf(aId)
                    + " | " + aCat);

            String URL = AppConfig.URL_VIEW_EVENT;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseEvent = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Event Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", String.valueOf(sm.getIntPreferences("id_tenant")));
                    keys.put("id_asset", String.valueOf(aId));
                    keys.put("id_asset_category", detIntent.getStringExtra("id_asset_category"));
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e(TAG, responseEvent);
            return responseEvent;
        }

        @Override
        protected void onPostExecute(String event) {
            if (event != null) {
                try {
                    JSONObject eventObject = new JSONObject(event);
                    JSONArray eventArray = new JSONArray(eventObject.getString("transaction"));
                    JSONArray scheduleArray = new JSONArray(eventObject.getString("schedules"));

                    ArrayList<CalendarDay> dateTrans = new ArrayList<>();
                    ArrayList<CalendarDay> dateSchedule = new ArrayList<>();

                    if (eventArray.length() > 0){
                        for (int i = 0; i < eventArray.length(); i++) {
                            JSONObject arrayObject = eventArray.getJSONObject(i);

                            try{
                                Date startDate = format.parse(arrayObject.getString("start_date"));
                                Date endDate = format.parse(arrayObject.getString("end_date"));

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(startDate);

                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(endDate);

                                CalendarDay dateDay;
                                while(!cal.after(cal2))
                                {
                                    dateDay = CalendarDay.from(cal.getTime());
                                    dateTrans.add(dateDay);
                                    cal.add(Calendar.DATE, 1);
                                    Log.e(TAG, "date : " + dateDay);
                                }


                            }catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (scheduleArray.length() > 0) {
                        for (int i = 0; i < scheduleArray.length(); i++) {
                            JSONObject scheduleObject = scheduleArray.getJSONObject(i);

                            try {
                                Date schedule = format.parse(scheduleObject.getString("start_date"));
                                CalendarDay daySchedule = CalendarDay.from(schedule);
                                dateSchedule.add(daySchedule);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

//                    dayEvent = dates;
                    daySchedule = dateSchedule;
                    dayTrans = dateTrans;
                    calendarView.addDecorator(new EventDecorator(Color.GREEN, getResources().getDrawable(R.drawable.schedule_circle_bacground), dateSchedule));
                    calendarView.addDecorator(new EventDecorator(Color.RED, getResources().getDrawable(R.drawable.trans_cirle_background), dateTrans));

//                    Log.e(TAG, "Event : " + dayEvent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Tidak ada event.",
                        Toast.LENGTH_LONG).show();
            }

            mWorkTask = null;
        }
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
                case "12": URL = AppConfig.URL_FASHION; break;
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
                            aLatitude = jsonobject.getString("latitude");
                            aLongitude = jsonobject.getString("longitude");
                            aMainImage = jsonobject.getString("main_image");

                            if(aCat.equals("3")){ aName = jsonobject.getString("sub_type");}
                            else{aName = jsonobject.getString("brand");}

                            //Price
                            JSONArray priceArray = jsonobject.getJSONArray("price");
                            aPrice = priceArray.toString();
                            Log.e(TAG, "Price : " + priceArray);

                            ArrayList<PriceModul> ePrice = new ArrayList<PriceModul>();

                            if(priceArray.length() > 0){
                                mPrice.clear();
                                for (int j = 0; j < priceArray.length(); j++) {
                                    JSONObject priceObject = priceArray.getJSONObject(j);

                                    PriceModul priceModul = new PriceModul();
                                    priceModul.setRangeName(priceObject.getString("range_name"));
                                    priceModul.setStartDate(priceObject.getString("start_date"));
                                    priceModul.setEndDate(priceObject.getString("end_date"));
                                    priceModul.setPrice(PricingTools.PriceStringFormat(priceObject.getString("price")));

                                    mPrice.add(priceModul);
                                }
                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.as_price_recyclerView);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mAdapter = new PriceAdapter(getApplicationContext(),mPrice);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                            JSONArray images = jsonobject.getJSONArray("images");
                            imagesArray = new String[images.length()];
                            for (int k = 0; k < images.length(); k++) {
                                imagesArray[k] = AppConfig.URL_IMAGE_ASSETS + images.getString(k);
                            }

                            aAssetImages.setPageCount(images.length());
                            aAssetImages.setImageListener(imageUrlListener);

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
                                if (aDeliveryMethod.equals("pickup")) delivery_method.setText("Diambil");
                                else delivery_method.setText("Dikirim");
                            }

                            if (aInsurance.equals("true"))insurance.setText("Tersedia");
                            else insurance.setText("Tidak Tersedia");

                            address.setText(jsonobject.getString("address"));

                            member_badge.setText(jsonobject.getString("member_badge"));
                            if(member_badge.getText().equals(getResources().getString(R.string.member_badge_verified))){
                                member_badge.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            } else if (member_badge.getText().equals(getResources().getString(R.string.member_badge_smart_con))){
                                member_badge.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                            }

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
}

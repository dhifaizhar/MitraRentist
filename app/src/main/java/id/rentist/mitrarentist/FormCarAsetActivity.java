package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormCarAsetActivity extends AppCompatActivity {
    private AsyncTask mAddAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Bitmap bitmap;
    Intent iFormAsset;

    ImageView aImg, aImgSTNK;
    ImageButton btnCamSTNK, btnFileSTNK;
    TextView aName,  aType, aPlat, aPlatStart, aPlatEnd, aYear,  aRegNum,
            aDesc, aRangName, aStartDate, aEndDate,
            aPriceAdvance, btnAdvancePrice, aMinRentDay,
            aBasicPriceDisp, aAdvancePriceDisp;
    EditText aBasicPrice, aAdvancePrice;
    LinearLayout conAdvancePrice;
    Integer idAsset;
    String aLatitude, aLongitude, aAddress, aRentPackage, tenant, category, encodedImage,
            isiimage = "", ext, imgString, imgStringSTNK, aDriverStatus, aDeliveryMethod;
    CheckBox aAc, aAb, aDriver, aNoDriver, aAssurace, aDelivery, aPickup;
    RadioGroup aTransmisionGroup;
    RadioButton aTransmisionButton, rManual, rAuto;
    Button btnImgUpload;
    Spinner subcategory, aMerk, aColor, aFuel, aEngCap, aSeat;

    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_IMAGE_STNK_REQUEST = 2;
    private int PICK_DATE_REQUEST = 3;
    private static final int CAMERA_REQUEST = 1888;

    private static final String TAG = "FormAssetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_car_aset);
        setTitle("Aset Mobil");

        iFormAsset = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        contentcontrol();
    }

    private void contentcontrol() {
        subcategory = (Spinner) findViewById(R.id.as_subcat_spinner);
        aMerk = (Spinner) findViewById(R.id.as_merk);
        aColor = (Spinner) findViewById(R.id.as_colour);
        aEngCap = (Spinner) findViewById(R.id.as_engcap);
        aFuel = (Spinner) findViewById(R.id.as_fuel_car);
        aSeat = (Spinner) findViewById(R.id.as_seat_car);
        aName = (TextView) findViewById(R.id.as_name);
        aType = (TextView) findViewById(R.id.as_type);
        aYear = (TextView) findViewById(R.id.as_year);
//        aRegNum = (TextView) findViewById(R.id.as_regnum);
        aPlat = (TextView) findViewById(R.id.as_plat);
        aPlatStart = (TextView) findViewById(R.id.as_plat_start);
        aPlatEnd = (TextView) findViewById(R.id.as_plat_end);
        aTransmisionGroup = (RadioGroup) findViewById(R.id.transmission_group);
        aAc = (CheckBox) findViewById(R.id.as_ck_ac);
        aAb = (CheckBox) findViewById(R.id.as_ck_ab);
        aDriver = (CheckBox) findViewById(R.id.as_ck_driver);
        aNoDriver = (CheckBox) findViewById(R.id.as_ck_no_driver);
        aDelivery = (CheckBox) findViewById(R.id.as_ck_delivery);
        aPickup = (CheckBox) findViewById(R.id.as_ck_pickup);
        aAssurace = (CheckBox) findViewById(R.id.as_ck_assurance);
        aImg = (ImageView) findViewById(R.id.thumb_aset);
        aImgSTNK = (ImageView) findViewById(R.id.stnk_image);
        aMinRentDay = (TextView) findViewById(R.id.as_min_rent_day);
        aRangName = (TextView) findViewById(R.id.as_range_name);
        aStartDate = (TextView) findViewById(R.id.as_start_date);
        aEndDate = (TextView) findViewById(R.id.as_end_date);
        aAdvancePrice = (EditText) findViewById(R.id.as_price_advance);
        aAdvancePriceDisp = (TextView) findViewById(R.id.as_price_advance_disp);
        aBasicPriceDisp = (TextView) findViewById(R.id.as_price_basic_disp);
        aBasicPrice = (EditText) findViewById(R.id.as_price_basic);

        btnImgUpload = (Button) findViewById(R.id.btnUploadFoto);
        btnFileSTNK = (ImageButton) findViewById(R.id.btn_photo);
        btnCamSTNK = (ImageButton) findViewById(R.id.btn_camera);
        btnAdvancePrice = (TextView) findViewById(R.id.btn_price_advance);
        conAdvancePrice = (LinearLayout) findViewById(R.id.con_advance_price);

        btnCamSTNK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        btnImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("asset");
            }
        });

        btnFileSTNK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser("STNK");
            }
        });

        btnAdvancePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conAdvancePrice.setVisibility(View.VISIBLE);
            }
        });

        aBasicPrice.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                if(!aBasicPrice.getText().toString().isEmpty()){
                    Integer price = Integer.parseInt(aBasicPrice.getText().toString().replace(",",""));

                    Integer priceFee = price + (price/100*20);

                    NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
                    String currency = formatter.format(priceFee) + " IDR" ;

                    aBasicPriceDisp.setText(currency);
                } else {
                    aBasicPriceDisp.setText("0 IDR");
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aAdvancePrice.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                if(!aAdvancePrice.getText().toString().isEmpty()){
                    Integer price = Integer.parseInt(aAdvancePrice.getText().toString().replace(",",""));

                    Integer priceFee = price + (price/100*20);

                    NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
                    String currency = formatter.format(priceFee) + " IDR" ;

                    aAdvancePriceDisp.setText(currency);
                } else {
                    aAdvancePriceDisp.setText("0 IDR");
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST);
            }
        });

        aStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST);
            }
        });

//        set value
        if(iFormAsset.getStringExtra("action").equals("update")){
            btnCamSTNK.setVisibility(View.GONE);
            btnFileSTNK.setVisibility(View.GONE);

            aName.setText(iFormAsset.getStringExtra("name"));
            aType.setText(iFormAsset.getStringExtra("type"));
            aYear.setText(iFormAsset.getStringExtra("year"));
            aMinRentDay.setText(iFormAsset.getStringExtra("min_rent_day"));

            String plat = iFormAsset.getStringExtra("plat");
//            aPlatStart.setText(plat.substring(0, plat.indexOf(" ")));
//            aPlat.setText(plat.substring(plat.indexOf(" "), plat.indexOf(" ", 2)));
//            aPlatEnd.setText(plat.substring(plat.indexOf(" ", 2)));

            String a,b,c;
             a = plat.substring(0, plat.indexOf(" ", 1));
            if (a.length()>1){
                b = plat.substring(plat.indexOf(" ")+1, plat.indexOf(" ", 3));
                c = plat.substring(plat.indexOf(" ", 3));
            }else{
                b = plat.substring(plat.indexOf(" "), plat.indexOf(" ", 2));
                c = plat.substring(plat.indexOf(" ", 2));
            }


            aPlatStart.setText(a);
            aPlat.setText(b);
            aPlatEnd.setText(c);
            Log.e(TAG, "Awal :" + a +" tengah "+ b +" akhir "+ c);

            //spinner
            String compareValue = iFormAsset.getStringExtra("subcat");
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.asset_subcategory_entries, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
            subcategory.setAdapter(adapter);
            Log.e(TAG, "Value Sub Cat: " + compareValue);
            if (!compareValue.equals(null)) {
                int spinnerPosition = adapter.getPosition(compareValue);
                subcategory.setSelection(spinnerPosition);
            }

            String merkValue = iFormAsset.getStringExtra("merk");
            ArrayAdapter<CharSequence> merkAdapter = ArrayAdapter.createFromResource(this, R.array.car_brand_entries, android.R.layout.simple_spinner_item);
            merkAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
            aMerk.setAdapter(merkAdapter);
            Log.e(TAG, "Value Merk: " + merkValue);
            if (!merkValue.equals(null)) {
                int merkPosition = merkAdapter.getPosition(merkValue);
                aMerk.setSelection(merkPosition);
            }

            String colorValue = iFormAsset.getStringExtra("color");
            ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, R.array.color_entries, android.R.layout.simple_spinner_item);
            colorAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
            aColor.setAdapter(colorAdapter);
            if (!colorValue.equals(null)) {
                int colorPosition = colorAdapter.getPosition(colorValue);
                aColor.setSelection(colorPosition);
            }

            String fuelValue = iFormAsset.getStringExtra("fuel");
            ArrayAdapter<CharSequence> fuelAdapter = ArrayAdapter.createFromResource(this, R.array.fuel_entries, android.R.layout.simple_spinner_item);
            fuelAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
            aFuel.setAdapter(fuelAdapter);
            if (!fuelValue.equals(null)) {
                int fuelPosition = fuelAdapter.getPosition(fuelValue);
                aFuel.setSelection(fuelPosition);
            }

            String seatValue = iFormAsset.getStringExtra("seat");
            ArrayAdapter<CharSequence> seatAdapter = ArrayAdapter.createFromResource(this, R.array.seats_entries, android.R.layout.simple_spinner_item);
            seatAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
            aSeat.setAdapter(seatAdapter);
            if (!seatValue.equals(null)) {
                int seatPosition = seatAdapter.getPosition(seatValue);
                aSeat.setSelection(seatPosition);
            }

            //Image
            if(!iFormAsset.getStringExtra("main_image").isEmpty()) {
                String imageUrl = AppConfig.URL_IMAGE_ASSETS + iFormAsset.getStringExtra("main_image");
                Picasso.with(getApplicationContext()).load(imageUrl).into(aImg);
            }
            if(!iFormAsset.getStringExtra("no_stnk").isEmpty()) {
                String imageSTNKUrl = AppConfig.URL_IMAGE_DOCUMENTS + iFormAsset.getStringExtra("no_stnk");
                Picasso.with(getApplicationContext()).load(imageSTNKUrl).into(aImgSTNK);
            }

            if (iFormAsset.getStringExtra("assurance").equals("true")){aAssurace.setChecked(true);}
            if (iFormAsset.getStringExtra("air_bag").equals("true")){aAb.setChecked(true);}
            if (iFormAsset.getStringExtra("air_cond").equals("true")){aAc.setChecked(true);}

//            Log.e(TAG, "Transsmision :" + iFormAsset.getStringExtra("transsmision"));
            if(iFormAsset.getStringExtra("transmission").equals("manual")) {
//                aTransmisionGroup.check(R.id.r_manual);
                ((RadioButton)aTransmisionGroup.getChildAt(0)).setChecked(true);

            } else {
                ((RadioButton)aTransmisionGroup.getChildAt(1)).setChecked(true);
            }
//            aTransmisionGroup.check(iFormAsset.getStringExtra("transsmision").equals("manual")?R.id.r_manual:R.id.r_automatic);

            if (iFormAsset.getStringExtra("delivery_method").equals("both")){
                aPickup.setChecked(true);
                aDelivery.setChecked(true);
            }else if (iFormAsset.getStringExtra("delivery_method").equals("pickup")){
                aPickup.setChecked(true);
            }else if (iFormAsset.getStringExtra("delivery_method").equals("deliver")){
                aDelivery.setChecked(true);
            }

            if (iFormAsset.getStringExtra("driver").equals("both")){
                aNoDriver.setChecked(true);
                aDriver.setChecked(true);
            }else if (iFormAsset.getStringExtra("driver").equals("true")){
                aDriver.setChecked(true);
            }else if (iFormAsset.getStringExtra("driver").equals("false")){
                aNoDriver.setChecked(true);
            }
//            ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, R.array.color_entries, android.R.layout.simple_spinner_item);
//            colorAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
//            aColor.setAdapter(colorAdapter);
//            if (!iFormAsset.getStringExtra("color").equals(null)) {
//                int spinnerPosition = adapter.getPosition(iFormAsset.getStringExtra("color"));
//                aColor.setSelection(spinnerPosition);
//            }

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
        getMenuInflater().inflate(R.menu.menu_save_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
            category = iFormAsset.getStringExtra("cat");
            idAsset = iFormAsset.getIntExtra("id_asset",0);

            if(aDriver.isChecked() && aNoDriver.isChecked()){aDriverStatus = "both";}
            else if (aDriver.isChecked()){ aDriverStatus = "true";}
            else if (aNoDriver.isChecked()){ aDriverStatus = "false";}
            else { aDriverStatus = "nodefine";}

            if(aDelivery.isChecked() && aPickup.isChecked()){aDeliveryMethod = "both";}
            else if (aDelivery.isChecked()){ aDeliveryMethod = "deliver";}
            else if (aPickup.isChecked()){ aDeliveryMethod = "pickup";}
            else { aDeliveryMethod = "nodefine";}

            if (aBasicPrice.getText().toString().isEmpty() || aDeliveryMethod.equals("nodefine") || aDriverStatus.equals("nodefine")){
                Toast.makeText(getApplicationContext(), "Harap Lengkapi Form",Toast.LENGTH_LONG).show();
            } else{
                if(iFormAsset.getStringExtra("action").equals("update")){
                    updateDataAset(category);
                }else {
                    if (imgStringSTNK.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Harap Lengkapi Foto STNK", Toast.LENGTH_LONG).show();
                    } else
                        addDataAset(tenant);

                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // IMAGE : pick image
    private void showFileChooser(String action) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if(action.equals("STNK")){
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_STNK_REQUEST);
        } else {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    // IMAGE : get string for upload
    public String getStringImage(Bitmap bmp){
        if(bmp != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }else{
            return null;
        }
    }

    // IMAGE : show in frame
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting the Bitmap to ImageView
                aImg.setImageBitmap(bitmap);

                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = getStringImage(bitmap);
                imgString = ext +"," + isiimage;
                isiimage = imgString;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_STNK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                aImgSTNK.setImageBitmap(bitmap);
                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);

                isiimage = getStringImage(bitmap);
                imgStringSTNK = ext +"," + isiimage;

                Log.e(TAG, "Image : " + imgStringSTNK);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String str64b = getStringImage(photo);
            imgStringSTNK = "image/jpeg," + str64b;

            aImgSTNK.setImageBitmap(photo);

            Log.e(TAG, "Image STNK : " + imgStringSTNK);
        }


        if (requestCode == PICK_DATE_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String resultStart = data.getStringExtra("startDate");
                String resultEnd = data.getStringExtra("endDate");
                /*transaction.setStartDate(resultStart);
                transaction.setEndDate(resultEnd);*/
                aStartDate.setText(resultStart);
                aEndDate.setText(resultEnd);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void addDataAset(String tenant) {
        int transmissionId = aTransmisionGroup.getCheckedRadioButtonId();
        aTransmisionButton = (RadioButton) findViewById(transmissionId);
        aAddress = "BALI,INDONESIA";
        aLatitude = "0";
        aLongitude = "0";
        aRentPackage = "-";

        pDialog.setMessage("loading ...");
        showProgress(true);
        new addAsetTask(tenant).execute();
    }

    private class addAsetTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseAsset;

        private addAsetTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_MOBIL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseAsset = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Asset Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("name", aName.getText().toString());
                    keys.put("slug", aName.getText().toString().replace(" ","-"));
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("brand", aMerk.getSelectedItem().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("year", aYear.getText().toString());
                    keys.put("colour", aColor.getSelectedItem().toString());
                    keys.put("engine_capacity", aEngCap.getSelectedItem().toString());
                    keys.put("license_plat", aPlatStart.getText().toString()+" "+aPlat.getText().toString()+" "+aPlatEnd.getText().toString());
                    keys.put("seat", aSeat.getSelectedItem().toString());
                    keys.put("air_bag", String.valueOf(aAb.isChecked()));
                    keys.put("air_conditioner", String.valueOf(aAc.isChecked()));
                    keys.put("transmission", aTransmisionButton.getText().toString());
                    keys.put("fuel", aFuel.getSelectedItem().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("min_rent_day", aMinRentDay.getText().toString());
                    keys.put("driver_included", aDriverStatus);
                    keys.put("delivery_method", aDeliveryMethod);
                    keys.put("address", aAddress);
                    keys.put("rent_package", aRentPackage);
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
                    if(!imgStringSTNK.isEmpty()) {
                        keys.put("stnk", imgStringSTNK);
                    }
                    if(!isiimage.isEmpty()){
                        keys.put("file", isiimage);
                    }

                    ArrayList<String> pricingArray = new ArrayList<String>();
                    JSONObject priceBasicObject = new JSONObject();
                    try {
                        priceBasicObject.put("price", aBasicPrice.getText().toString().replace(",",""));
                        priceBasicObject.put("range_name","BASECOST");
                        priceBasicObject.put("start_date","1970-01-01");
                        priceBasicObject.put("end_date","1970-01-01");

                        pricingArray.add(priceBasicObject.toString().replace("=",":"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(!aAdvancePrice.getText().toString().isEmpty()) {
                        for (int i = 0; i < 1; i++) {
                            Map<String, String> pricingObject = new HashMap<String, String>();
                            pricingObject.put("\"range_name\"", "\"" + aRangName.getText().toString() + "\"");
                            pricingObject.put("\"start_date\"", "\"" + aStartDate.getText().toString() + "\"");
                            pricingObject.put("\"end_date\"", "\"" + aEndDate.getText().toString() + "\"");
                            pricingObject.put("\"price\"", "\"" + aAdvancePrice.getText().toString().replace(",", "") + "\"");

                            pricingArray.add(pricingObject.toString().replace("=", ":"));
                        }
                    }
                    keys.put("price", pricingArray.toString());
                    Log.e(TAG, "Value Object : " + keys.toString());
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
        protected void onPostExecute(String aset) {
            mAddAssetTask = null;
            showProgress(false);

            if(aset != null){
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FormCarAsetActivity.this,AsetListActivity.class);
                intent.putExtra("id_category", 1);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAddAssetTask = null;
            showProgress(false);
        }
    }

    private void updateDataAset(String category) {
        int transmissionId = aTransmisionGroup.getCheckedRadioButtonId();
        aTransmisionButton = (RadioButton) findViewById(transmissionId);
        aAddress = "BALI,INDONESIA";
        aLatitude = "0";
        aLongitude = "0";
        aRentPackage = "-";

        pDialog.setMessage("loading ...");
        showProgress(true);
        new updateAsetTask(category).execute();
    }

    private class updateAsetTask extends AsyncTask<String, String, String> {
        private final String mCategory;
        private String errorMsg, responseAsset;

        private updateAsetTask(String category) {
            mCategory = category;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_MOBIL;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseAsset = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Asset Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_asset", String.valueOf(idAsset));
                    keys.put("name", aName.getText().toString());
                    keys.put("slug", aName.getText().toString().replace(" ","-"));
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("brand", aMerk.getSelectedItem().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("year", aYear.getText().toString());
                    keys.put("colour", aColor.getSelectedItem().toString());
                    keys.put("engine_capacity", aEngCap.getSelectedItem().toString());
                    keys.put("license_plat", aPlatStart.getText().toString()+" "+aPlat.getText().toString()+" "+aPlatEnd.getText().toString());
                    keys.put("seat", aSeat.getSelectedItem().toString());
                    keys.put("air_bag", String.valueOf(aAb.isChecked()));
                    keys.put("air_conditioner", String.valueOf(aAc.isChecked()));
                    keys.put("transmission", aTransmisionButton.getText().toString());
                    keys.put("fuel", aFuel.getSelectedItem().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("min_rent_day", aMinRentDay.getText().toString());
                    keys.put("driver_included", aDriverStatus);
                    keys.put("delivery_method", aDeliveryMethod);
                    keys.put("address", aAddress);
                    keys.put("rent_package", aRentPackage);
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
//                    if(!imgStringSTNK.isEmpty()) {
//                        keys.put("stnk", imgStringSTNK);
//                    }
                    if(!isiimage.isEmpty()){
                        keys.put("file", isiimage);
                    }

                    ArrayList<String> pricingArray = new ArrayList<String>();
                    JSONObject priceBasicObject = new JSONObject();
                    try {
                        priceBasicObject.put("price", aBasicPrice.getText().toString().replace(",",""));
                        priceBasicObject.put("range_name","BASECOST");
                        priceBasicObject.put("start_date","1970-01-01");
                        priceBasicObject.put("end_date","1970-01-01");

                        pricingArray.add(priceBasicObject.toString().replace("=",":"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(!aAdvancePrice.getText().toString().isEmpty()) {
                        for (int i = 0; i < 1; i++) {
                            Map<String, String> pricingObject = new HashMap<String, String>();
                            pricingObject.put("\"range_name\"", "\"" + aRangName.getText().toString() + "\"");
                            pricingObject.put("\"start_date\"", "\"" + aStartDate.getText().toString() + "\"");
                            pricingObject.put("\"end_date\"", "\"" + aEndDate.getText().toString() + "\"");
                            pricingObject.put("\"price\"", "\"" + aAdvancePrice.getText().toString().replace(",", "") + "\"");

                            pricingArray.add(pricingObject.toString().replace("=", ":"));
                        }
                    }
                    keys.put("price", pricingArray.toString());
                    Log.e(TAG, "Value Object : " + keys.toString());
                    Log.e(TAG, "Asset Keys: " + String.valueOf(keys));
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
        protected void onPostExecute(String aset) {
            mAddAssetTask = null;
            showProgress(false);

            if(aset != null){
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAddAssetTask = null;
            showProgress(false);
        }
    }
}

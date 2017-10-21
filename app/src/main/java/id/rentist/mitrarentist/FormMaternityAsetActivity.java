package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class FormMaternityAsetActivity extends AppCompatActivity {
    private AsyncTask mAddAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Bitmap bitmap;
    Intent iFormAsset;

    LinearLayout conAdvancePrice;
    ImageView aImg;
    TextView aName, aMerk, aType, aMinDayRent, aDesc,
            aRangName, aStartDate, aEndDate, aPriceAdvance, btnAdvancePrice, aBasicPriceDisp, aAdvancePriceDisp;
    EditText aBasicPrice, aAdvancePrice;
    Integer idAsset;
    String aLatitude, aLongitude, aAddress, aRentPackage, tenant, category, encodedImage,
            isiimage = "", ext, imgString, aDeliveryMethod;
    CheckBox aAssurace, aDelivery, aPickup;
    Button btnImgUpload;
    Spinner subcategory;

    private int PICK_DATE_REQUEST = 5;
    private int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "FormAssetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_maternity_aset);
        setTitle("Aset Ibu & Anak");

        iFormAsset = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btnImgUpload = (Button) findViewById(R.id.btnUploadFoto);
        btnAdvancePrice = (TextView) findViewById(R.id.btn_price_advance);
        conAdvancePrice = (LinearLayout) findViewById(R.id.con_advance_price);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        contentcontrol();

        btnImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btnAdvancePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conAdvancePrice.setVisibility(View.VISIBLE);
            }
        });
    }

    private void contentcontrol() {
        subcategory = (Spinner) findViewById(R.id.as_subcat_spinner);
        aName = (TextView) findViewById(R.id.as_name);
        aMerk = (TextView) findViewById(R.id.as_merk);
        aType = (TextView) findViewById(R.id.as_type);
        aAssurace = (CheckBox) findViewById(R.id.as_ck_assurance);
        aImg = (ImageView) findViewById(R.id.thumb_aset);
        aDesc = (TextView) findViewById(R.id.as_desc);
        aMinDayRent = (TextView) findViewById(R.id.as_min_day_rent);
        aRangName = (TextView) findViewById(R.id.as_range_name);
        aStartDate = (TextView) findViewById(R.id.as_start_date);
        aEndDate = (TextView) findViewById(R.id.as_end_date);
        aAdvancePrice = (EditText) findViewById(R.id.as_price_advance);
        aBasicPrice = (EditText) findViewById(R.id.as_price_basic);
        aAdvancePriceDisp = (TextView) findViewById(R.id.as_price_advance_disp);
        aBasicPriceDisp = (TextView) findViewById(R.id.as_price_basic_disp);
        aDelivery = (CheckBox) findViewById(R.id.as_ck_delivery);
        aPickup = (CheckBox) findViewById(R.id.as_ck_pickup);

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

        //aset value
        if(iFormAsset.getStringExtra("action").equals("update")){
            aMerk.setText(iFormAsset.getStringExtra("merk"));
            aType.setText(iFormAsset.getStringExtra("type"));
            aMinDayRent.setText(iFormAsset.getStringExtra("min_rent_day"));
            aDesc.setText(iFormAsset.getStringExtra("description"));

            //Image
            String imageUrl = AppConfig.URL_IMAGE_ASSETS + iFormAsset.getStringExtra("main_image");
            Picasso.with(getApplicationContext()).load(imageUrl).into(aImg);

            //spinner
            String compareValue = iFormAsset.getStringExtra("subcat");
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.maternity_subcategory_entries, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
            subcategory.setAdapter(adapter);
            Log.e(TAG, "Value Sub Cat: " + compareValue);
            if (!compareValue.equals(null)) {
                int spinnerPosition = adapter.getPosition(compareValue);
                subcategory.setSelection(spinnerPosition);
            }
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

    // IMAGE : pick image
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

                // Get file extension
                String imgStr = data.toString();
                ext = imgStr.substring(imgStr.indexOf("typ") + 4, imgStr.indexOf("flg") - 1);

                isiimage = getStringImage(bitmap);
                imgString = ext + "," + isiimage;
                isiimage = imgString;

            } catch (IOException e) {
                e.printStackTrace();
            }
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

            if(aDelivery.isChecked() && aPickup.isChecked()){aDeliveryMethod = "both";}
            else if (aDelivery.isChecked()){ aDeliveryMethod = "deliver";}
            else if (aPickup.isChecked()){ aDeliveryMethod = "pickup";}
            else { aDeliveryMethod = "nodefine";}

            if (aBasicPrice.getText().toString().isEmpty() || aDeliveryMethod.equals("nodefine")){
                Toast.makeText(getApplicationContext(), "Harap Lengkapi Form",Toast.LENGTH_LONG).show();
            } else{
                if(iFormAsset.getStringExtra("action").equals("update")){
                    updateDataAset(category);
                }else{
                    addDataAset(tenant);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDataAset(String tenant) {
        aAddress = "BALI,INDONESIA";
        aLatitude = "0";
        aLongitude = "0";

        pDialog.setMessage("loading ...");
        showProgress(true);
        new FormMaternityAsetActivity.addAsetTask(tenant).execute();
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
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_MATERNITY, new Response.Listener<String>() {
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
                    keys.put("description", aDesc.getText().toString());
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("brand", aMerk.getText().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("min_rent_day", aMinDayRent.getText().toString());
                    keys.put("delivery_method", aDeliveryMethod);
                    keys.put("address", aAddress);
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
                    if(!isiimage.isEmpty()){
                        keys.put("file", isiimage);
                    }
                    //Keys Pricing
                    ArrayList<String> pricingArray = new ArrayList<String>();
                    JSONObject priceBasicObject = new JSONObject();
                    try {
                        priceBasicObject.put("price", aBasicPrice.getText().toString().replace(",", ""));
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
                Intent intent = new Intent(FormMaternityAsetActivity.this,AsetListActivity.class);
                intent.putExtra("asset_name", "Ibu & Anak");
                intent.putExtra("asset_category", 8);
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
        aAddress = "BALI,INDONESIA";
        aLatitude = "0";
        aLongitude = "0";
        aRentPackage = "-";

        pDialog.setMessage("loading ...");
        showProgress(true);
        new FormMaternityAsetActivity.updateAsetTask(category).execute();
    }

    private class updateAsetTask extends AsyncTask<String, String, String> {
        private final String mCategory;
        private String errorMsg, responseAsset;

        private updateAsetTask(String category) {
            mCategory = category;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_MATERNITY;
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
                    keys.put("description", aDesc.getText().toString());
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
                    keys.put("brand", aMerk.getText().toString());
                    keys.put("type", aType.getText().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("address", aAddress);
                    keys.put("latitude", aLatitude);
                    keys.put("longitude", aLongitude);
                    if(!isiimage.isEmpty()){
                        keys.put("file", isiimage);
                    }
                    ArrayList<String> pricingArray = new ArrayList<String>();
                    JSONObject priceBasicObject = new JSONObject();
                    try {
                        priceBasicObject.put("price", aBasicPrice.getText().toString());
                        priceBasicObject.put("range_name","BASECOST");
                        priceBasicObject.put("start_date","1970-01-01");
                        priceBasicObject.put("end_date","1970-01-01");

                        pricingArray.add(priceBasicObject.toString().replace("=",":"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < 1; i++) {
                        Map<String, String> pricingObject = new HashMap<String, String>();
                        pricingObject.put("\"range_name\"","\""+aRangName.getText().toString()+"\"");
                        pricingObject.put("\"start_date\"","\""+aStartDate.getText().toString()+"\"");
                        pricingObject.put("\"end_date\"","\""+aEndDate.getText().toString()+"\"");
                        pricingObject.put("\"price\"",aPriceAdvance.getText().toString());

                        pricingArray.add(pricingObject.toString().replace("=",":"));
                    }
                    keys.put("price", pricingArray.toString());
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

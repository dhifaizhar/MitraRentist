package id.rentist.mitrarentist;

import android.annotation.TargetApi;
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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormYachtAsetActivity extends AppCompatActivity {
    private AsyncTask mAddAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Bitmap bitmap;
    Intent iFormAsset;

    LinearLayout conAdvancePrice;
    ImageView aImg;
    TextView aName, aSubType, aType, aModel, aGuest, aCrew, aCabin, aLength, aBeam,
            aDraft, aGrossTon, aCruisingSpeed, aTopSpeed, aBuilder, aNavalArc,
            aIntDesign, aExtDesign, aBasicPrice, aMinDayRent, btnAdvancePrice,
            aDesc, aRangName, aStartDate, aEndDate, aPriceAdvance, aAssuracePrice;
    Integer idAsset;
    String aLatitude, aLongitude, aAddress, tenant, category, encodedImage, isiimage = "", ext, imgString;
    CheckBox aAssurace;
    Button btnImgUpload;
    Spinner subcategory;

    private int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "FormAssetActivity";
    private static final String TOKEN = "secretissecret";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_yacht_aset);
        setTitle("Aset Kapal Pesiar");

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
        aType = (TextView) findViewById(R.id.as_type);
        aSubType = (TextView) findViewById(R.id.as_subtype);
        aModel = (TextView) findViewById(R.id.as_model);
        aBuilder = (TextView) findViewById(R.id.as_builder);
        aNavalArc = (TextView) findViewById(R.id.as_naval_architect);
        aIntDesign = (TextView) findViewById(R.id.as_interior_designer);
        aExtDesign = (TextView) findViewById(R.id.as_exterior_designer);
        aGuest = (TextView) findViewById(R.id.as_guest);
        aCabin = (TextView) findViewById(R.id.as_cabin);
        aCrew = (TextView) findViewById(R.id.as_crew);
        aLength = (TextView) findViewById(R.id.as_length);
        aBeam = (TextView) findViewById(R.id.as_beam);
        aDraft = (TextView) findViewById(R.id.as_draft);
        aCruisingSpeed = (TextView) findViewById(R.id.as_cruising_speed);
        aTopSpeed = (TextView) findViewById(R.id.as_top_speed);
        aGrossTon = (TextView) findViewById(R.id.as_gross_tonnage);
        aMinDayRent = (TextView) findViewById(R.id.as_min_day_rent);
        aAssurace = (CheckBox) findViewById(R.id.as_ck_assurance);
        aAssuracePrice = (TextView) findViewById(R.id.as_assurance_price);
        aImg = (ImageView) findViewById(R.id.thumb_aset);
        aDesc = (TextView) findViewById(R.id.as_desc);
        aRangName = (TextView) findViewById(R.id.as_range_name);
        aStartDate = (TextView) findViewById(R.id.as_start_date);
        aEndDate = (TextView) findViewById(R.id.as_end_date);
        aPriceAdvance = (TextView) findViewById(R.id.as_price_advance);
        aBasicPrice = (TextView) findViewById(R.id.as_price_basic);


        //aset value
        if(iFormAsset.getStringExtra("action").equals("update")){
            aType.setText(iFormAsset.getStringExtra("type"));
            aSubType.setText(iFormAsset.getStringExtra("subtype"));
            aModel.setText(iFormAsset.getStringExtra("model"));
            aLength.setText(iFormAsset.getStringExtra("length"));
            aBeam.setText(iFormAsset.getStringExtra("beam"));
            aGrossTon.setText(iFormAsset.getStringExtra("gross_tone"));
            aDraft.setText(iFormAsset.getStringExtra("draft"));
            aCruisingSpeed.setText(iFormAsset.getStringExtra("cruising_speed"));
            aTopSpeed.setText(iFormAsset.getStringExtra("top_speed"));
            aBuilder.setText(iFormAsset.getStringExtra("builder"));
            aNavalArc.setText(iFormAsset.getStringExtra("naval_architect"));
            aIntDesign.setText(iFormAsset.getStringExtra("aIntDesign"));
            aExtDesign.setText(iFormAsset.getStringExtra("aExtDesign"));
            aGuest.setText(iFormAsset.getStringExtra("aGuest"));
            aCabin.setText(iFormAsset.getStringExtra("aCabin"));
            aCrew.setText(iFormAsset.getStringExtra("aCrew"));
            aMinDayRent.setText(iFormAsset.getStringExtra("min_rent_day"));

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
            if(iFormAsset.getStringExtra("action").equals("update")){
                updateDataAset(category);
            }else{
                addDataAset(tenant);
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
        new FormYachtAsetActivity.addAsetTask(tenant).execute();
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
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_YACHT, new Response.Listener<String>() {
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
                    keys.put("type", aType.getText().toString());
                    keys.put("sub_type", aSubType.getText().toString());
                    keys.put("model", aModel.getText().toString());
                    keys.put("guest", aGuest.getText().toString());
                    keys.put("cabin", aCabin.getText().toString());
                    keys.put("crew", aCrew.getText().toString());
                    keys.put("length", aLength.getText().toString());
                    keys.put("beam", aBeam.getText().toString());
                    keys.put("gross_tonnage", aGrossTon.getText().toString());
                    keys.put("draft", aDraft.getText().toString());
                    keys.put("cruising_speed", aCruisingSpeed.getText().toString());
                    keys.put("top_speed", aTopSpeed.getText().toString());
                    keys.put("buider", aBuilder.getText().toString());
                    keys.put("naval_architect", aNavalArc.getText().toString());
                    keys.put("interior_design", aIntDesign.getText().toString());
                    keys.put("exterior_design", aExtDesign.getText().toString());
                    keys.put("insurance", String.valueOf(aAssurace.isChecked()));
                    keys.put("insurance_price", aAssuracePrice.getText().toString());
                    keys.put("min_rent_day", aMinDayRent.getText().toString());
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
                        pricingObject.put("\"price\"","\""+aPriceAdvance.getText().toString()+"\"");

                        pricingArray.add(pricingObject.toString().replace("=",":"));
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

        pDialog.setMessage("loading ...");
        showProgress(true);
        new FormYachtAsetActivity.updateAsetTask(category).execute();
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
                    keys.put("description", aDesc.getText().toString());
                    keys.put("subcategory", subcategory.getSelectedItem().toString());
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

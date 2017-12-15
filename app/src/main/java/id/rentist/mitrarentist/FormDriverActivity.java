package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormDriverActivity extends AppCompatActivity {
    private AsyncTask mDetailDriverTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent formDriver;
    Bitmap bitmap;
    RadioGroup aGenderGroup;
    RadioButton aGenderButton;
    CountryCodePicker countryCode;

    int genderId;
    String tenant, aId, birthdate,encodedImage, isiimage = "", ext, imgString, aGender;
    TextView name, sim, bdate, gender, phone;
    ImageView profilePic;
    Button btnUploadFoto;

    private int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "FormDriverActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_driver);
        setTitle("Form Pengemudi");

        formDriver = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        aGenderGroup = (RadioGroup) findViewById(R.id.dr_rad_gender);
        profilePic = (ImageView) findViewById(R.id.dr_thumb_driver);
        name = (TextView)findViewById(R.id.dr_driver_name);
        sim = (TextView)findViewById(R.id.dr_sim_number);
        bdate = (TextView)findViewById(R.id.dr_bdate);
        phone = (TextView)findViewById(R.id.dr_phone);
        countryCode =(CountryCodePicker) findViewById(R.id.country_code);
        btnUploadFoto = (Button) findViewById(R.id.btnUploadFoto);

        // set content control value
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        if(formDriver.getStringExtra("action").equals("update")){
            aId = formDriver.getStringExtra("id_driver");
            Log.e(TAG, "Id Tenant Form Driver : " + aId);
            name.setText(formDriver.getStringExtra("fullname"));
            sim.setText(formDriver.getStringExtra("no_sim"));
            bdate.setText(formDriver.getStringExtra("birthdate"));
            if(formDriver.getStringExtra("gender").equals("male")){
                RadioButton aMaleButton = (RadioButton) findViewById(R.id.radioMale);
                aMaleButton.setChecked(true);
            }else if(formDriver.getStringExtra("gender").equals("female")){
                RadioButton aFemaleButton = (RadioButton) findViewById(R.id.radioFemale);
                aFemaleButton.setChecked(true);
            }

            if(!formDriver.getStringExtra("profilepic").isEmpty()){
                Picasso.with(getApplicationContext()).load(
                        AppConfig.URL_IMAGE_PROFIL + formDriver.getStringExtra("profilepic")).transform(new CircleTransform()).into(profilePic);
            }
        }

        // set action
        bdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePicker = new DatePickerDialog(FormDriverActivity.this,
                        datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));

                datePicker.setCancelable(false);
                datePicker.setTitle("Pilih Tanggal Lahir");
                datePicker.show();
            }
        });

        btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void formDriverTenant(String tenant, String id) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        genderId = aGenderGroup.getCheckedRadioButtonId();
        aGenderButton = (RadioButton) findViewById(genderId);
        if(aGenderButton.getText().toString().equals("Pria")){
            aGender = "male";
        }else{
            aGender = "female";
        }
        if(formDriver.getStringExtra("action").equals("add")){
            new getformDriverAddTask(tenant, id).execute();
        }else if(formDriver.getStringExtra("action").equals("update")){
            new getFormDriverUpdateTask(tenant, id).execute();
        }
    }

    private class getformDriverAddTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idDriver;
        private String errorMsg, responseDriver;

        private getformDriverAddTask(String tenant, String id) {
            mTenant = tenant;
            idDriver = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_ADD_DRIVER + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseDriver = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant Driver Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("fullname", name.getText().toString());
                    keys.put("gender", aGender);
                    keys.put("phone", countryCode.getSelectedCountryCode() + phone.getText().toString());
                    keys.put("no_sim", sim.getText().toString());
                    keys.put("birthdate", bdate.getText().toString());
                    if(!imgString.equals("null")) keys.put("file", imgString);

                    Log.e(TAG, "Key Body : " + keys.toString());
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

            return responseDriver;
        }

        @Override
        protected void onPostExecute(String driver) {
            mDetailDriverTask = null;
            showProgress(false);

            if(driver != null){
                Toast.makeText(getApplicationContext(),"Sukses menyimpan data.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FormDriverActivity.this,DriverDetailActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mDetailDriverTask = null;
            showProgress(false);
        }

    }

    private class getFormDriverUpdateTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idDriver;
        private String errorMsg, responseDriver;

        private getFormDriverUpdateTask(String tenant, String id) {
            mTenant = tenant;
            idDriver = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_EDIT_DRIVER + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseDriver = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant Driver Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_driver", idDriver);
                    keys.put("fullname", name.getText().toString());
                    keys.put("gender", aGenderButton.getText().toString());
                    keys.put("no_sim", sim.getText().toString());
                    keys.put("birthdate", bdate.getText().toString());
                    Log.e(TAG, "File Body : " + imgString);
                    if(imgString != null && !imgString.equals("null") && !imgString.equals(formDriver.getStringExtra("profilepic"))){
                        keys.put("file", imgString);
                    }

                    Log.e(TAG, "Key Body : " + keys.toString());
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

            return responseDriver;
        }

        @Override
        protected void onPostExecute(String user) {
            mDetailDriverTask = null;
            showProgress(false);

            if(user != null){
                Toast.makeText(getApplicationContext(),"Sukses mengubah data.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FormDriverActivity.this,DriverActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mDetailDriverTask = null;
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

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            bdate.setText(year1 + "-" + month1 + "-" + day1);
        }
    };

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                String imgStr = data.toString();

                ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);
                Log.e(TAG, "ext: " + ext);

                //Setting the Bitmap to ImageView
                profilePic.setImageBitmap(bitmap);
                isiimage = getStringImage(bitmap);

                imgString = ext +"," + isiimage;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            isiimage = "";
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            formDriverTenant(tenant, aId);
        }

        return super.onOptionsItemSelected(item);
    }
}

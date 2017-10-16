package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;

public class SelectCategoryActivity extends AppCompatActivity {
    AsyncTask mCategoryTask = null;
    ProgressDialog pDialog;
    JSONObject tenantObject, responseMessage;
    private Intent formTenant;

    String tenant,
            car = "false",
            bike = "false",
            yacht = "false",
            medical = "false",
            foto = "false",
            toys = "false",
            adventure = "false",
            maternity = "false",
            electronic = "false",
            bicycle = "false",
            office = "false";
    ImageView cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10, cat11;
    LinearLayout lay1, lay2, lay3, lay4, lay5, lay6, lay7, lay8, lay9, lay10, lay11;
    Button btnSave;

    private static final String TAG = "CategoryActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        setTitle("");

        formTenant = getIntent();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        cat1 = (ImageView) findViewById(R.id.cat1);
        cat2 = (ImageView) findViewById(R.id.cat2);
        cat3 = (ImageView) findViewById(R.id.cat3);
        cat4 = (ImageView) findViewById(R.id.cat4);
        cat5 = (ImageView) findViewById(R.id.cat5);
        cat6 = (ImageView) findViewById(R.id.cat6);
        cat7 = (ImageView) findViewById(R.id.cat7);
        cat8 = (ImageView) findViewById(R.id.cat8);
        cat9 = (ImageView) findViewById(R.id.cat9);
        cat10 = (ImageView) findViewById(R.id.cat10);
        cat11 = (ImageView) findViewById(R.id.cat11);
        lay1 = (LinearLayout) findViewById(R.id.lay1);
        lay2 = (LinearLayout) findViewById(R.id.lay2);
        lay3 = (LinearLayout) findViewById(R.id.lay3);
        lay4 = (LinearLayout) findViewById(R.id.lay4);
        lay5 = (LinearLayout) findViewById(R.id.lay5);
        lay6 = (LinearLayout) findViewById(R.id.lay6);
        lay7 = (LinearLayout) findViewById(R.id.lay7);
        lay8 = (LinearLayout) findViewById(R.id.lay8);
        lay9 = (LinearLayout) findViewById(R.id.lay9);
        lay10 = (LinearLayout) findViewById(R.id.lay10);
        lay11 = (LinearLayout) findViewById(R.id.lay11);
        btnSave = (Button) findViewById(R.id.btn_save);

        // set content control value
        tenant = formTenant.getStringExtra("id_tenant");

        // set action
        cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(car.equals("false")){
                    lay1.setBackgroundColor(0xffffffff);
                    car = "true";
                }else{
                    lay1.setBackgroundColor(0xffd6d6d6);
                    car = "false";
                }
            }
        });
        cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bike.equals("false")){
                    lay2.setBackgroundColor(0xffffffff);
                    bike = "true";
                }else{
                    lay2.setBackgroundColor(0xffd6d6d6);
                    bike = "false";
                }
            }
        });
        cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yacht.equals("false")){
                    lay3.setBackgroundColor(0xffffffff);
                    yacht = "true";
                }else{
                    lay3.setBackgroundColor(0xffd6d6d6);
                    yacht = "false";
                }
            }
        });
        cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(medical.equals("false")){
                    lay4.setBackgroundColor(0xffffffff);
                    medical = "true";
                }else{
                    lay4.setBackgroundColor(0xffd6d6d6);
                    medical = "false";
                }
            }
        });
        cat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foto.equals("false")){
                    lay5.setBackgroundColor(0xffffffff);
                    foto = "true";
                }else{
                    lay5.setBackgroundColor(0xffd6d6d6);
                    foto = "false";
                }
            }
        });
        cat6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toys.equals("false")){
                    lay6.setBackgroundColor(0xffffffff);
                    toys = "true";
                }else{
                    lay6.setBackgroundColor(0xffd6d6d6);
                    toys = "false";
                }
            }
        });
        cat7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adventure.equals("false")){
                    lay7.setBackgroundColor(0xffffffff);
                    adventure = "true";
                }else{
                    lay7.setBackgroundColor(0xffd6d6d6);
                    adventure = "false";
                }
            }
        });
        cat8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maternity.equals("false")){
                    lay8.setBackgroundColor(0xffffffff);
                    maternity = "true";
                }else{
                    lay8.setBackgroundColor(0xffd6d6d6);
                    maternity = "false";
                }
            }
        });
        cat9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(electronic.equals("false")){
                    lay9.setBackgroundColor(0xffffffff);
                    electronic = "true";
                }else{
                    lay9.setBackgroundColor(0xffd6d6d6);
                    electronic = "false";
                }
            }
        });
        cat10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bicycle.equals("false")){
                    lay10.setBackgroundColor(0xffffffff);
                    bicycle = "true";
                }else{
                    lay10.setBackgroundColor(0xffd6d6d6);
                    bicycle = "false";
                }
            }
        });
        cat11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(office.equals("false")){
                    lay11.setBackgroundColor(0xffffffff);
                    office = "true";
                }else{
                    lay11.setBackgroundColor(0xffd6d6d6);
                    office = "false";
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formCategoryTenant(tenant);
            }
        });
    }

    private void formCategoryTenant(String tenant) {
        pDialog.setMessage("registering account ...");
        showProgress(true);
        new categorySelectTask(tenant).execute();
    }

    private class categorySelectTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responseComp;

        private categorySelectTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_UPDATE_TENANT + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseComp = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Rental Category Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("car", car);
                    keys.put("motorcycle", bike);
                    keys.put("yacht", yacht);
                    keys.put("medical_equipment", medical);
                    keys.put("photography", foto);
                    keys.put("kid_toys", toys);
                    keys.put("adventure", adventure);
                    keys.put("maternity", maternity);
                    keys.put("electronic", electronic);
                    keys.put("bicycle", bicycle);
                    keys.put("office", office);
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

            return responseComp;
        }

        @Override
        protected void onPostExecute(String user) {
            mCategoryTask= null;
            showProgress(false);

            if(user != null){
                Toast.makeText(getApplicationContext(),"Sukses menambahkan kategory rental.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SelectCategoryActivity.this, LoginActivity.class));
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal menambahkan kategory rental.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mCategoryTask = null;
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

}

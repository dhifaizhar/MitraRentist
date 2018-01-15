package id.rentist.mitrarentist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private SessionManager sm;
    Intent iEditRent;
    TextView rName, rOwner, rAddress, rEmail, rPhone, rBankAccount, rBankName, rTenantCode,
            rAccountOwner, rBranch, rCity, rProvince, rVillage, rDistrict, rRentalType,
            rPostalCode, rNoKTP;
    ImageView profilePhoto, phoneVerif, emailVerif;
    ImageButton vAll;
    String imageUrl, tenantCity;
    private ProgressDialog pDialog;

    private static final String TAG = "ProfileActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_profile);
        setTitle(sm.getPreferences("nama_rental"));

        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // conten call controll
        controlContent();
    }

    private void controlContent() {
        //initialize view
        rName = (TextView) findViewById(R.id.pr_name);
        rOwner = (TextView) findViewById(R.id.pr_owner_name) ;
        rAddress = (TextView) findViewById(R.id.pr_address_name);
        rPhone = (TextView) findViewById(R.id.pr_phone_number);
        rEmail = (TextView) findViewById(R.id.pr_email);
        rBankAccount = (TextView) findViewById(R.id.pr_bank_account);
        rBankName = (TextView) findViewById(R.id.pr_bank_name);
        rAccountOwner = (TextView) findViewById(R.id.pr_account_name);
        rBranch= (TextView) findViewById(R.id.pr_branch);
        rProvince = (TextView) findViewById(R.id.pr_province);
        rCity = (TextView) findViewById(R.id.pr_city);
        rDistrict = (TextView) findViewById(R.id.pr_distric);
        rVillage = (TextView) findViewById(R.id.pr_village);
        rRentalType = (TextView) findViewById(R.id.pr_rental_type);
        profilePhoto = (ImageView) findViewById(R.id.pr_thumb);
        rPostalCode = (TextView) findViewById(R.id.pr_postal_code);
        rTenantCode = (TextView) findViewById(R.id.pr_rental_code);
        rNoKTP = (TextView) findViewById(R.id.pr_no_ktp);
        phoneVerif = (ImageView) findViewById(R.id.pr_phone_verif);
        emailVerif = (ImageView) findViewById(R.id.pr_email_verif);

        vAll = (ImageButton) findViewById(R.id.view_testi);

        // set content control value
        rName.setText(sm.getPreferences("nama_rental"));
        rOwner.setText(sm.getPreferences("nama_pemilik"));
        rNoKTP.setText(sm.getPreferences("no_ktp"));
        rAddress.setText(String.valueOf(sm.getPreferences("alamat").isEmpty()?"-":sm.getPreferences("alamat")));
        rPhone.setText(String.valueOf("+"+sm.getPreferences("telepon")));
        rEmail.setText(sm.getPreferences("email_rental"));
        rBankName.setText(String.valueOf(sm.getPreferences("bank_name").isEmpty()?"-":sm.getPreferences("bank_name")));
        rBankAccount.setText(String.valueOf(sm.getPreferences("bank_account").isEmpty()?"-":sm.getPreferences("bank_account")));
        rAccountOwner.setText(sm.getPreferences("account_name"));
        rRentalType.setText("(" + sm.getPreferences("rental_type") + ")");
        rBranch.setText("Cabang : " + String.valueOf(sm.getPreferences("branch").isEmpty()?"-":sm.getPreferences("branch")));
        rPostalCode.setText(sm.getPreferences("kode_pos"));
        rTenantCode.setText(String.valueOf("ID Rental : " + sm.getPreferences("tenant_code")));

        if(sm.getPreferences("origin").equals("phone") || sm.getPreferences("second_verified").equals("phone")){
            phoneVerif.setVisibility(View.VISIBLE);
        }

        if(sm.getPreferences("origin").equals("email") || sm.getPreferences("second_verified").equals("email")){
            emailVerif.setVisibility(View.VISIBLE);
        }

        if(!String.valueOf(sm.getIntPreferences("city")).isEmpty()){
            rCity.setText("");
        }else{
            rCity.setText("Kota Belum Dipilih");
        }

        //Load image
        if (sm.getPreferences("foto_profil_tenant").equals("null")){
            imageUrl = AppConfig.URL_IMAGE_PROFIL + "img_default.png";
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilePhoto);
        } else {
            imageUrl = AppConfig.URL_IMAGE_PROFIL + sm.getPreferences("foto_profil_tenant");
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilePhoto);
        }

        Log.e(TAG, sm.getPreferences("village_name") + " | " + sm.getPreferences("distric_name") + " | " +
                sm.getPreferences("city_name") + " | " + sm.getPreferences("province_name"));
        if(sm.getPreferences("province_name").isEmpty() ||
                sm.getPreferences("city_name").isEmpty() ||
                sm.getPreferences("distric_name").isEmpty() ||
                sm.getPreferences("village_name").isEmpty()){
            Log.e(TAG, "Reuqest");
            getAddress();
        } else {
            rVillage.setText(sm.getPreferences("village_name"));
            rDistrict.setText(", " + sm.getPreferences("distric_name"));
            rCity.setText(sm.getPreferences("city_name"));
            rProvince.setText(", " + sm.getPreferences("province_name"));
        }

        vAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewTesti = new Intent(ProfileActivity.this, TestimonyActivity.class);
                startActivity(iViewTesti);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(sm.getPreferences("role").equals(getString(R.string.role_superadmin))) {
            getMenuInflater().inflate(R.menu.menu_edit_option, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
                iEditRent = new Intent(ProfileActivity.this, FormEditProfilActivity.class);
                startActivityForResult(iEditRent, 2);

        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            ProfileActivity.this.finish();
            Intent ii = new Intent(ProfileActivity.this,ProfileActivity.class);
            startActivity(ii);

        }

    }

    private void getAddress(){
        final int id_province, id_city, id_distric, id_village;

        id_province = sm.getIntPreferences("province");
        id_city = sm.getIntPreferences("city");
        id_distric = sm.getIntPreferences("distric");
        id_village = sm.getIntPreferences("village");

        String URL = AppConfig.URL_PROVINCE;
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        final StringRequest villageRequest = new StringRequest(Request.Method.POST, AppConfig.URL_VILLAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray provinceArray = new JSONArray(response);
                    if(provinceArray.length() > 0){
                        for (int i = 0; i < provinceArray.length(); i++) {
                            JSONObject provinceObject = provinceArray.getJSONObject(i);
                            if(provinceObject.getInt("id") == id_village){
                                rVillage.setText(provinceObject.getString("village_name") + ", ");
                                sm.setPreferences("village_name", provinceObject.getString("village_name"));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Province Data Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_distric", String.valueOf(id_distric));
                return keys;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("token", TOKEN);
                return keys;
            }
        };

        final StringRequest districRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DISTRIC, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray provinceArray = new JSONArray(response);
                    if(provinceArray.length() > 0){
                        for (int i = 0; i < provinceArray.length(); i++) {
                            JSONObject provinceObject = provinceArray.getJSONObject(i);
                            if(provinceObject.getInt("id") == id_distric){
                                rDistrict.setText(provinceObject.getString("distric_name"));
                                sm.setPreferences("distric_name", provinceObject.getString("distric_name"));
                            }
                        }
                    }
                    requestQueue.add(villageRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Province Data Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_city", String.valueOf(id_city));
                return keys;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("token", TOKEN);
                return keys;
            }
        };

        final StringRequest cityRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray provinceArray = new JSONArray(response);
                    if(provinceArray.length() > 0){
                        for (int i = 0; i < provinceArray.length(); i++) {
                            JSONObject provinceObject = provinceArray.getJSONObject(i);
                            if(provinceObject.getInt("id") == id_city){
                                rCity.setText(provinceObject.getString("city_name") + " ,");
                                sm.setPreferences("city_name", provinceObject.getString("city_name"));
                            }
                        }
                    }
                    requestQueue.add(districRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Province Data Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_province", String.valueOf(id_province));
                return keys;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("token", TOKEN);
                return keys;
            }
        };

        StringRequest provinceRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray provinceArray = new JSONArray(response);
                    if(provinceArray.length() > 0){
                        for (int i = 0; i < provinceArray.length(); i++) {
                            JSONObject provinceObject = provinceArray.getJSONObject(i);
                            if(provinceObject.getInt("id") == id_province){
                                rProvince.setText(provinceObject.getString("province_name"));
                                sm.setPreferences("province_name", provinceObject.getString("province_name"));
                            }
                        }
                    }
                    requestQueue.add(cityRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Province Data Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("token", TOKEN);
                return keys;
            }
        };

        requestQueue.add(provinceRequest);
    }

}

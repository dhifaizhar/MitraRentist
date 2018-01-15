package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AdministrationArea;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.FormValidation;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;

public class FormEditProfilActivity extends AppCompatActivity {
    AsyncTask mProfileTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private ArrayList<String> provinceList;
    View focusView;
    Bitmap bitmap;
    CountryCodePicker countryCode;
    FormValidation formValidation;
    AdministrationArea administrationArea;

    EditText rName, rOwner, rAddress, rEmail, rPhone, rBankAccount, rAccountOwner, rBranch, rPostalCode,
            idProv, idCity, idDistric, idVillage, rNoKTP;
    ImageView profilePhoto;
    Button btnUploadFoto, bSaveButton;
    String tenant, erName, erOwner, erAddress, erEmail, erPhone, erBankAccount, erAccountOwner,
            erBranch, erPostalCode, erNoKTP;
    Resources eprofilePhoto;
    ImageLoader mImageLoader;
    String imageUrl, province;
    Spinner rBankName, rProvince, rCity, rDistric, rVillage;

    String encodedImage, isiimage = "", ext, imgString;
    private int PICK_IMAGE_REQUEST = 1;

    private static final String TAG = "FormUserActivity";
    private static final String TOKEN = "secretissecret";
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_edit_profil);
        setTitle("Ubah Profil Rental");

        formValidation = new FormValidation(FormEditProfilActivity.this);
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // conten call controll
        controlContent();
    }

    private void controlContent() {
        //initialize view
        rName = (EditText) findViewById(R.id.epr_rental_name);
        rOwner = (EditText) findViewById(R.id.epr_owner_name) ;
        rAddress = (EditText) findViewById(R.id.epr_address_name);
        rPhone = (EditText) findViewById(R.id.epr_telp);
        rEmail = (EditText) findViewById(R.id.epr_email);
        rBankAccount = (EditText) findViewById(R.id.epr_bank_account);
        rBankName = (Spinner) findViewById(R.id.epr_bank_name);
        rProvince = (Spinner) findViewById(R.id.epr_province);
        idProv = (EditText) findViewById(R.id.id_prov);
        rCity = (Spinner) findViewById(R.id.epr_city);
        idCity = (EditText) findViewById(R.id.id_city);
        rDistric = (Spinner) findViewById(R.id.epr_district);
        idDistric = (EditText) findViewById(R.id.id_distric);
        rVillage = (Spinner) findViewById(R.id.epr_vilagge);
        idVillage = (EditText) findViewById(R.id.id_village);
        rBranch = (EditText) findViewById(R.id.epr_branch);
        rAccountOwner = (EditText) findViewById(R.id.epr_account_name);
        profilePhoto = (ImageView) findViewById(R.id.pr_thumb);
        btnUploadFoto = (Button) findViewById(R.id.btnUploadFoto);
        countryCode =(CountryCodePicker) findViewById(R.id.country_code);
        rPostalCode = (EditText) findViewById(R.id.epr_postal_code);
        rNoKTP = (EditText) findViewById(R.id.epr_no_ktp);

        // Get Area
        administrationArea = new AdministrationArea(FormEditProfilActivity.this, rProvince, rCity, rDistric, rVillage, idProv, idCity, idDistric, idVillage);
        administrationArea.getProvince();

        // set content control value
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        rName.setText(sm.getPreferences("nama_rental"));
        rOwner.setText(sm.getPreferences("nama_pemilik"));
        rAddress.setText(sm.getPreferences("alamat"));
        rPostalCode.setText(sm.getPreferences("kode_pos"));
        rPhone.setText(sm.getPreferences("telepon").substring(2));
        rEmail.setText(sm.getPreferences("email_rental"));
        rBankAccount.setText(sm.getPreferences("bank_account"));
        rBranch.setText(sm.getPreferences("branch"));
        rAccountOwner.setText(sm.getPreferences("account_name"));
        rNoKTP.setText(sm.getPreferences("no_ktp"));
        if(sm.getPreferences("bank_name").equals("BCA")){rBankName.setSelection(0);
        }else if(sm.getPreferences("bank_name").equals("BNI")){rBankName.setSelection(1);
        }else if(sm.getPreferences("bank_name").equals("BRI")){rBankName.setSelection(2);
        }else if(sm.getPreferences("bank_name").equals("Mandiri")){rBankName.setSelection(3);
        }

        if(!String.valueOf(sm.getIntPreferences("province")).isEmpty()){
            rProvince.setSelection(sm.getIntPreferences("province"));
        }
        if(!String.valueOf(sm.getIntPreferences("city")).isEmpty()){
            rCity.setSelection(sm.getIntPreferences("city"));
        }

        if (sm.getPreferences("foto_profil_tenant").equals("null")){
            imageUrl = AppConfig.URL_IMAGE_PROFIL + "default.png";
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilePhoto);
        } else {
            imageUrl = AppConfig.URL_IMAGE_PROFIL + sm.getPreferences("foto_profil_tenant");
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilePhoto);
        }

        btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                PickSetup setup = Tools.imagePickerSetup();
                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        r.getBitmap();
                        r.getError();
                        r.getUri();
                        isiimage = Tools.getStringImageView(r.getBitmap(), profilePhoto);
                        Log.e("onPickResult: ",isiimage );
                    }
                }).show(FormEditProfilActivity.this);


            }
        });

        bSaveButton = (Button) findViewById(R.id.btn_save);
        bSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sm.getPreferences("role").equals("SuperAdmin") || sm.getPreferences("role").equals("Admin")){
                    updateProfileRent();
                }            }
        });

    }

    private void updateProfileRent() {
        rName.setError(null);
        rOwner.setError(null);
        rAddress.setError(null);
        rPhone.setError(null);
        rNoKTP.setError(null);
        rPostalCode.setError(null);
        rEmail.setError(null);
        rBankAccount.setError(null);
        rBranch.setError(null);
        rAccountOwner.setError(null);

        //initialize new value
        erName = rName.getText().toString();
        erOwner = rOwner.getText().toString();
        erAddress = rAddress.getText().toString();
        erPhone = countryCode.getSelectedCountryCode() + rPhone.getText().toString().trim();
        erEmail = rEmail.getText().toString();
        erBankAccount = rBankAccount.getText().toString();
        erBranch = rBranch.getText().toString();
        erAccountOwner = rAccountOwner.getText().toString();
        erPostalCode = rPostalCode.getText().toString();
        eprofilePhoto = profilePhoto.getResources();
        erNoKTP = rNoKTP.getText().toString();

        //some code for post value
        if(!TextUtils.isEmpty(erName)){
            if(!TextUtils.isEmpty(erOwner)){
                if(!TextUtils.isEmpty(erAddress)){
                    if(!TextUtils.isEmpty(erPostalCode)){
                        if(!TextUtils.isEmpty(erNoKTP)){
                            if(formValidation.isPhoneValid(erPhone)){
                                if(!TextUtils.isEmpty(erBankAccount)){
                                    if(!TextUtils.isEmpty(erBankAccount)){
                                        if(!TextUtils.isEmpty(erAccountOwner)){
                                            if(!TextUtils.isEmpty(erBranch)){
                                                pDialog.setMessage("loading ...");
                                                showProgress(true);
                                                new updateProfileTask(tenant).execute();
                                            }else{
                                                rBranch.setError(getString(R.string.error_field_required));
                                                focusView = rBranch;
                                                focusView.requestFocus();
                                            }
                                        }else{
                                            rAccountOwner.setError(getString(R.string.error_field_required));
                                            focusView = rAccountOwner;
                                            focusView.requestFocus();
                                        }
                                    }else{
                                        rBankAccount.setError(getString(R.string.error_field_required));
                                        focusView = rBankAccount;
                                        focusView.requestFocus();
                                    }
                                }else{
                                    rBankAccount.setError(getString(R.string.error_field_required));
                                    focusView = rBankAccount;
                                    focusView.requestFocus();
                                }
                            }else {
                                rPhone.setError(getString(R.string.error_invalid_phone));
                                focusView = rPhone;
                                focusView.requestFocus();
                            }
                        }else{
                            rNoKTP.setError(getString(R.string.error_field_required));
                            focusView = rNoKTP;
                            focusView.requestFocus();
                        }
                    }else{
                        rPostalCode.setError(getString(R.string.error_field_required));
                        focusView = rPostalCode;
                        focusView.requestFocus();
                    }
                }else{
                    rAddress.setError(getString(R.string.error_field_required));
                    focusView = rAddress;
                    focusView.requestFocus();
                }
            }else{
                rOwner.setError(getString(R.string.error_field_required));
                focusView = rOwner;
                focusView.requestFocus();
            }
        }else{
            rName.setError(getString(R.string.error_field_required));
            focusView = rName;
            focusView.requestFocus();
        }
    }

    private class updateProfileTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responseUser;

        private updateProfileTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_UPDATE_TENANT + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseUser = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant Update Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("owner_name", erOwner);
                    keys.put("rental_name", erName);
                    keys.put("address", erAddress);
                    keys.put("postal_code", erPostalCode);
                    keys.put("phone", erPhone);
                    keys.put("bank_name", rBankName.getSelectedItem().toString());
                    keys.put("bank_account", erBankAccount);
                    keys.put("account_name", erAccountOwner);
                    keys.put("branch", erBranch);
                    if(!String.valueOf(idProv.getText()).equals("")) keys.put("id_province", String.valueOf(idProv.getText()));
                    if(!String.valueOf(idCity.getText()).equals("")) keys.put("id_city", String.valueOf(idCity.getText()));
                    if(!String.valueOf(idDistric.getText()).equals("")) keys.put("id_distric", String.valueOf(idDistric.getText()));
                    if(!String.valueOf(idVillage.getText()).equals("")) keys.put("id_village", String.valueOf(idVillage.getText()));
                    keys.put("no_ktp", erNoKTP);
                    if(!isiimage.equals("")) keys.put("file", isiimage);

                    Log.e(TAG, "POST DATA: " + String.valueOf(keys));
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

            return responseUser;
        }

        @Override
        protected void onPostExecute(String user) {
            mProfileTask = null;
            showProgress(false);

            if(user != null){
                // set new preferences
                sm.setPreferences("nama_rental", erName);
                sm.setPreferences("nama_pemilik", erOwner);
                sm.setPreferences("alamat", erAddress );//+", "+ rVillage.getSelectedItem().toString() +", "+ rDistric.getSelectedItem().toString() +
//                        ", "+rCity.getSelectedItem().toString() +", "+ rProvince.getSelectedItem().toString());
                sm.setPreferences("telepon", erPhone);
                sm.setPreferences("no_ktp", erNoKTP);
                sm.setPreferences("kode_pos", erPostalCode);
                sm.setPreferences("email", erEmail);
                sm.setPreferences("bank_name", rBankName.getSelectedItem().toString());
                sm.setPreferences("bank_account", erBankAccount);
                sm.setPreferences("account_name", erAccountOwner);
                sm.setPreferences("branch", erBranch);
                sm.setIntPreferences("province", Integer.valueOf(String.valueOf(idProv.getText())));
                sm.setIntPreferences("city", Integer.valueOf(String.valueOf(idCity.getText())));
                sm.setIntPreferences("distric", Integer.valueOf(String.valueOf(idDistric.getText())));
                sm.setIntPreferences("village", Integer.valueOf(String.valueOf(idVillage.getText()).equals("")?"0":String.valueOf(idVillage.getText())));
                Log.e(TAG, "User : not null");

                try {
                    JSONArray respArray = new JSONArray(user);
                    if(respArray.length() > 0){
                        for (int i = 0; i < respArray.length(); i++) {
                            JSONObject respObject = respArray.getJSONObject(i);
                            sm.setPreferences("foto_profil_tenant", respObject.getString("profil_pic"));
                        }
                    }
                    Log.e(TAG, "Response : " + respArray.toString() );

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),"Sukses mengubah data.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FormEditProfilActivity.this,ProfileActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mProfileTask = null;
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
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_save_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            if(sm.getPreferences("role").equals("SuperAdmin") || sm.getPreferences("role").equals("Admin")){
                updateProfileRent();
            }
        }

        return super.onOptionsItemSelected(item);
    }

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
                profilePhoto.setImageBitmap(bitmap);
                isiimage = Tools.getStringImage(bitmap);

                imgString = ext +"," + isiimage;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            isiimage = "";
        }
    }

}

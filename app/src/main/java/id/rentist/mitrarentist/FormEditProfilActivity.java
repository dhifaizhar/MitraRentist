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
import android.os.Environment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.FormValidation;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormEditProfilActivity extends AppCompatActivity {
    AsyncTask mProfileTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
//    private ArrayList<String> provinceList;
    View focusView;
    Bitmap bitmap;
    CountryCodePicker countryCode;
    FormValidation formValidation;
//    AdministrationArea administrationArea;

    EditText rName, rOwner, rAddress, rEmail, rPhone, rBankAccount, rAccountOwner, rBranch, rPostalCode;
    ImageView profilePhoto;
    Button btnUploadFoto;
    String tenant, erName, erOwner, erAddress, erEmail, erPhone, erBankAccount, erAccountOwner,
            erBranch, erPostalCode;
    Resources eprofilePhoto;
    ImageLoader mImageLoader;
    String imageUrl, province;
    Spinner rBankName, rProvince, rCity;

    String encodedImage, isiimage = "", ext, imgString;
    private int PICK_IMAGE_REQUEST = 1;

    private static final String TAG = "FormUserActivity";
    private static final String TOKEN = "secretissecret";
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_form_edit_profil);
        setTitle("Ubah Profil Rental");

//        administrationArea = new AdministrationArea(FormEditProfilActivity.this);
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
        rCity = (Spinner) findViewById(R.id.epr_city);
        rBranch = (EditText) findViewById(R.id.epr_branch);
        rAccountOwner = (EditText) findViewById(R.id.epr_account_name);
        profilePhoto = (ImageView) findViewById(R.id.pr_thumb);
        btnUploadFoto = (Button) findViewById(R.id.btnUploadFoto);
        countryCode =(CountryCodePicker) findViewById(R.id.country_code);
        rPostalCode = (EditText) findViewById(R.id.epr_postal_code);

        // Get Area
//        administrationArea.getProvince(rProvince);

        // set content control value
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        rName.setText(sm.getPreferences("nama_rental"));
        rOwner.setText(sm.getPreferences("nama_pemilik"));
        rAddress.setText(sm.getPreferences("alamat"));
        rPhone.setText(sm.getPreferences("telepon").substring(2));
        rEmail.setText(sm.getPreferences("email_rental"));
        rBankAccount.setText(sm.getPreferences("bank_account"));
        rBranch.setText(sm.getPreferences("branch"));
        rAccountOwner.setText(sm.getPreferences("account_name"));
        rPostalCode.setText(sm.getPreferences("kode_pos"));
        if(sm.getPreferences("bank_name").equals("BCA")){rBankName.setSelection(0);
        }else if(sm.getPreferences("bank_name").equals("BNI")){rBankName.setSelection(1);
        }else if(sm.getPreferences("bank_name").equals("BRI")){rBankName.setSelection(2);
        }else if(sm.getPreferences("bank_name").equals("Mandiri")){rBankName.setSelection(3);
        }else if(sm.getPreferences("bank_name").equals("Permata")){rBankName.setSelection(4);
        }
        Log.e("CityEDIT", sm.getPreferences("city").toString());

//        if(!sm.getPreferences("city").isEmpty()){
//            rCity.setSelection(Integer.parseInt(sm.getPreferences("city"))-1);
//        }

        if (sm.getPreferences("foto_profil_tenant").equals("null")){
            imageUrl = AppConfig.URL_IMAGE_PROFIL + "default.png";
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilePhoto);
        } else {
            imageUrl = AppConfig.URL_IMAGE_PROFIL + sm.getPreferences("foto_profil_tenant");
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilePhoto);
        }

        btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPic();
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

    private void uploadPic() {
        //invoke image gallery
        Intent igetGallery = new Intent(Intent.ACTION_PICK);

        // where to find data
        File picDrectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picDirPath = picDrectory.getPath();

        // get URI representation
        Uri data = Uri.parse(picDirPath);

        // get all image
        igetGallery.setDataAndType(data, "image/*");

        startActivityForResult(igetGallery, RESULT_LOAD_IMAGE);
    }

    private void updateProfileRent() {
        rName.setError(null);
        rOwner.setError(null);
        rAddress.setError(null);
        rPhone.setError(null);
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

        //some code for post value
        if(!TextUtils.isEmpty(erName)){
            if(!TextUtils.isEmpty(erOwner)){
                if(!TextUtils.isEmpty(erAddress)){
                    if(!TextUtils.isEmpty(erPostalCode)){
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
                        }else{
                            rPhone.setError(getString(R.string.error_invalid_phone));
                            focusView = rPhone;
                            focusView.requestFocus();
                        }
                    }else{
                        rAddress.setError(getString(R.string.error_field_required));
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
                    keys.put("id_city", "148");//String.valueOf(rCity.getSelectedItemId()+1));
                    keys.put("file", isiimage);
//                    Log.e(TAG, "IMAGE ; " + imgString);

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
                sm.setPreferences("alamat", erAddress);
                sm.setPreferences("telepon", erPhone);
                sm.setPreferences("email", erEmail);
                sm.setPreferences("bank_name", rBankName.getSelectedItem().toString());
                sm.setPreferences("bank_account", erBankAccount);
                sm.setPreferences("account_name", erAccountOwner);
                sm.setPreferences("branch", erBranch);
                sm.setPreferences("city", "148");//String.valueOf(rCity.getSelectedItemId()+1));
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
//                FormEditProfilActivity.this.finish();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
//            Uri selectedImage = data.getData();
//            profilePhoto.setImageURI(selectedImage);
//            Toast.makeText(getApplicationContext(),selectedImage.toString(), Toast.LENGTH_LONG).show();
//        }
//    }

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
        getMenuInflater().inflate(R.menu.menu_save_option, menu);
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
                isiimage = getStringImage(bitmap);

                imgString = ext +"," + isiimage;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            isiimage = "";
        }
    }

}

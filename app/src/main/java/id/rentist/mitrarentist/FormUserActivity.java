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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.FormValidation;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormUserActivity extends AppCompatActivity {
    private AsyncTask mDetailUserTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent formUser;
    FormValidation formValidation;
    View focusView;

    String tenant, aId, imgString = "", isiimage = "";
    TextView nama, email, pass, cpass, phone;
    Spinner role;
    ImageView profilPic;
    Button btnUploadFoto;
    CountryCodePicker countryCode;
    LinearLayout layoutPass, layoutConfirmPass;

    private int PICK_IMAGE_REQUEST = 1;

    private static final String TAG = "FormUserActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);
        setTitle("Pengguna");

        formUser = getIntent();
        formValidation = new FormValidation(FormUserActivity.this);
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
        profilPic = (ImageView)findViewById(R.id.fus_thumb);
        nama = (TextView)findViewById(R.id.fus_name);
        role = (Spinner) findViewById(R.id.fus_role);
        email = (TextView)findViewById(R.id.fus_email);
        pass = (TextView)findViewById(R.id.fus_password);
        cpass = (TextView)findViewById(R.id.fus_repassword);
        phone = (TextView) findViewById(R.id.fus_phone);
        btnUploadFoto = (Button) findViewById(R.id.btnUploadFoto);
        countryCode =(CountryCodePicker) findViewById(R.id.country_code);
        layoutPass = (LinearLayout) findViewById(R.id.layout_pass);
        layoutConfirmPass = (LinearLayout) findViewById(R.id.layout_confirm_pass);

        // set content control value
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        if(formUser.getStringExtra("action").equals("update")){
            aId = formUser.getStringExtra("id_user");
            Log.e(TAG, "Id Tenant Form User : " + aId);
            String profilpicStr = formUser.getStringExtra("profile_pic");
            if (profilpicStr.equals("null")){
                String imageUrl = AppConfig.URL_IMAGE_PROFIL + "img_default.png";
                Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilPic);
            }else{
                String imageUrl = AppConfig.URL_IMAGE_PROFIL + profilpicStr;
                Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilPic);
            }
            nama.setText(formUser.getStringExtra("name"));
            email.setText(formUser.getStringExtra("email"));
            phone.setText(formUser.getStringExtra("phone").substring(2));

            if(formUser.getStringExtra("role").equals("Admin")){
                role.setSelection(0);
            }else if(formUser.getStringExtra("role").equals("Operation")){
                role.setSelection(1);
            }else if(formUser.getStringExtra("role").equals("Finance")){
                role.setSelection(2);
            }else if(formUser.getStringExtra("role").equals("Delivery")){
                role.setSelection(3);
            }

            layoutPass.setVisibility(View.GONE);
            layoutConfirmPass.setVisibility(View.GONE);
        }

        // set action
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

    private void formUserTenant(String tenant, String id) {
        if (mDetailUserTask != null) {
            return;
        }

        // Reset errors.
        nama.setError(null);
        email.setError(null);
        phone.setError(null);
        pass.setError(null);
        cpass.setError(null);

        if(!TextUtils.isEmpty(nama.getText().toString())){
            if(formValidation.isEmailValid(email.getText().toString())){
                if(formValidation.isPhoneValid(phone.getText().toString())){
                    if(formUser.getStringExtra("action").equals("add")){
                        if(formValidation.isPasswordValid(pass.getText().toString())){
                            if(formValidation.isConfirmPasswordValid(pass.getText().toString(),cpass.getText().toString())){
                                pDialog.setMessage("loading ...");
                                showProgress(true);
                                new getFormUserAddTask(tenant, id).execute();
                            }else{
                                cpass.setError(getString(R.string.error_invalid_confirm_password));
                                focusView = cpass;
                                focusView.requestFocus();
                            }
                        }else{
                            pass.setError(getString(R.string.error_invalid_password));
                            focusView = pass;
                            focusView.requestFocus();
                        }
                    }else if(formUser.getStringExtra("action").equals("update")){
                        pDialog.setMessage("loading ...");
                        showProgress(true);
                        new getFormUserUpdateTask(tenant, id).execute();
                    }
                }else{
                    phone.setError(getString(R.string.error_invalid_phone));
                    focusView = phone;
                    focusView.requestFocus();
                }
            }else{
                email.setError(getString(R.string.error_invalid_email));
                focusView = email;
                focusView.requestFocus();
            }
        }else{
            nama.setError(getString(R.string.error_field_required));
            focusView = nama;
            focusView.requestFocus();
        }
    }

    private class getFormUserAddTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idUser;
        private String errorMsg, responseUser;

        private getFormUserAddTask(String tenant, String id) {
            mTenant = tenant;
            idUser = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_ADD_USER;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseUser = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant User Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("name", nama.getText().toString());
                    keys.put("role", role.getSelectedItem().toString());
                    keys.put("password", pass.getText().toString());
                    keys.put("email", email.getText().toString());
                    keys.put("phone", countryCode.getSelectedCountryCode()+phone.getText().toString().trim());
                    keys.put("file", imgString);
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

            return responseUser;
        }

        @Override
        protected void onPostExecute(String user) {
            mDetailUserTask = null;
            showProgress(false);

            if(user != null){
                Toast.makeText(getApplicationContext(),"Sukses menambahkan data.", Toast.LENGTH_LONG).show();
                new Intent(FormUserActivity.this,UsersActivity.class);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mDetailUserTask = null;
            showProgress(false);
        }
    }

    private class getFormUserUpdateTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idUser;
        private String errorMsg, responseUser;

        private getFormUserUpdateTask(String tenant, String id) {
            mTenant = tenant;
            idUser = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DETAIL_USER + idUser;
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
                    Log.e(TAG, "Tenant User Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("name", nama.getText().toString());
                    keys.put("role", role.getSelectedItem().toString());
                    keys.put("email", email.getText().toString());
                    keys.put("phone", countryCode.getSelectedCountryCode()+phone.getText().toString().trim());
                    if (!imgString.equals("")){
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

            return responseUser;
        }

        @Override
        protected void onPostExecute(String user) {
            mDetailUserTask = null;
            int id_user;
            showProgress(false);

            if(user != null){
                try {
                    Log.e(TAG, "Response : " + user);
                    JSONArray dataArray = new JSONArray(user);
                    if (dataArray.length() > 0){
                        JSONObject dataObject = dataArray.getJSONObject(0);
                        id_user = dataObject.getInt("id");
                    } else {
                        id_user = 0;
                    }

                    Toast.makeText(getApplicationContext(),"Sukses mengubah data.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FormUserActivity.this,UserDetailActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mDetailUserTask = null;
            showProgress(false);
        }

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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                String imgStr = data.toString();

                String ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);
                Log.e(TAG, "ext: " + ext);

                //Setting the Bitmap to ImageView
                profilPic.setImageBitmap(bitmap);
                isiimage = getStringImage(bitmap);
                imgString = ext +"," + isiimage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            imgString = "";
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            formUserTenant(tenant, aId);
        }

        return super.onOptionsItemSelected(item);
    }

}

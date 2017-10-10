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
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormUserActivity extends AppCompatActivity {
    private AsyncTask mDetailUserTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent formUser;

    String tenant, aId, imgString;
    TextView nama, email, pass;
    Spinner role;
    ImageView profilPic;
    Button btnUploadFoto;

    private int PICK_IMAGE_REQUEST = 1;

    private static final String TAG = "FormUserActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);
        setTitle("User Form");

        formUser = getIntent();
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
        btnUploadFoto = (Button) findViewById(R.id.btnUploadFoto);

        // set content control value
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        if(formUser.getStringExtra("action").equals("update")){
            aId = formUser.getStringExtra("id_user");
            Log.e(TAG, "Id Tenant Form User : " + aId);
            String profilpicStr = formUser.getStringExtra("profile_pic");
            if (profilpicStr.equals("null")){
                profilPic.setImageResource(R.drawable.user_ava_man);
            }else{
                String imageUrl = AppConfig.URL_IMAGE + profilpicStr;
                Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilPic);
            }
            nama.setText(formUser.getStringExtra("name"));
            email.setText(formUser.getStringExtra("email"));
            if(formUser.getStringExtra("role").equals("SuperAdmin")){
                role.setSelection(0);
            }else if(formUser.getStringExtra("role").equals("Operational")){
                role.setSelection(1);
            }else if(formUser.getStringExtra("role").equals("Executive")){
                role.setSelection(2);
            }
        } else if(formUser.getStringExtra("action").equals("add")){
            pass.setVisibility(View.VISIBLE);
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
        pDialog.setMessage("loading ...");
        showProgress(true);
        if(formUser.getStringExtra("action").equals("add")){
            new getFormUserAddTask(tenant, id).execute();
        }else if(formUser.getStringExtra("action").equals("update")){
            new getFormUserUpdateTask(tenant, id).execute();
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
                Toast.makeText(getApplicationContext(),"Sukses mengubah data.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(),"Sukses mengubah data.", Toast.LENGTH_LONG).show();
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
                String isiimage = getStringImage(bitmap);

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

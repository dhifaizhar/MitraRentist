package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.SessionManager;

public class UserDetailActivity extends AppCompatActivity {
    private AsyncTask mDetailUserTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent detIntent;

    Integer aId;
    String changeStatus = "active", tenant, profilpicStr;
    TextView nama, email, role;
    ImageView profilPic;

    private static final String TAG = "DetailUserActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        setTitle("");

        detIntent = getIntent();
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
        profilPic = (ImageView)findViewById(R.id.us_thumb);
        nama = (TextView)findViewById(R.id.us_name);
        role = (TextView)findViewById(R.id.us_role_name);
        email = (TextView)findViewById(R.id.us_email);

        // set content control value
        aId = detIntent.getIntExtra("id_user", 0);
        Log.e(TAG, "Id Tenant Detail User : " + aId.toString());
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        detUserTenant(tenant, aId);
    }

    private void detUserTenant(String tenant, Integer id) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getDetUserTask(tenant, id).execute();
    }

    private class getDetUserTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final Integer idUser;
        private String errorMsg, responseUser;

        private getDetUserTask(String tenant, Integer id) {
            mTenant = tenant;
            idUser = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DETAIL_USER + idUser.toString();
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
                try {
                    JSONObject userObject = new JSONObject(user);
                    Log.d(TAG, String.valueOf(userObject));

                    profilpicStr = userObject.getString("profile_pic");
                    if (profilpicStr.equals("null")){
                        profilPic.setImageResource(R.drawable.user_ava_man);
                    }else{
                        String imageUrl = AppConfig.URL_IMAGE_PROFIL + profilpicStr;
                        Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(profilPic);
                    }
                    nama.setText(userObject.getString("name"));
                    email.setText(userObject.getString("email"));
                    role.setText(userObject.getString("role"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
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
        getMenuInflater().inflate(R.menu.menu_edit_delete_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            detIntent = new Intent(UserDetailActivity.this, FormUserActivity.class);
            detIntent.putExtra("action","update");
            detIntent.putExtra("profile_pic", profilpicStr);
            detIntent.putExtra("id_user", aId.toString());
            detIntent.putExtra("name", nama.getText());
            detIntent.putExtra("role", role.getText());
            detIntent.putExtra("email", email.getText());
            startActivity(detIntent);
        } else if (id == R.id.action_delete){
            deleteDataUser(tenant, aId);
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDataUser(final String tenant, final int aId) {
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Hapus pengguna aplikasi ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                pDialog.setMessage("loading ...");
                showProgress(true);
                new putDeleteUserTask(tenant, aId).execute();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        AlertDialog alertDialog = showAlert.create();
        alertDialog.show();
    }

    private class putDeleteUserTask  extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final int idUser;
        private String errorMsg, responseUser;

        private putDeleteUserTask(String tenant, int aId) {
            mTenant = tenant;
            idUser = aId;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, AppConfig.URL_DELETE_USER + idUser, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseUser = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form User Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    Log.e(TAG, "Delete Data : " + String.valueOf(keys));
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
        protected void onPostExecute(String User) {
            mDetailUserTask = null;
            showProgress(false);

            if(User != null){
                Toast.makeText(getApplicationContext(),"Data berhasil dihapus", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal menghapus data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mDetailUserTask = null;
            showProgress(false);
        }

    }
}

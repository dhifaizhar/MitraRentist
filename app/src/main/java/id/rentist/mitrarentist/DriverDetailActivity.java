package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.SessionManager;

public class DriverDetailActivity extends AppCompatActivity {
    AsyncTask mDetailDriverTask = null;
    Toolbar toolbar;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent detIntent;

    String tenant, aId, birthdate, fotoname;
    TextView name, sim, bdate, gender, phone, email;
    ImageView profilePic;
    FloatingActionButton fab;

    private static final String TAG = "DetailDriverActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_detail);
        setTitle("");

        detIntent = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        profilePic = (ImageView)findViewById(R.id.det_dr_profile_pic);
        name = (TextView)findViewById(R.id.det_dr_name);
        sim = (TextView)findViewById(R.id.det_dr_sim);
        bdate = (TextView)findViewById(R.id.det_dr_bdate);
        gender = (TextView) findViewById(R.id.det_dr_gender);
        phone = (TextView) findViewById(R.id.det_dr_phone_number);
        email = (TextView) findViewById(R.id.det_dr_email);


        // set content control value
        aId = detIntent.getStringExtra("id_driver");
        Log.e(TAG, "Id Tenant Detail User : " + aId);
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        detUserTenant(tenant, aId);
    }

    private void detUserTenant(String tenant, String id) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getDetDriverTask(tenant, id).execute();
    }

    private class getDetDriverTask extends AsyncTask<String, String, String>{
        private final String mTenant, idDriver;
        private String errorMsg, responseDriver;

        private getDetDriverTask(String tenant, String id) {
            mTenant = tenant;
            idDriver = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DETAIL_DRIVER + idDriver;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
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
                try {
                    JSONArray jsonArray = new JSONArray(driver);
                    JSONObject driverObject = jsonArray.getJSONObject(0);
                    Log.d(TAG, String.valueOf(driverObject));

                    Picasso.with(getApplicationContext()).load(
                            AppConfig.URL_IMAGE_PROFIL + driverObject.getString("profile_pic")).transform(new CircleTransform()).into(profilePic);

                    fotoname = driverObject.getString("profile_pic");
                    name.setText(driverObject.getString("fullname"));
                    sim.setText(driverObject.getString("no_sim"));
                    gender.setText(driverObject.getString("gender").equals("male") ? "Pria" : "Wanita");
                    phone.setText(driverObject.getString("phone").equals("null")?"":driverObject.getString("phone"));
                    email.setText(driverObject.getString("email").equals("null")?"":driverObject.getString("email"));
                    birthdate = driverObject.getString("birthdate");

                    // formatter
                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    Date currentDate = new Date(dateformat.parse(birthdate).getTime());
                    // set new format
                    SimpleDateFormat newformat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    String newdate = newformat.format(currentDate);
                    bdate.setText(newdate);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
            detIntent = new Intent(DriverDetailActivity.this, FormDriverActivity.class);
            detIntent.putExtra("action","update");
            detIntent.putExtra("id_driver", aId);
            detIntent.putExtra("fullname", name.getText());
            detIntent.putExtra("no_sim", sim.getText());
            detIntent.putExtra("email", email.getText());
            detIntent.putExtra("phone", phone.getText());
            detIntent.putExtra("birthdate", bdate.getText());
            detIntent.putExtra("gender", gender.getText());
            detIntent.putExtra("profilepic", fotoname);
            startActivityForResult(detIntent, 2);
        } else if (id == R.id.action_delete){
            deleteDataDriver(tenant, aId);
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDataDriver(final String tenant, final String aId) {
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Hapus pengemudi ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                pDialog.setMessage("loading ...");
                showProgress(true);
                new putDeleteDriverTask(tenant, aId).execute();
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

    private class putDeleteDriverTask  extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idDriver;
        private String errorMsg, responseDriver;

        private putDeleteDriverTask(String tenant, String aId) {
            mTenant = tenant;
            idDriver = aId;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, AppConfig.URL_DELETE_DRIVER + mTenant, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseDriver = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Driver Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_driver", idDriver);
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

            return responseDriver;
        }

        @Override
        protected void onPostExecute(String User) {
            mDetailDriverTask = null;
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
            mDetailDriverTask = null;
            showProgress(false);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            detUserTenant(tenant, aId);
        }

    }
}

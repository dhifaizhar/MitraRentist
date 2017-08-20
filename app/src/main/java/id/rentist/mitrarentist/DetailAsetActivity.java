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
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;


public class DetailAsetActivity extends AppCompatActivity {
    private AsyncTask mDetailAssetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private AlertDialog.Builder showAlert;
    private AlertDialog alertDialog;
    private Intent detIntent;

    Integer aId;
    String changeStatus = "active";
    TextView rating, price, status;
    ImageView imgThumbnail;

    private static final String TAG = "DetailAssetActivity";
    private static final String TOKEN = "secretissecret";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);
        setTitle("Daihatsu Jazz | DC 123 WOW");

        detIntent = getIntent();
        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail_aset);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        controlContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_det_aset);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iAsetEdit = new Intent(DetailAsetActivity.this, FormEditAsetActivity.class);
                startActivity(iAsetEdit);
            }
        });
    }

    private void controlContent() {
        //initialize view
        imgThumbnail = (ImageView)findViewById(R.id.det_as_thumb_aset);
        rating = (TextView)findViewById(R.id.det_as_rating_text);
        price = (TextView)findViewById(R.id.det_as_harga_det);
        status = (TextView)findViewById(R.id.det_status_aset);

        // set content control value
        imgThumbnail.setImageResource(R.drawable.mobil_1);
        rating.setText("4/5");
        price.setText("Rp 300.000 /hari");
        aId = detIntent.getIntExtra("id_asset", 0);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatAset();
            }
        });
    }

    private void changeStatAset() {
        showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Ubah status aset ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String nowStatus = "active";
                if(nowStatus.equals("active")){
                    changeStatus = "deactive";
                }
                pDialog.setMessage("loading ...");
                showProgress(true);
                new postStatAssetTask(changeStatus).execute();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        alertDialog = showAlert.create();
        alertDialog.show();
    }

    private class postStatAssetTask extends AsyncTask<String, String, String>{
        private final String mStatus;
        private String errorMsg, responseStatus;

        private postStatAssetTask(String status) {
            mStatus = status;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_EDIT_STATUS_MOBIL;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseStatus = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Change Status Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    //keys.put("id_tenant", mTenant);
                    keys.put("id", String.valueOf(aId));
                    keys.put("status", mStatus);
                    Log.e(TAG, "Change Status Fetch KEYS : " + String.valueOf(keys));
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

            return responseStatus;
        }

        @Override
        protected void onPostExecute(String status) {
            mDetailAssetTask = null;
            showProgress(false);

            if(status != null){
                Toast.makeText(getApplicationContext(),"Data sukses diubah", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mDetailAssetTask = null;
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
}

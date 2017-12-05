package id.rentist.mitrarentist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class VoucherDetailActivity extends AppCompatActivity {
    String id;
    TextView name, poin, desc;
    ImageView image;
    Intent intent;
    Button btn_purchase;

    private static final String TAG = "DetailVoucherActivity";
    private static final String TOKEN = "secretissecret";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_detail);
        intent = getIntent();

        setTitle(intent.getStringExtra("voucher_name"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = (TextView) findViewById(R.id.vd_name);
        poin = (TextView) findViewById(R.id.vd_poin) ;
        desc = (TextView) findViewById(R.id.vd_desc);
        image = (ImageView) findViewById(R.id.vd_image);
        btn_purchase = (Button) findViewById(R.id.btn_purchase);

        name.setText(intent.getStringExtra("voucher_name"));
        poin.setText(intent.getStringExtra("voucher_poin"));
        desc.setText(intent.getStringExtra("voucher_desc"));

        btn_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder scheduleAlert = new AlertDialog.Builder(VoucherDetailActivity.this);
                scheduleAlert.setMessage("Beli Kupon ?");
                scheduleAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        purchaseVoucher(intent.getStringExtra("id_voucher"));
                    }
                });
                scheduleAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                    }
                });
                scheduleAlert.show();
            }
        });
    }

    private void purchaseVoucher(final String id) {
        SessionManager sm = new SessionManager(getApplicationContext());
        String id_tenant = sm.getPreferences("id_tenant");

        String URL = AppConfig.URL_VOUCHER_PURCHASE + id_tenant;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {
                            Toast.makeText(getApplicationContext(),
                                    "Kupon Berhasil dibeli",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Voucher List Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_voucher_catalog", id);
                Log.e(TAG, "Value Object : " + keys.toString());
                return keys;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        queue.add(strReq);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

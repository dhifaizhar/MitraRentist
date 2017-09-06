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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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

public class FormVoucherActivity extends AppCompatActivity implements View.OnClickListener{
    private AsyncTask mFormVoucherTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent formVoucher;
//    private CalendarPickerView calendar;

    Integer id;
    String tenant, vId, vType, vStartDate, vEndDate, nominalV, percentageV;
    Spinner vCategory;
    Button btnGetDate,btnAddVoucher;
    CheckBox vTypeWeb, vTypeMobile;
    EditText vName, vDesc, vCode, vDate, vKuota, vNominal, vPercentage;

    private int mYear, mMonth, mDay;
    private static final String TAG = "FormVoucherActivity";
    private static final String TOKEN = "secretissecret";

    @TargetApi(Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_voucher);
        setTitle("Form Voucher");

        formVoucher = getIntent();
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
        btnAddVoucher=(Button)findViewById(R.id.btn_add);
        btnGetDate=(Button)findViewById(R.id.btn_start_date);
        vName=(EditText)findViewById(R.id.vou_title);
        vCode=(EditText)findViewById(R.id.vou_code);
        vDesc=(EditText)findViewById(R.id.vou_desc);
        vDate=(EditText)findViewById(R.id.vou_date);
        vTypeWeb=(CheckBox)findViewById(R.id.vou_type_web);
        vTypeMobile=(CheckBox)findViewById(R.id.vou_type_mobile);
        vCategory=(Spinner) findViewById(R.id.vou_spinner);
        vKuota=(EditText)findViewById(R.id.vou_amount);
        vNominal=(EditText)findViewById(R.id.vou_discount);
        vPercentage=(EditText)findViewById(R.id.vou_discount_percent);

        // set content control value
        if(formVoucher.getStringExtra("action").equals("update")) {
            id = formVoucher.getIntExtra("id_vou", 0);
            vId = id.toString();

            Log.e(TAG, "Data Voucher to update : " + formVoucher.getStringExtra("action") + "id_vou" + vId);

        }
        vDate.setText(formVoucher.getStringExtra("range_date"));
        vStartDate = formVoucher.getStringExtra("start_date");
        vEndDate = formVoucher.getStringExtra("end_date");
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        if(vTypeWeb.isChecked() && vTypeMobile.isChecked()){
            vType = "both";
        }else if(vTypeWeb.isChecked()){
            vType = "web";
        }else if(vTypeMobile.isChecked()){
            vType = "mobile";
        }



//        if(formVoucher.getStringExtra("action").equals("update")){
//            aId = formVoucher.getStringExtra("id_user");
//            Log.e(TAG, "Id Tenant Form User : " + aId);
//            if(formVoucher.getStringExtra("role").equals("SuperAdmin")){
//                role.setSelection(0);
//            }else if(formVoucher.getStringExtra("role").equals("Operational")){
//                role.setSelection(1);
//            }else if(formVoucher.getStringExtra("role").equals("Executive")){
//                role.setSelection(2);
//            }
//        }

        // set action
        btnGetDate.setOnClickListener(this);
        btnAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataVoucher(tenant);
            }
        });
    }

    private void addDataVoucher(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new postVoucherTask(tenant).execute();
    }

    private class postVoucherTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseVoucher;

        private postVoucherTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_VOUCHER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseVoucher = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Voucher Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("voucher_name", vName.getText().toString());
                    keys.put("description", vDesc.getText().toString());
                    keys.put("voucher_code", vCode.getText().toString());
                    keys.put("start_date", vStartDate);
                    keys.put("end_date", vEndDate);
                    keys.put("type", vType);
                    keys.put("id_asset_category", String.valueOf(vCategory.getSelectedItemId()+1));
                    nominalV = vNominal.getText().toString();
                    percentageV = vPercentage.getText().toString();
                    if(!nominalV.equals("") && !percentageV.equals("")){
                        percentageV = "0";
                    }
                    keys.put("nominal", nominalV.equals("") ? "0" : nominalV);
                    keys.put("percentage", percentageV.equals("") ? "0" : percentageV);
                    Log.e(TAG, "Post Data : " + String.valueOf(keys));
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

            return responseVoucher;
        }

        @Override
        protected void onPostExecute(String voucher) {
            mFormVoucherTask = null;
            showProgress(false);

            if(voucher != null){
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mFormVoucherTask = null;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_option, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {

            deleteDataVoucher(tenant, vId);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDataVoucher(String tenant, String vId) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new putDeleteVoucherTask(tenant, vId).execute();
    }

    private class putDeleteVoucherTask  extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idVou;
        private String errorMsg, responseVoucher;

        private putDeleteVoucherTask(String tenant, String vId) {
            mTenant = tenant;
            idVou = vId;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, AppConfig.URL_DELETE_VOUCHER + mTenant, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseVoucher = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Form Voucher Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_voucher", idVou);
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

            return responseVoucher;
        }

        @Override
        protected void onPostExecute(String voucher) {
            mFormVoucherTask = null;
            showProgress(false);

            if(voucher != null){
                Toast.makeText(getApplicationContext(),"Data berhasil dihapus", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal menghapus data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mFormVoucherTask = null;
            showProgress(false);
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnGetDate) {
            Intent iPickDate = new Intent(FormVoucherActivity.this, DateRangePickerActivity.class);
            startActivity(iPickDate);
            finish();
        }
    }


}

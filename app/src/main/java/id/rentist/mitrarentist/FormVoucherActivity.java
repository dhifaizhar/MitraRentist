package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormVoucherActivity extends AppCompatActivity implements View.OnClickListener{
    private AsyncTask mFormVoucherTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private Intent formVoucher;

    Integer id, category;
    String tenant, vId, vType, vStartDate, vEndDate, nominalV, percentageV, rangeDate;
    Spinner vCategory;
    Button btnGetDate,btnSaveVoucher;
    CheckBox vTypeWeb, vTypeMobile;
    EditText vName, vDesc, vCode, vKuota, vNominal, vPercentage;
    TextView vDate;
    RadioGroup vTypeGroup;
    RelativeLayout rNominal, rPercent;

    private int PICK_DATE_REQUEST = 10;

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
        btnSaveVoucher=(Button)findViewById(R.id.btn_add);
        vName=(EditText)findViewById(R.id.vou_title);
        vCode=(EditText)findViewById(R.id.vou_code);
        vDesc=(EditText)findViewById(R.id.vou_desc);
        vDate=(TextView)findViewById(R.id.vou_date);
        vTypeWeb=(CheckBox)findViewById(R.id.vou_type_web);
        vTypeMobile=(CheckBox)findViewById(R.id.vou_type_mobile);
        vCategory=(Spinner) findViewById(R.id.vou_spinner);
        vKuota=(EditText)findViewById(R.id.vou_amount);
        vNominal=(EditText)findViewById(R.id.vou_discount);
        vPercentage=(EditText)findViewById(R.id.vou_discount_percent);
        vTypeGroup=(RadioGroup)findViewById(R.id.disc_type_group);
        rNominal=(RelativeLayout)findViewById(R.id.r_nominal);
        rPercent=(RelativeLayout)findViewById(R.id.r_percent);
        rNominal.setVisibility(View.GONE);

        vTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(checkedId == R.id.percent){
                    rPercent.setVisibility(View.VISIBLE);
                    rNominal.setVisibility(View.GONE);
                    vNominal.setText("0");
                } else {
                    rPercent.setVisibility(View.GONE);
                    vPercentage.setText("0");
                    rNominal.setVisibility(View.VISIBLE);
                }
            }
        });

        vDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), CustomDatePickerRangeActivity.class);
                startActivityForResult(intent,PICK_DATE_REQUEST);
            }
        });

        // set content control value
        if(formVoucher.getStringExtra("action").equals("update")) {
            id = formVoucher.getIntExtra("id_vou", 0);
            rangeDate = formVoucher.getStringExtra("start_date") + " s/d " + formVoucher.getStringExtra("end_date");
            vId = id.toString();

            vDate.setText(rangeDate);

            if (formVoucher.getStringExtra("type").equals("both")) {
                vTypeWeb.setChecked(true);
                vTypeMobile.setChecked(true);
            } else if (formVoucher.getStringExtra("type").equals("web")) {
                vTypeWeb.setChecked(true);
            } else if (formVoucher.getStringExtra("type").equals("mobile")) {
                vTypeMobile.setChecked(true);
            }

            vName.setText(formVoucher.getStringExtra("name"));
            vCode.setText(formVoucher.getStringExtra("code"));
            vDesc.setText(formVoucher.getStringExtra("desc"));
            vCategory.setSelection(formVoucher.getIntExtra("category",0)-1);
            if (!formVoucher.getStringExtra("nominal").equals("0")){
                rPercent.setVisibility(View.GONE);
                rNominal.setVisibility(View.VISIBLE);
                ((RadioButton)vTypeGroup.getChildAt(1)).setChecked(true);
            }
            if (!formVoucher.getStringExtra("percent").equals("0")){
                rPercent.setVisibility(View.VISIBLE);
                rNominal.setVisibility(View.GONE);
                ((RadioButton)vTypeGroup.getChildAt(0)).setChecked(true);
            }
            vNominal.setText(formVoucher.getStringExtra("nominal"));
            vPercentage.setText(formVoucher.getStringExtra("percent"));
            vKuota.setText(formVoucher.getStringExtra("quantity"));

            vStartDate= "";
            vEndDate= "";

            Log.e(TAG, "Data Voucher to update : " + formVoucher.getStringExtra("action") + "id_vou: " + vId + ", Date: " + rangeDate);
        }

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));

        // set action
        btnSaveVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formVoucher(tenant, vId);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_DATE_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String resultStart = data.getStringExtra("startDate");
                String resultEnd = data.getStringExtra("endDate");
                vStartDate = resultStart;
                vEndDate = resultEnd;
                vDate.setText(resultStart + " s/d " + resultEnd);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    private void formVoucher(String tenant, String id) {


        Boolean valid = formValidation();
        if(valid.equals(true)) {
            pDialog.setMessage("loading ...");
            showProgress(true);
            if (formVoucher.getStringExtra("action").equals("add")) {
                new FormVoucherActivity.postVoucherTask(tenant).execute();
            } else if (formVoucher.getStringExtra("action").equals("update")) {
                new FormVoucherActivity.putUpdateVoucherTask(tenant, id).execute();
            }
        }
    }

    private boolean formValidation(){
        Boolean valid = true;
        vName.setError(null);
        vCode.setError(null);
        vKuota.setError(null);
        vDate.setError(null);
        vNominal.setError(null);
        vPercentage.setError(null);

        if(vTypeWeb.isChecked() && vTypeMobile.isChecked()){
            vType = "both";
        }else if(vTypeWeb.isChecked()){
            vType = "web";
        }else if(vTypeMobile.isChecked()){
            vType = "mobile";
        }else {
            vType = "nodefine";
            valid = false;
            Toast.makeText(getApplicationContext(), "Harap pilih tipe kupon",
                    Toast.LENGTH_LONG).show();
        }

        if(vName.getText().toString().isEmpty()){
            vName.setError(getString(R.string.error_field_required));
            valid = false;
        }

        if(vCode.getText().toString().isEmpty()){
            vCode.setError(getString(R.string.error_field_required));
            valid = false;
        }


        if(vKuota.getText().toString().isEmpty()){
            vKuota.setError(getString(R.string.error_field_required));
            valid = false;
        }

        if(vDate.getText().toString().isEmpty()){
            vDate.setError(getString(R.string.error_field_required));
            valid = false;
        }

        return valid;
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
                    keys.put("quantity", vKuota.getText().toString());
                    keys.put("id_asset_category", String.valueOf(vCategory.getSelectedItemId()+1));
                    keys.put("nominal",  vNominal.getText().toString().replace(",",""));
                    keys.put("percentage", vPercentage.getText().toString());
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

    private class putUpdateVoucherTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idVoucher;
        private String errorMsg, responseUser;

        private putUpdateVoucherTask(String tenant, String id) {
            mTenant = tenant;
            idVoucher = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_UPDATE_VOUCHER + mTenant;
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
                    Log.e(TAG, "Voucher Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("id_voucher", idVoucher);
                    keys.put("voucher_name", vName.getText().toString());
                    keys.put("voucher_code", vCode.getText().toString());
                    keys.put("description", vDesc.getText().toString());
                    keys.put("nominal", vNominal.getText().toString().replace(",",""));
                    keys.put("quantity", vKuota.getText().toString());
                    keys.put("percentage", vPercentage.getText().toString());
                    if (!vStartDate.equals("")) keys.put("start_date", vStartDate);
                    if (!vEndDate.equals(""))  keys.put("end_date", vEndDate);
                    keys.put("id_asset_category", String.valueOf(vCategory.getSelectedItemId()+1));
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
            mFormVoucherTask = null;
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
            mFormVoucherTask = null;
            showProgress(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(formVoucher.getStringExtra("action").equals("update")) {
            getMenuInflater().inflate(R.menu.menu_delete_option, menu);
        }

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

    private void deleteDataVoucher(final String tenant, final String vId) {
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Hapus kupon ini ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                pDialog.setMessage("loading ...");
                showProgress(true);
                new putDeleteVoucherTask(tenant, vId).execute();
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

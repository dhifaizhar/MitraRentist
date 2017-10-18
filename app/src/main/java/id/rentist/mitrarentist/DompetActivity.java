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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.modul.DompetModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class DompetActivity extends AppCompatActivity {
    private List<DompetModul> mDompet = new ArrayList<>();
    private AsyncTask mDompetTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    Intent iWithdrawal;
    String tenant, balance;
    TextView credit, note, date, nominal, desc, status, his_wd;
    Button withdrawal;

    ArrayList<String> transId = new ArrayList<String>();

    private static final String TAG = "DompetActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dompet);
        setTitle("Dompet");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        credit = (TextView)findViewById(R.id.dm_credit);
        withdrawal = (Button)findViewById(R.id.dm_btn_drawal);
        date = (TextView)findViewById(R.id.last_with_date);
        nominal = (TextView)findViewById(R.id.last_with_balance);
        desc = (TextView)findViewById(R.id.last_with_desc);
        status = (TextView)findViewById(R.id.last_with_stat);
        his_wd = (TextView)findViewById(R.id.btn_history_withdrawal);
        note = (TextView)findViewById(R.id.note);

        // set content control value
//        credit.setText("7.000.000 IDR");
//        tunai.setText("0 IDR");

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        retrieveDompetData(tenant);

        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent iWithdrawalal = new Intent(v.getContext(), WithdrawalActivity.class);
                iWithdrawal = new Intent(DompetActivity.this, WithdrawalActivity.class);
                iWithdrawal.putExtra("balance", balance);
                iWithdrawal.putExtra("transId", transId);
                startActivity(iWithdrawal);
            }
        });

        his_wd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iHistoryWd = new Intent(DompetActivity.this, HistoryWithdrawalActivity.class);
                iHistoryWd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iHistoryWd);
            }
        });

    }

    private void retrieveDompetData(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getDataDompetTask(tenant).execute();
    }

    private class getDataDompetTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responseData, total;

        private getDataDompetTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DOMPET + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseData = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Dompet Data Fetch Error : " + errorMsg);
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

            return responseData;
        }

        @Override
        protected void onPostExecute(String user) {
            mDompetTask = null;
            showProgress(false);

            if(user != null){
                try {

                    JSONObject dataObject = new JSONObject(user);
                    JSONArray lastWithdrawalArray = new JSONArray(String.valueOf(dataObject.getJSONArray("last")));
                    JSONArray TransArray = new JSONArray(String.valueOf(dataObject.getJSONArray("data")));
                    JSONArray balanceArray = new JSONArray(String.valueOf(dataObject.getJSONArray("total")));


                    if(lastWithdrawalArray.length() > 0){
                        errorMsg = "-";
                    for (int i = 0; i < lastWithdrawalArray.length(); i++) {
                        JSONObject last = lastWithdrawalArray.getJSONObject(i);

                        String dt = last.getString("createdAt").substring(0,10);

                        String vNominal = ": " +  last.getString("nominal") + " IDR";
                        String vDesc = ": " + last.getString("description");
                        String vStatus = ": " + last.getString("status");
                        date.setText(dt);
                        nominal.setText(vNominal);
                        desc.setText(vDesc);
                        status.setText(vStatus);
                    }

                } else {
                    errorMsg = "Anda belum mengajukan withdrawal";

                    date.setText(": -");
                    nominal.setText(": -");
                    desc.setText(": -");
                    status.setText(": -");

                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }

                    if(TransArray.length() > 0){
                        errorMsg = "-";

                        for (int i = 0; i < TransArray.length(); i++) {
                            JSONObject trans = TransArray.getJSONObject(i);

                            transId.add(trans.getString("id"));
                        }

                    } else {
                        errorMsg = "Note : Anda tidak bisa melakukan withdrawal karena semua saldo anda sudah dalam pengajuan withdrawal. Cek pengajuan withdrawal anda";
                        note.setText(errorMsg);
                        withdrawal.setEnabled(false);
                        withdrawal.setBackgroundColor(getResources().getColor(R.color.colorButtonDefault));
                        withdrawal.setTextColor(getResources().getColor(R.color.colorBlack87));

//                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }

                    if(balanceArray.length() > 0) {
                        JSONObject balanceObject = balanceArray.getJSONObject(0);

                        if (balanceObject.getString("nominal").equals("null")){
                            balance = "0";
                        } else {
                            balance = balanceObject.getString("nominal");
                        }
                    }

                    String balanceKurs = balance + " IDR";

                    credit.setText(balanceKurs);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON Error : " + e);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mDompetTask = null;
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
        getMenuInflater().inflate(R.menu.menu_refresh_option, menu);
        getMenuInflater().inflate(R.menu.menu_history, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            retrieveDompetData(tenant);

            //ubah dengan fungsi
            return true;
        }

        if (id == R.id.action_history) {
            Intent iHistorySaldo = new Intent(DompetActivity.this, HistorySaldoActivity.class);
            startActivity(iHistorySaldo);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

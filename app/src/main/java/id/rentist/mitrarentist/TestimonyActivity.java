package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.rentist.mitrarentist.adapter.TestimonyAdapter;
import id.rentist.mitrarentist.modul.TestimonyModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class TestimonyActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<TestimonyModul> mTesti = new ArrayList<>();
    private AsyncTask mTestiTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    private static final String TAG = "TestiActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimony);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Testimony Pelanggan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // action retrieve data aset
        String tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getTestiDataList(tenant);
    }

    private void getTestiDataList(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getTestiListTask(tenant).execute();
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
        return true;
    }

    private class getTestiListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseTesti;

        private getTestiListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LIST_TESTIMONY + mTenant, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseTesti = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Testimony Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    // Posting parameters to login url
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
            Log.e(TAG, String.valueOf(mTenant + " | " + TOKEN));

            return responseTesti;
        }

        @Override
        protected void onPostExecute(String aset) {
            mTestiTask = null;
            showProgress(false);
            String tNameMember, tPhone, tEmail, tContent, tDate;
            Integer dataLength, tRating;

            if (aset != null) {
                try {
                    JSONArray jsonArray = new JSONArray(aset);
                    Log.e(TAG, "Testimony : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";
                            TestimonyModul testimonyModul = new TestimonyModul();
                            JSONObject jsonobject = jsonArray.getJSONObject(i);

//                            JSONArray detailArray = new JSONArray(String.valueOf(jsonobject.getJSONArray("detail")));
//
//                            if(detailArray.length() > 0){
//                                JSONObject detailobject = detailArray.getJSONObject(0);
//
                                tContent = jsonobject.getString("content");
                                tRating = jsonobject.getInt("rating");
                                testimonyModul.setRating(tRating);
                                testimonyModul.setContent(tContent);
//
//                                Log.e(TAG, "What Data Detail : " + String.valueOf(detailobject));
//                            }

//                            tNameMember = jsonobject.getString("firstname") + " " + jsonobject.getString("lastname");
//                            tPhone = jsonobject.getString("phone");
//                            tEmail = jsonobject.getString("email");
                            String createdDate = jsonobject.getString("createdAt");
                            // formatter
                            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                            Date currentDate = new Date(dateformat.parse(createdDate).getTime());
                            // set new format
                            SimpleDateFormat newformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US);
                            String newdate = newformat.format(currentDate);
                            tDate = newdate;

//                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

//                            testimonyModul.setNameMember(tNameMember);
//                            testimonyModul.setPhone(tPhone);
//                            testimonyModul.setEmail(tEmail);
                            testimonyModul.setDate(tDate);
                            mTesti.add(testimonyModul);
                        }

                        mRecyclerView = (RecyclerView) findViewById(R.id.tm_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new TestimonyAdapter(getApplicationContext(),mTesti);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Belum Ada Testimony";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Belum Ada Testimony";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mTestiTask = null;
            showProgress(false);
        }
    }
}

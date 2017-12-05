package id.rentist.mitrarentist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.DriverAssignAdapter;
import id.rentist.mitrarentist.modul.ItemDriverModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class DriverAssignActivity extends AppCompatActivity {
    RecyclerView mRecycleDriver, mRecycleUser;
    RecyclerView.Adapter mAdapterDriver, mAdapterUser;
    RecyclerView.LayoutManager mLayoutManager;
    private AsyncTask mDriverTask = null;
    private SpinKitView pBar;
    private SessionManager sm;
    private List<ItemDriverModul> mDriver = new ArrayList<>();
    private List<ItemDriverModul> mUser = new ArrayList<>();
    private Intent iDriver;

    String tenant, idTrans, id_driver, driver_name;
    RadioGroup aTypeGroup;
    RadioButton rDriver;
    TextView msgDriver, msgUser;

    private static final String TAG = "AssignDriverActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_assign);

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        aTypeGroup = (RadioGroup) findViewById(R.id.driver_type);
        mRecycleDriver = (RecyclerView) findViewById(R.id.dr_driver);
        mRecycleUser = (RecyclerView) findViewById(R.id.dr_user);
        msgDriver = (TextView) findViewById(R.id.driver_msg);
        msgUser = (TextView) findViewById(R.id.user_msg);
        rDriver = (RadioButton) findViewById(R.id.r_driver);

        pBar.setVisibility(View.VISIBLE);
        iDriver = getIntent();
        driverGetList();

        idTrans = iDriver.getStringExtra("id_transaction");

        aTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                msgUser.setVisibility(View.GONE);
                msgDriver.setVisibility(View.GONE);
                mRecycleUser.setVisibility(View.GONE);
                mRecycleDriver.setVisibility(View.GONE);

                if(checkedId == R.id.r_driver){
                    mRecycleDriver.setVisibility(View.VISIBLE);
                    if(!msgDriver.getText().toString().isEmpty()){
                        msgDriver.setVisibility(View.VISIBLE);
                    }
                } else {
                    mRecycleUser.setVisibility(View.VISIBLE);
                    if(!msgUser.getText().toString().isEmpty()){
                        msgUser.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            id_driver = intent.getStringExtra("id_driver");
            driver_name = intent.getStringExtra("driver_name");

            postAssignDriver(id_driver);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("id_driver", id_driver);
            returnIntent.putExtra("driver_name", driver_name);
            DriverAssignActivity.this.setResult(RESULT_OK, returnIntent);
            Toast.makeText(getApplicationContext(), driver_name + " telah dipilih",
                    Toast.LENGTH_LONG).show();

            finish();
        }
    };

    private void driverGetList() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LIST_DRIVER + tenant, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("error", response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            driverParseData(jObj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Driver Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
            Map<String, String> keys = new HashMap<String, String>();
                keys.put("start_date", iDriver.getStringExtra("start_date"));
                keys.put("end_date", iDriver.getStringExtra("end_date"));

                Log.e(TAG, "Key : " + keys);
            return keys;
        }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        pBar.setVisibility(View.GONE);
        queue.add(strReq);
    }

    private void postAssignDriver(final String id_driver) {
        final String URL = AppConfig.URL_ASSIGN_DRIVER + idTrans;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response : ", response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Driver Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                int aTypeGroupId = aTypeGroup.getCheckedRadioButtonId();
                View typeButton = aTypeGroup.findViewById(aTypeGroupId);
                int idx = aTypeGroup.indexOfChild(typeButton);
                Log.e(TAG,  " INDEX : " + idx);

                if(idx == 0){
                    keys.put("id_driver", id_driver);
                    keys.put("id_tenant_user", "0");
                } else {
                    keys.put("id_driver", "0");
                    keys.put("id_tenant_user", id_driver);
                }
                Log.e(TAG, URL + " With Key Body : " + keys.toString());
                return keys;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        pBar.setVisibility(View.GONE);
        queue.add(strReq);
    }

    private void driverParseData(JSONObject responseJson) {
        String aId, aName, aThumbPhoto;

        try {
            JSONArray jsonDriver = responseJson.getJSONArray("driver");
            JSONArray jsonUser = responseJson.getJSONArray("deliveryman");

            Log.e(TAG, "Response : " + responseJson);

            if(jsonDriver.length() > 0){
                for (int i = 0; i < jsonDriver.length(); i++) {
                    JSONObject jsonobject = jsonDriver.getJSONObject(i);
                    aId = jsonobject.getString("id");
                    aName = jsonobject.getString("fullname");
                    aThumbPhoto = jsonobject.getString("profile_pic");
                    Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                    ItemDriverModul driverModul = new ItemDriverModul();
                    driverModul.setId(aId);
                    driverModul.setName(aName);
                    driverModul.setThumb(aThumbPhoto);
                    mDriver.add(driverModul);
                }

                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapterDriver = new DriverAssignAdapter(getApplicationContext(), mDriver);

                mRecycleDriver.setLayoutManager(mLayoutManager);
                mRecycleDriver.setAdapter(mAdapterDriver);

            }else{
                msgDriver.setVisibility(View.VISIBLE);
                msgDriver.setText("Anda tidak memiliki pengemudi");
            }

            if(jsonUser.length() > 0){
                for (int i = 0; i < jsonUser.length(); i++) {
                    JSONObject jsonobject = jsonUser.getJSONObject(i);
                    aId = jsonobject.getString("id");
                    aName = jsonobject.getString("name");
                    aThumbPhoto = jsonobject.getString("profile_pic");
                    Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                    ItemDriverModul driverModul = new ItemDriverModul();
                    driverModul.setId(aId);
                    driverModul.setName(aName);
                    driverModul.setThumb(aThumbPhoto);
                    mUser.add(driverModul);
                }

                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapterUser = new DriverAssignAdapter(getApplicationContext(), mUser);

                mRecycleUser.setLayoutManager(mLayoutManager);
                mRecycleUser.setAdapter(mAdapterUser);

            }else{
                msgUser.setText("Anda tidak memiliki jenis pengguna delivery");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

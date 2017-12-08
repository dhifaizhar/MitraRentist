package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import id.rentist.mitrarentist.adapter.ComplainAdapter;
import id.rentist.mitrarentist.modul.ComplainModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;

public class ComplainActivity extends AppCompatActivity {
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<ComplainModul> mMsg = new ArrayList<>();

    private SpinKitView pBar;
    SessionManager sm;
    String tenant;

    private static final String TAG = "ComplainActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        setTitle("Pengaduan Pelanggan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        pBar.setVisibility(View.VISIBLE);
        getComplainList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(ComplainActivity.this, FormComplainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void getComplainList() {
        String URL = AppConfig.URL_LIST_COMPLAIN + tenant;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pBar.setVisibility(View.GONE);

                        if (response != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                Log.e(TAG, "Complain : " + jsonArray);
                                mMsg.clear();

                                if(jsonArray.length() > 0){
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                                        ComplainModul itemModul = new ComplainModul();

                                        if (jsonobject.has("id_member")){
                                            JSONObject member = jsonobject.getJSONObject("id_member");

                                            itemModul.setNamaPelanggan(member.getString("firstname") +
                                                    " " + member.getString("lastname"));
                                            itemModul.setPerihal(jsonobject.getString("title"));
                                            itemModul.setDetilKeluhan(jsonobject.getString("content"));
                                            itemModul.setTglKirim(Tools.dateHourFormat(jsonobject.getString("createdAt").substring(0,19)));

                                            mMsg.add(itemModul);
                                        }

                                    }

                                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.msg_recyclerView);
                                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    mAdapter = new ComplainAdapter(ComplainActivity.this, mMsg);

                                    mRecyclerView.setLayoutManager(mLayoutManager);
                                    mRecyclerView.setAdapter(mAdapter);

                                }else{
                                    Toast.makeText(getApplicationContext(),"Tidak Ada Pengaduan" , Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Pengaduan Tidak Ditemukan" , Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Aset List Fetch Error : " +  error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        queue.add(strReq);
    }
}

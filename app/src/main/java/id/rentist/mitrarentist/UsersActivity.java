package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Objects;

import id.rentist.mitrarentist.adapter.UserAdapter;
import id.rentist.mitrarentist.modul.UserModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class UsersActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private List<UserModul> mUser = new ArrayList<>();
    private AsyncTask mUserTask = null;
    private ProgressDialog pDialog;
    private SpinKitView pBar;
    private SessionManager sm;
    private Intent iUserAdd;

    private static final String TAG = "UserActivity";
    private static final String TOKEN = "secretissecret";
    private String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Pengguna");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // action retrieve data aset
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        pBar.setVisibility(View.VISIBLE);
        getUserDataList(tenant);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUser.clear();
                getUserDataList(tenant);
            }
        });
    }

    private void getUserDataList(String tenant) {
//       pBar.setVisibility(View.VISIBLE);
        new getUserListTask(tenant).execute();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (sm.getPreferences("role").equals("SuperAdmin")){
            getMenuInflater().inflate(R.menu.menu_add_option, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            iUserAdd = new Intent(UsersActivity.this, FormUserActivity.class);
            iUserAdd.putExtra("action","add");
            startActivityForResult(iUserAdd, 2);
        }

        return super.onOptionsItemSelected(item);
    }

    private class getUserListTask extends AsyncTask<String, String, String> {
        private final String mTenant;
        private String errorMsg, responseUser;

        private getUserListTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String newURL = AppConfig.URL_LIST_USER + mTenant;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseUser = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "User Fetch Error : " + errorMsg);
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

            return responseUser;
        }

        @Override
        protected void onPostExecute(String user) {
            mUserTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
//            showProgress(false);
            String aName, aEmail, aRole, aThumbPhoto, aPhone;
            Integer aId, dataLength;


            if (user != null) {
                Log.e(TAG, "User Result : " + user);

                try {
                    JSONArray jsonArray = new JSONArray(user);
                    Log.e(TAG, "User : " + jsonArray);
                    dataLength = jsonArray.length();
                    if(dataLength > 0){
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMsg = "-";

                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            aId = jsonobject.getInt("id");
                            aName = jsonobject.getString("name");
                            aEmail = jsonobject.getString("email");
                            aRole = jsonobject.getString("role");
                            aPhone = jsonobject.getString("phone");
                            aThumbPhoto = jsonobject.getString("profile_pic");
                            Log.e(TAG, "What Data : " + String.valueOf(jsonobject));

                            UserModul userModul = new UserModul();
                            userModul.setId(aId);
                            userModul.setName(aName);
                            userModul.setEmail(aEmail);
                            userModul.setPhone(aPhone);
                            userModul.setRole(aRole);
                            userModul.setThumbnail(aThumbPhoto);

                            if(!Objects.equals(aId, sm.getIntPreferences("id"))){
                                mUser.add(userModul);
                            }
                        }


                        mRecyclerView = (RecyclerView) findViewById(R.id.user_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new UserAdapter(getApplicationContext(),mUser);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }else{
                        errorMsg = "Anda belum memiliki User";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Anda belum memiliki User";
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mUserTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
//            showProgress(false);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            pBar.setVisibility(View.VISIBLE);
            mUser.clear();
            getUserDataList(tenant);
        }

    }
}

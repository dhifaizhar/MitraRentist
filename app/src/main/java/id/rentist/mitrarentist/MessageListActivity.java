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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.MessageListAdapter;
import id.rentist.mitrarentist.modul.MessageListModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class MessageListActivity extends AppCompatActivity {
    AsyncTask mMessageTask = null;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Toolbar toolbar;
    private List<MessageListModul> mMsg = new ArrayList<>();
    private List<String> mTrans = new ArrayList<>();
    private SpinKitView pBar;
    private ProgressDialog pDialog;
    SessionManager sm;
    JSONObject dataObject, objectMessage, objectMessageDetail;
    JSONArray dataArray;
    Intent iMessage;
    LinearLayout noData;

    private static final String TAG = "MessageActivity";
    private static final String TOKEN = "secretissecret";
    private String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        setTitle("Pesan");

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

//        pBar = (SpinKitView)findViewById(R.id.progressBar);
//        FadingCircle fadingCircle = new FadingCircle();
//        pBar.setIndeterminateDrawable(fadingCircle);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));

        getTransaction();
        getTransactionNew();

        getMessageList();

        noData = (LinearLayout) findViewById(R.id.no_data);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRecyclerView != null) {
                    mMsg.clear();
                    mRecyclerView.setAdapter(null);
                }
                getMessageList();
            }
        });
    }


    private void getTransaction() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TRANSACTION + tenant, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray acceptArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("accepted")));
                            JSONArray ongoArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("ongoing")));

                            if(acceptArray.length() > 0) {
                                for (int i = 0; i < acceptArray.length(); i++) {
                                    JSONObject transObject = acceptArray.getJSONObject(i);
                                    JSONObject memberObject = transObject.getJSONObject("id_member");

                                    mTrans.add(memberObject.getString("phone"));
                                }
                            }

                            if(ongoArray.length() > 0) {
                                for (int i = 0; i < ongoArray.length(); i++) {
                                    JSONObject transObject = ongoArray.getJSONObject(i);
                                    JSONObject memberObject = transObject.getJSONObject("id_member");

                                    mTrans.add(memberObject.getString("phone"));
                                }
                            }

                            Log.e(TAG, "Transaction Data Add: " + mTrans);

                        } catch (JSONException e) {
                            e.printStackTrace();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
//        pBar.setVisibility(View.GONE);
        queue.add(strReq);
    }

    private void getTransactionNew() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TRANSACTION_NEW + tenant, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if(jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject transObject = jsonArray.getJSONObject(i);
//                                    JSaONObject memberObject = transObject.getJSONObject("id_member");

                                    mTrans.add(transObject.getString("id"));
                                }
                            }


                            Log.e(TAG, "Transaction Data Add:  " + mTrans);

                        } catch (JSONException e) {
                            e.printStackTrace();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
//        pBar.setVisibility(View.GONE);
        queue.add(strReq);
    }

    private void getMessageList() {
//        pBar.setVisibility(View.VISIBLE);
        pDialog.setMessage("loading ...");
        showProgress(true);

        if (mMessageTask != null) {
            return;
        }

        new getMessageListTask().execute();
    }

    private class getMessageListTask extends AsyncTask<String, String, String> {
        private String errorMsg, responseMessage;

        private getMessageListTask() {

        }

        @Override
        protected String doInBackground(String... params) {
            String newURL = "https://rentistid-174904.firebaseio.com/messages.json";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseMessage = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Message Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            });

            try {
                requestQueue.add(stringRequest);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return responseMessage;
        }

        @Override
        protected void onPostExecute(String msg) {
            mMessageTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
//            pBar.setVisibility(View.GONE);
            showProgress(false);

            Integer dataLength;

            if (msg != null) {
                try {
                    dataObject = new JSONObject(msg);
                    dataLength = dataObject.length();
                    Log.e(TAG, "Message Response : " + msg);

                    if(dataLength > 0){
                        errorMsg = "-";
                        Log.e(TAG, "Data lebih dari 1 " );

                        Iterator<String> keys = dataObject.keys();
                        while (keys.hasNext())
                        {
                            String keyValue = (String)keys.next();
                            Log.e(TAG, "keysnya : " + keyValue);

                            if (mTrans.contains(keyValue)){

                                JSONObject user = dataObject.getJSONObject(keyValue);
                                Log.e(TAG, "usernya : " + user);

                                Iterator<String> userKeys = user.keys();
                                while (userKeys.hasNext()) {
                                    String phone = (String)userKeys.next();
                                    Log.e(TAG, "phonenya : " + phone);

                                    if (!phone.equals(sm.getPreferences("hp"))){
                                        MessageListModul msgModul = new MessageListModul();
                                        msgModul.setTitle(keyValue);
                                        msgModul.setName(phone);

                                        mMsg.add(msgModul);
                                        Log.e(TAG, "Ku ingin tahu : " + phone);
                                    }

                                }

                            }

                        }


                        mRecyclerView = (RecyclerView) findViewById(R.id.msg_recyclerView);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new MessageListAdapter(getApplicationContext(),mMsg);

                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);
                        noData.setVisibility(View.GONE);
                    }else{
                        noData.setVisibility(View.VISIBLE);
                        errorMsg = "Tidak ada pesan.";
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mMessageTask= null;
            mSwipeRefreshLayout.setRefreshing(false);
            pBar.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            //ubah dengan fungsi
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
    public void onRestart(){
        super.onRestart();
        getMessageList();
    }
}

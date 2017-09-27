package id.rentist.mitrarentist;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class CriticSuggestionActivity extends AppCompatActivity {
    private AsyncTask mCriticSuggestionTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    String name, email;
    Spinner category, type;
    EditText title, content;

    private static final String TAG = "CriticSuggestActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critic_suggestion);
        setTitle("Kritik & Saran");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        name = sm.getPreferences("nama");
        email = sm.getPreferences("email");
        category = (Spinner) findViewById(R.id.as_cat_spinner);
        type = (Spinner) findViewById(R.id.type_spinner);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);

        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCriticSuggest(name, email);
            }
        });

    }

    private void sendCriticSuggest(String name, String email) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new CriticSuggestionActivity.postCriticSuggestTask(name, email).execute();
    }

    private class postCriticSuggestTask extends AsyncTask<String, String, String>{
        private final String mName, mEmail;
        private String errorMsg, responseCritic;

        private postCriticSuggestTask(String name, String email) {
            mName = name;
            mEmail = email;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CRITIC_SUGGESTION, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseCritic = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Critic Suggest Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("name", mName);
                    keys.put("email", mEmail);
                    keys.put("id_asset_category", String.valueOf(category.getSelectedItemId()+1));
                    keys.put("type", type.getSelectedItem().toString());
                    keys.put("title", title.getText().toString());
                    keys.put("content", content.getText().toString());
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

            return responseCritic;
        }

        @Override
        protected void onPostExecute(String voucher) {
            mCriticSuggestionTask = null;
            showProgress(false);

            if(voucher != null){
                Toast.makeText(getApplicationContext(),"Data Berhasil Dikirim", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal mengirim data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mCriticSuggestionTask = null;
            showProgress(false);
        }
    }

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

}

package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class TestimonyAddActivity extends AppCompatActivity {
    private SessionManager sm;
    private Intent iTesti;

    SimpleRatingBar cleanliness, neatness, honesty, comunication;
    EditText content;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimony_add);

        sm = new SessionManager(getApplicationContext());

        cleanliness = (SimpleRatingBar) findViewById(R.id.t_cleanliness_rating);
        neatness = (SimpleRatingBar) findViewById(R.id.t_neatness_rating);
        honesty = (SimpleRatingBar) findViewById(R.id.t_honesty_rating);
        comunication = (SimpleRatingBar) findViewById(R.id.t_comunication_rating);
        content = (EditText) findViewById(R.id.t_content);
        submit = (Button) findViewById(R.id.btn_submit) ;

        iTesti = getIntent();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void getData() {


        String url = AppConfig.URL_TESTIMONY_SUBMIT;


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_member", iTesti.getStringExtra("id_member"));
        params.put("id_tenant", iTesti.getStringExtra("id_tenant"));
        params.put("id_transaction_detail",iTesti.getStringExtra("id"));
        params.put("cleanliness",String.valueOf(cleanliness.getRating()));
        params.put("neatness", String.valueOf(neatness.getRating()));
        params.put("honesty", String.valueOf(honesty.getRating()));
        params.put("communication", String.valueOf(comunication.getRating()));
        params.put("content", content.getText().toString());
        params.put("created_by", "tenant");

        Log.e("paramsnyaadalah", String.valueOf(cleanliness.getStarSize())+"-"+ iTesti.getStringExtra("id") + iTesti.getStringExtra("id_tenant") );

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        Toast.makeText(TestimonyAddActivity.this, "Tertimoni berhasil dikirim", Toast.LENGTH_SHORT).show();
                        TestimonyAddActivity.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                        Toast.makeText(TestimonyAddActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        TestimonyAddActivity.this.finish();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "secretissecret");


                return params;
            }
        };
        queue.add(jor);

    }
}

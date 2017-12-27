package id.rentist.mitrarentist;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.NumberTextWatcherForThousand;
import id.rentist.mitrarentist.tools.SessionManager;

public class FormDeliveryPriceActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private SessionManager sm;

    Intent iDelivery;
    String tenant;
    EditText distance, price;
    LinearLayout rowCategory;
    CheckBox car, motor, yacht, medic, photo, toys, adventure, maternity, elektronic, bicycle, office, fashion;
    List<Integer> category = new ArrayList<>();

    private static final String TAG = "DeliveryPriceActivity";
    private static final String TOKEN = "secretissecret";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_delivery_price);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        tenant = sm.getIntPreferences("id_tenant").toString();
        distance = (EditText) findViewById(R.id.distance);
        price = (EditText) findViewById(R.id.price);
        rowCategory = (LinearLayout) findViewById(R.id.row_asset_category);
        car = (CheckBox) findViewById(R.id.ck_car);
        motor = (CheckBox) findViewById(R.id.ck_motor);
        yacht = (CheckBox) findViewById(R.id.ck_yacht);
        medic = (CheckBox) findViewById(R.id.ck_medic);
        photo = (CheckBox) findViewById(R.id.ck_photo);
        toys = (CheckBox) findViewById(R.id.ck_toys);
        adventure = (CheckBox) findViewById(R.id.ck_adventure);
        maternity  = (CheckBox) findViewById(R.id.ck_maternity);
        elektronic = (CheckBox) findViewById(R.id.ck_elektronic);
        bicycle = (CheckBox) findViewById(R.id.ck_bicycle);
        office = (CheckBox) findViewById(R.id.ck_office);
        fashion = (CheckBox) findViewById(R.id.ck_fashion);

        String[] asset_category = getApplicationContext().getResources().getStringArray(R.array.asset_category_entries);
        car.setText(asset_category[0]);
        motor.setText(asset_category[1]);
        yacht.setText(asset_category[2]);
        medic.setText(asset_category[3]);
        photo.setText(asset_category[4]);
        toys.setText(asset_category[5]);
        adventure.setText(asset_category[6]);
        maternity.setText(asset_category[7]);
        elektronic.setText(asset_category[8]);
        bicycle.setText(asset_category[9]);
        office.setText(asset_category[10]);
        fashion.setText(asset_category[11]);

        price.addTextChangedListener(new NumberTextWatcherForThousand(price));

        iDelivery = getIntent();
        Log.e(TAG, "FROM" +iDelivery.getStringExtra("from") );
        if (iDelivery.getStringExtra("from").equals("list")){
            rowCategory.setVisibility(View.VISIBLE);
        } else {
            rowCategory.setVisibility(View.GONE);
            category.add(Integer.parseInt(iDelivery.getStringExtra("id_asset_category")));
        }

        final ImageView btn_expand = (ImageView) findViewById(R.id.expandable_toggle_button);
        final LinearLayout mItemExpand = (LinearLayout) findViewById(R.id.category);

        btn_expand.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mItemExpand.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    mItemExpand.setVisibility(View.VISIBLE);
                    btn_expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_18dp);
                } else {
                    // it's expanded - collapse it
                    mItemExpand.setVisibility(View.GONE);
                    btn_expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_18dp);
                }

                @SuppressLint("ObjectAnimatorBinding")
                ObjectAnimator animation = ObjectAnimator.ofInt(mItemExpand, String.valueOf(btn_expand), mItemExpand.getHeight());
                animation.setDuration(200).start();
            }
        });

        Button btnSend = (Button) findViewById(R.id.btn_add);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(distance.getText().toString().isEmpty() || price.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Harap Lengkapi Form",Toast.LENGTH_LONG).show();

                }else{
                    addData();

                }
            }
        });
    }

    private void addData() {
        pDialog.setMessage("Menyimpan Data...");
        showProgress(true);
        if (iDelivery.getStringExtra("from").equals("list")) {
            if (car.isChecked()) category.add(1);
            if (motor.isChecked()) category.add(2);
            if (yacht.isChecked()) category.add(3);
            if (medic.isChecked()) category.add(4);
            if (photo.isChecked()) category.add(5);
            if (toys.isChecked()) category.add(6);
            if (adventure.isChecked()) category.add(7);
            if (maternity.isChecked()) category.add(8);
            if (elektronic.isChecked()) category.add(9);
            if (bicycle.isChecked()) category.add(10);
            if (office.isChecked()) category.add(11);
            if (fashion.isChecked()) category.add(12);
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DELIVERY_PRICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);

                if(response != null){
                    Toast.makeText(getApplicationContext(),"Data berhasil ditambahkan", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Gagal menyimpan data", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Complain Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_tenant", tenant);
                keys.put("max_distance", distance.getText().toString());
                keys.put("price_per_km", NumberTextWatcherForThousand.trimCommaOfString(price.getText().toString()));
                keys.put("id_asset_category", category.toString());
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

        requestQueue.add(stringRequest);
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

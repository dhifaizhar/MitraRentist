package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.Tools;

public class MemberProfileActivity extends AppCompatActivity {
    private Intent iMember;

    TextView mName, mLevel, mPhone, mEmail, mBirthday, mAddress;
    ImageView mProfilePic, mKTP, mKK, mRekListrik;
    SimpleRatingBar mCleanliness, mNeatness, mHonesty, mComunication;

    private static final String TAG = "MemberProfileActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        setTitle("Detil Pemesan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        iMember = getIntent();

        mName = (TextView) findViewById(R.id.mp_name);
        mPhone = (TextView) findViewById(R.id.mp_phone_number);
        mLevel = (TextView) findViewById(R.id.mp_level);
        mAddress = (TextView) findViewById(R.id.mp_address_name);
        mBirthday = (TextView) findViewById(R.id.mp_birthday);
        mEmail = (TextView) findViewById(R.id.mp_email);
        mProfilePic = (ImageView) findViewById(R.id.mp_thumb);
        mKTP = (ImageView) findViewById(R.id.mp_ktp_img);
        mKK = (ImageView) findViewById(R.id.mp_kk_img);
        mRekListrik = (ImageView) findViewById(R.id.mp_rek_listrik_img);
        mCleanliness = (SimpleRatingBar) findViewById(R.id.mp_cleanliness_rating);
        mNeatness = (SimpleRatingBar) findViewById(R.id.mp_neatness_rating);
        mHonesty = (SimpleRatingBar) findViewById(R.id.mp_honesty_rating);
        mComunication = (SimpleRatingBar) findViewById(R.id.mp_comunication_rating);

        getDataMember();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getDataMember() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_MEMBER_PROFILE + iMember.getStringExtra("id_member"), new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        memberData(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Member Fetch Error : " + error.toString());
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

    private void memberData(String response) {
        Log.e(TAG, "Member Response: " + iMember.getStringExtra("id_member") + " , " +response);

        try {
            JSONObject dataObject = new JSONObject(response);
            JSONObject ratingObject = new JSONObject(String.valueOf(dataObject.getJSONObject("ratings")));

            mName.setText(dataObject.getString("firstname") + " " + dataObject.getString("lastname"));
            mPhone.setText("+" + dataObject.getString("phone"));
            mEmail.setText(dataObject.getString("email"));
            mAddress.setText(dataObject.getString("address"));
            mBirthday.setText(Tools.dateFineFormat(dataObject.getString("birthdate").substring(0,10)));

            mLevel.setText(dataObject.getString("badge"));
            if (mLevel.getText().equals(getResources().getString(R.string.member_badge_verified))) {
                mLevel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            } else if (mLevel.getText().equals(getResources().getString(R.string.member_badge_smart_con))) {
                mLevel.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            }

            String thumbURL = dataObject.getString("profil_pic").equals("null") ? AppConfig.URL_IMAGE_PROFIL + "img_default.png" :
                    AppConfig.URL_IMAGE_PROFIL + dataObject.getString("profil_pic");

            Picasso.with(getApplicationContext()).load(thumbURL).transform(new CircleTransform()).into(mProfilePic);

            if(dataObject.getString("ktp").equals("null")) mKTP.setVisibility(View.GONE);
            else Picasso.with(getApplicationContext()).load(AppConfig.URL_IMAGE_DOCUMENTS + dataObject.getString("ktp")).into(mKTP);

            if(dataObject.getString("family_card").equals("null")) mKK.setVisibility(View.GONE);
            else Picasso.with(getApplicationContext()).load(AppConfig.URL_IMAGE_DOCUMENTS + dataObject.getString("family_card")).into(mKK);

            if(dataObject.getString("electricity_bills").equals("null")) mRekListrik.setVisibility(View.GONE);
            else Picasso.with(getApplicationContext()).load(AppConfig.URL_IMAGE_DOCUMENTS + dataObject.getString("electricity_bills")).into(mRekListrik);

            Float clean = Float.parseFloat(ratingObject.getString("cleanliness"))/ratingObject.getInt("counts");
            Float neat = Float.parseFloat(ratingObject.getString("neatness"))/ratingObject.getInt("counts");
            Float honest = Float.parseFloat(ratingObject.getString("honesty"))/ratingObject.getInt("counts");
            Float com = Float.parseFloat(ratingObject.getString("communication"))/ratingObject.getInt("counts");

            mCleanliness.setRating(ratingObject.getInt("counts") > 0 ? clean : 0);
            mNeatness.setRating(ratingObject.getInt("counts") > 0 ? neat : 0);
            mHonesty.setRating(ratingObject.getInt("counts") > 0 ? honest : 0);
            mComunication.setRating(ratingObject.getInt("counts") > 0 ? com : 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

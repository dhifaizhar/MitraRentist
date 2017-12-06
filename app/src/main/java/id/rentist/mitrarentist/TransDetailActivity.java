package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.PricingTools;
import id.rentist.mitrarentist.tools.SessionManager;

public class TransDetailActivity extends AppCompatActivity {
    private AsyncTask mTransactionTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;

    Button btnClosePopup;
    Button btnDriver;
    ImageButton btnCamera;
    private PopupWindow pwindow;
    private Intent itransDet;

    TextView mAset, mPrice, mCodeTrans, mMember, mStartDate, mEndDate, mDriver, mPicktime, mAddress,
            mNote, feature_name, mOrderDate;
    LinearLayout mAdditional, con_add_feature, con_voucher,con_insurance, con_additonal;

    ImageView mAsetThumb, withDriverCheck;

    private static int PICK_DRIVER_REQUEST = 3;
    private static final int CAMERA_REQUEST = 1888;
    String str64b, imgString = "", imgExt, tenant, mDriverID;

    private static final String TAG = "DetailTransActivity";
    private static final String TOKEN = "secretissecret";
    String transId;

    MapView mapView;
    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LatLng mCenterLatLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_detail);
        setTitle("Detil Pemesanan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        itransDet = getIntent();

        // MapView
        mapView = (MapView) findViewById(R.id.detTrans_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                Double lat = Double.parseDouble(itransDet.getStringExtra("latitude"));
                Double lon = Double.parseDouble(itransDet.getStringExtra("longitude"));
                LatLng coordinate = new LatLng(lat,lon);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(coordinate).
                        zoom(12).build();

                googleMap.addMarker(new MarkerOptions().position(coordinate)
                        .title("Lokasi Pemesan"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getCameraPosition();

            }
        });

        controlContent();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void controlContent() {
        mAsetThumb = (ImageView) findViewById(R.id.detTrans_thumb);
        mAset = (TextView) findViewById(R.id.detTrans_aset);
        mPrice = (TextView) findViewById(R.id.detTrans_price);
        mCodeTrans = (TextView) findViewById(R.id.detTrans_codeTrans);
        mMember = (TextView) findViewById(R.id.detTrans_member);
        mStartDate = (TextView) findViewById(R.id.detTrans_startDate);
        mEndDate = (TextView) findViewById(R.id.detTrans_endDate);
        mPicktime = (TextView) findViewById(R.id.detTrans_picktime);
        mAddress = (TextView) findViewById(R.id.detTrans_address);
        mNote = (TextView) findViewById(R.id.detTrans_note);
        mDriver = (TextView) findViewById(R.id.driver);
        mAdditional = (LinearLayout) findViewById(R.id.additional);
        mOrderDate = (TextView) findViewById(R.id.detTrans_orderDate);
        con_add_feature = (LinearLayout) findViewById(R.id.con_add_feature);
        con_voucher = (LinearLayout) findViewById(R.id.detTrans_con_voucher);
        con_insurance = (LinearLayout) findViewById(R.id.detTrans_con_insurance);
        con_additonal = (LinearLayout) findViewById(R.id.detTrans_con_additional_feature);
        withDriverCheck = (ImageView) findViewById(R.id.detTrans_with_driver);
        btnCamera = (ImageButton) findViewById(R.id.btn_camera);
        btnDriver = (Button) findViewById(R.id.btn_assign_driver);

        LinearLayout btnContainer = (LinearLayout) findViewById(R.id.btnContainer);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width))/300.0;
        int height = (int)(ratio*50);
        LinearLayout.LayoutParams endGravity = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        LinearLayout.LayoutParams startGravity = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);

        endGravity.weight = 1.0f;
        endGravity.gravity = Gravity.END;
        startGravity.rightMargin = 10;

        Button btnAction= new  Button(this);
        btnAction.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnAction.setTextColor(getResources().getColor(R.color.colorWhite));
        btnAction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnAction.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Button btnAccept = new  Button(this);
        btnAccept.setText("Terima");
        btnAccept.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnAccept.setTextColor(getResources().getColor(R.color.colorWhite));
        btnAccept.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnAccept.setLayoutParams(endGravity);

        Button btnCancel = new  Button(this);
        btnCancel.setText("Tolak");
        btnCancel.setBackgroundColor(getResources().getColor(R.color.colorButtonDefault));
        btnCancel.setTextColor(getResources().getColor(R.color.colorBlack87));
        btnCancel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnCancel.setLayoutParams(startGravity);

        feature_name = new TextView(this);
        feature_name.setTextSize(13);

        // Set Value
        Picasso.with(getApplicationContext()).load(
                AppConfig.URL_IMAGE_ASSETS + itransDet.getStringExtra("aset_thumb")).into(mAsetThumb);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        transId = itransDet.getStringExtra("id_trans");
        mAset.setText(itransDet.getStringExtra("aset"));
        mPrice.setText(PricingTools.PriceFormat(Integer.parseInt(itransDet.getStringExtra("price"))));
        mCodeTrans.setText(itransDet.getStringExtra("code_trans"));
        mOrderDate.setText(itransDet.getStringExtra("orderDate"));
        mMember.setText(itransDet.getStringExtra("member"));
        mStartDate.setText(itransDet.getStringExtra("startDate"));
        mEndDate.setText(itransDet.getStringExtra("endDate"));
        mPicktime.setText(itransDet.getStringExtra("pickup_time"));
        mAddress.setText(itransDet.getStringExtra("address"));
        mNote.setText(itransDet.getStringExtra("note").equals("null") ? "-" : itransDet.getStringExtra("note"));
        if(itransDet.getStringExtra("insurance").equals("true")) {con_insurance.setVisibility(View.VISIBLE);}

        getAdditionalFeature();

        mMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iMember = new Intent(TransDetailActivity.this, MemberProfileActivity.class);
                iMember.putExtra("id_member", itransDet.getStringExtra("id_member"));
                startActivity(iMember);
            }
        });

        // Get Driver Name
        Log.e(TAG, itransDet.getStringExtra("with_driver"));
        if (itransDet.getStringExtra("status").matches("new|accepted|ongoing|completed")){
            if(itransDet.getStringExtra("with_driver").equals("true")){
                mAdditional.setVisibility(View.VISIBLE);
                mDriver.setVisibility(View.VISIBLE);
                if(itransDet.getStringExtra("status").equals("new")){
                    withDriverCheck.setVisibility(View.VISIBLE);
                }else{
                    if(!itransDet.getStringExtra("driver_name").equals("null")){
                        mDriver.setText("Pengemudi : " + itransDet.getStringExtra("driver_name"));
                    }else  mDriver.setText("Pengemudi : -");
                }

                if(itransDet.getStringExtra("status").equals("accepted")){
                    btnDriver.setVisibility(View.VISIBLE);
                }
            }
        }

        if (itransDet.hasExtra("voucher_code")){
            con_voucher.setVisibility(View.VISIBLE);

            TextView mVoucherCode = (TextView) findViewById(R.id.detTrans_voucher_code);
            TextView mVoucherDisc = (TextView) findViewById(R.id.detTrans_voucher_value);

            mVoucherCode.setText(itransDet.getStringExtra("voucher_code"));
            mVoucherDisc.setText(itransDet.getStringExtra("voucher_disc"));
        }

        // Button Action Configure
        if (itransDet.getStringExtra("status").equals("accepted")){
            btnAction.setText("Berhasil Diantar");
            btnContainer.addView(btnAction);

            btnCamera.setVisibility(View.VISIBLE);

            // Capture button clicks
            btnDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iDriver = new Intent(TransDetailActivity.this, DriverAssignActivity.class);
                    iDriver.putExtra("id_transaction", transId);
                    iDriver.putExtra("start_date", itransDet.getStringExtra("startDate"));
                    iDriver.putExtra("end_date", itransDet.getStringExtra("endDate"));
                    startActivityForResult(iDriver, PICK_DRIVER_REQUEST);
                }

            });

            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imgString.equals("")){
                        Toast.makeText(getApplicationContext(),"Harap masukan bukti aset berhasil diantar", Toast.LENGTH_LONG).show();
                    }else{
                        transAction(transId);
                    }
                }
            });

        } else if(itransDet.getStringExtra("status").equals("ongoing")){
            btnAction.setText("Berhasil Diambil");
            btnContainer.addView(btnAction);

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transAction(transId);
                }
            });

        } else if(itransDet.getStringExtra("status").equals("new")) {
            btnContainer.addView(btnCancel);
            btnContainer.addView(btnAccept);

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transConfirm(transId, "accepted");
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transConfirm(transId, "rejected");
                }
            });
        }

    }

    // Get Additional Feature
    private void getAdditionalFeature() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ITEM_FEATURE , new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray data = new JSONArray(response);
                            Log.e(TAG, "getAdditionalFeature Response : " + response);

                            if(data.length() > 0) {
                                mAdditional.setVisibility(View.VISIBLE);
                                con_additonal.setVisibility(View.VISIBLE);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject feature = data.getJSONObject(i);
                                    feature_name.setText("- " + feature.getString("feature_name"));
                                    con_add_feature.addView(feature_name);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getAdditionalFeature Fetch Error : " + error.toString());
                Toast.makeText(getApplicationContext(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> keys = new HashMap<String, String>();
                keys.put("id_additional_feature", itransDet.getStringExtra("id_additional"));
                Log.e(TAG, "Key Body : " + keys.toString());
                return keys;
            }

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

    private void transConfirm(final String transId, final String status) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        if (status.equals("accepted")){
            new TransDetailActivity.postTransConfirmTask(transId, status).execute();
        } else {
            AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
            showAlert.setMessage("Anda yakin menolak transaksi ?");
            showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    pDialog.setMessage("loading ...");
                    showProgress(true);
                    new TransDetailActivity.postTransConfirmTask(transId, status).execute();
                }
            });
            showAlert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showProgress(false);
                }
            });

            AlertDialog alertDialog = showAlert.create();
            alertDialog.show();
        }
    }

    private void transAction(final String transId) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        if(itransDet.getStringExtra("status").equals("accepted")){
            if (imgString.equals("")){
                Toast.makeText(getApplicationContext(),"Aset berhasil diantar", Toast.LENGTH_LONG).show();
            } else {
                new TransDetailActivity.postTransDropTask(transId).execute();
            }
        } else if (itransDet.getStringExtra("status").equals("ongoing")) {
            new TransDetailActivity.postTransTakeTask(transId).execute();
        }
    }

    //Action Accept or Reject
    private class postTransConfirmTask extends AsyncTask<String, String, String> {
        private final String mTransId, mStatus;
        private String errorMsg, responseTrans;

        private postTransConfirmTask(String transId, String status) {
            mTransId = transId;
            mStatus = status;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_TRANSACTION_CONFIRM + mTransId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseTrans = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Transaction Confirm Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", tenant);
                    keys.put("status", mStatus);

                    Log.e(TAG, "Post Data : ID = "+ mTransId + "|" + String.valueOf(keys));
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

            return responseTrans;
        }

        @Override
        protected void onPostExecute(String transaction) {
            mTransactionTask = null;
            showProgress(false);

            if(transaction != null){
                Toast.makeText(getApplicationContext(),"Transaksi berhasil diterima, Periksa menu transaksi anda", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(TransDetailActivity.this,TransactionaNewActivity.class);
                setResult(RESULT_OK, intent);
                TransDetailActivity.this.finish();
            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mTransactionTask = null;
            showProgress(false);
        }
    }

    //Action to Ongoing Trans
    private class postTransDropTask extends AsyncTask<String, String, String> {
        private final String mTransId;
        private String errorMsg, responseTrans;

        private postTransDropTask(String transId) {
            mTransId = transId;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_TRANSACTION_DROP + mTransId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseTrans = response;

                    Log.e(TAG, "Transaction Drop Response : " + responseTrans);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Transaction Drop Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("file", imgString);

                    Log.e(TAG, "Post Data : ID = "+ mTransId + String.valueOf(keys));
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

            return responseTrans;
        }

        @Override
        protected void onPostExecute(String response) {
            mTransactionTask = null;
            String msg = "";
            showProgress(false);

            if(response != null){
                try {
                    JSONObject responseObject = new JSONObject(response);
                    msg = responseObject.getString("status").equals("failed") ? responseObject.getString("data") : "Aset berhasil diantar";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }else{
                msg = "gagal melakukan aksi";
            }

            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onCancelled() {
            mTransactionTask = null;
            showProgress(false);
        }
    }

    //Action to Completed Trans
    private class postTransTakeTask extends AsyncTask<String, String, String> {
        private final String mTransId;
        private String errorMsg, responseTrans;

        private postTransTakeTask(String transId) {
            mTransId = transId;
        }

        @Override
        protected String doInBackground(String... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_TRANSACTION_TAKE + mTransId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseTrans = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Transaction Take Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();

                    Log.e(TAG, "Post Data Take : ID = "+ mTransId);
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

            return responseTrans;
        }

        @Override
        protected void onPostExecute(String trans) {
            mTransactionTask = null;
            showProgress(false);

            if(trans != null){
                try {
                    JSONArray jsonArray = new JSONArray(trans);
                    JSONObject transObject = jsonArray.getJSONObject(0);
                    String tenant, member, trans_det;

                    tenant = transObject.getString("id_tenant");
                    member = transObject.getString("id_member");
                    trans_det = transObject.getString("id");

                    Toast.makeText(getApplicationContext(),"Transaksi Selesai", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TransDetailActivity.this,TestimonyAddActivity.class);
                    intent.putExtra("id_tenant", tenant);
                    intent.putExtra("id", trans_det);
                    intent.putExtra("id_member", member);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e(TAG, "PTrans Data = "+ trans);



            }else{
                Toast.makeText(getApplicationContext(),"Gagal meyimpan data", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mTransactionTask = null;
            showProgress(false);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            str64b = getStringImage(photo);
            imgString = "image/jpeg," + str64b;

            btnCamera.setImageBitmap(photo);

            Log.e(TAG, "Image : " + imgString);
        }

        if (requestCode == PICK_DRIVER_REQUEST && resultCode == Activity.RESULT_OK) {
            mDriverID = data.getStringExtra("id_driver");
            String driver_name = "Pengemudi : " + data.getStringExtra("driver_name");
            mDriver.setText(driver_name);
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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

}

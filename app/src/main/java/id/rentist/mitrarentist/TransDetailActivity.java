package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.fragment.DriverDialogFragment;
import id.rentist.mitrarentist.tools.AppConfig;

public class TransDetailActivity extends AppCompatActivity {
    private AsyncTask mTransactionTask = null;
    private ProgressDialog pDialog;

    Button btnClosePopup;
    Button btnCreatePopup;
    ImageButton btnCamera;
    private PopupWindow pwindow;
    private Intent itransDet;

    TextView mAset, mPrice, mCodeTrans, mMember, mStartDate, mEndDate;

    private static final int CAMERA_REQUEST = 1888;
    String str64b, imgString, imgExt;

    private static final String TAG = "DetailTransActivity";
    private static final String TOKEN = "secretissecret";
    String transId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_detail);
        setTitle("Detil Pemesanan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        itransDet = getIntent();

        controlContent();
    }

    private void controlContent() {
        mAset = (TextView) findViewById(R.id.detTrans_aset);
        mPrice = (TextView) findViewById(R.id.detTrans_price);
        mCodeTrans = (TextView) findViewById(R.id.detTrans_codeTrans);
        mMember = (TextView) findViewById(R.id.detTrans_member);
        mStartDate = (TextView) findViewById(R.id.detTrans_startDate);
        mEndDate = (TextView) findViewById(R.id.detTrans_endDate);

        LinearLayout btnContainer = (LinearLayout) findViewById(R.id.btnContainer);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width))/300.0;
        int height = (int)(ratio*50);

        Button btnAction= new  Button(this);
        btnAction.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnAction.setTextColor(getResources().getColor(R.color.colorWhite));
        btnAction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnAction.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,height));

        Button btnAccept = new  Button(this);
        btnAccept.setText("Terima");
        btnAccept.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnAccept.setTextColor(getResources().getColor(R.color.colorWhite));
        btnAccept.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnAccept.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,height));

        Button btnCancel = new  Button(this);
        btnCancel.setText("Tolak");
        btnCancel.setBackgroundColor(getResources().getColor(R.color.colorButtonDefault));
        btnCancel.setTextColor(getResources().getColor(R.color.colorBlack87));
        btnCancel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnCancel.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,height));

        // Value
        transId = itransDet.getStringExtra("id_trans");
        mAset.setText(itransDet.getStringExtra("aset"));
        mPrice.setText(itransDet.getStringExtra("price"));
        mCodeTrans.setText(itransDet.getStringExtra("code_trans"));
        mMember.setText(itransDet.getStringExtra("member"));
        mStartDate.setText(itransDet.getStringExtra("startDate"));
        mEndDate.setText(itransDet.getStringExtra("endDate"));

        // Button Action Configure
        if (itransDet.getStringExtra("status").equals("accepted")){
            btnAction.setText("Berhasil Diantar");
            btnContainer.addView(btnAction);

            btnCamera = (ImageButton) findViewById(R.id.btn_camera);
            btnCreatePopup = (Button) findViewById(R.id.btn_assign_driver);
            btnCamera.setVisibility(View.VISIBLE);
            btnCreatePopup.setVisibility(View.VISIBLE);

            // Capture button clicks
            btnCreatePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DriverDialogFragment dFrag = new DriverDialogFragment();
                    dFrag.show(getSupportFragmentManager(), "Driver");
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
                    transAction(transId);
                }
            });

        } else if(itransDet.getStringExtra("status").equals("ongoing")){
            btnAction.setText("Berhasil Dijemput");
            btnContainer.addView(btnAction);

        } else {
            btnContainer.addView(btnCancel);
            btnContainer.addView(btnAccept);
        }

    }

    private void transAction(String transId) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        if(itransDet.getStringExtra("status").equals("accepted")){
            new TransDetailActivity.postTransDropTask(transId).execute();
        }
    }

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
        protected void onPostExecute(String voucher) {
            mTransactionTask = null;
            showProgress(false);

            if(voucher != null){
                Toast.makeText(getApplicationContext(),"Data sukses disimpan", Toast.LENGTH_LONG).show();
                finish();
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

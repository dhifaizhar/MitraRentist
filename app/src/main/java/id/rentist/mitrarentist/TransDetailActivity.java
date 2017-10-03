package id.rentist.mitrarentist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;

import id.rentist.mitrarentist.fragment.DriverDialogFragment;

public class TransDetailActivity extends AppCompatActivity {

    Button btnClosePopup;
    Button btnCreatePopup;
    ImageButton btnCamera;
    private PopupWindow pwindow;
    private Intent itransDet;

    TextView mAset, mPrice, mIdTrans, mMember, mStartDate, mEndDate;

    private static final int CAMERA_REQUEST = 1888;
    String str64b, imgString, imgExt;

    private static final String TAG = "DetailTransActivity";
    private static final String TOKEN = "secretissecret";
    String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_detail);
        setTitle("Detil Pemesanan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        itransDet = getIntent();

        controlContent();
    }

    private void controlContent() {
        mAset = (TextView) findViewById(R.id.detTrans_aset);
        mPrice = (TextView) findViewById(R.id.detTrans_price);
        mIdTrans = (TextView) findViewById(R.id.detTrans_idTrans);
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
        mAset.setText(itransDet.getStringExtra("aset"));
        mPrice.setText(itransDet.getStringExtra("price"));
        mIdTrans.setText(itransDet.getStringExtra("id_trans"));
        mMember.setText(itransDet.getStringExtra("member"));
        mStartDate.setText(itransDet.getStringExtra("startDate"));
        mEndDate.setText(itransDet.getStringExtra("endDate"));

        // Button Action Configure
        if (itransDet.getStringExtra("status").equals("onGoing")){
            btnAction.setText("Berhasil Dijemput");
            btnContainer.addView(btnAction);
        } else if(itransDet.getStringExtra("status").equals("accepted")){
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
                    Intent iDetTrans = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(iDetTrans);
                    Toast.makeText(getApplicationContext()," Aset Berhasil diantar", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            btnContainer.addView(btnCancel);
            btnContainer.addView(btnAccept);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

//            String imgStr = data.toString();
//            imgExt = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);
            str64b = getStringImage(photo);

            btnCamera.setImageBitmap(photo);

//            imgString = imgExt +"," + str64b;
            Log.e(TAG, "Image : " + str64b);


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

}

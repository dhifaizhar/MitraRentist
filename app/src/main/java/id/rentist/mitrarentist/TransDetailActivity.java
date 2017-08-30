package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import id.rentist.mitrarentist.fragment.DriverDialogFragment;

public class TransDetailActivity extends AppCompatActivity {

    Button btnClosePopup;
    Button btnCreatePopup;
    private PopupWindow pwindow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_detail);
        setTitle("Detil Pemesanan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnCreatePopup = (Button) findViewById(R.id.btn_assign_driver);
        // Capture button clicks
        btnCreatePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext()," Pilih Driver", Toast.LENGTH_LONG).show();
                DriverDialogFragment dFrag = new DriverDialogFragment();
                dFrag.show(getSupportFragmentManager(), "Driver");
            }

        });

        Button btnAccept = (Button) findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDetTrans = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(iDetTrans);
                Toast.makeText(getApplicationContext()," Aset Berhasil diantar", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

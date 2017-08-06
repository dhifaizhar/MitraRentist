package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class AktivasiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktivasi);
        setTitle("Aktivasi");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button aktif_btn = (Button) findViewById(R.id.aktif_button);
        setSupportActionBar(toolbar);

        aktif_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AktivasiActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        //
    }
}

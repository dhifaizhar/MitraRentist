package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.modul.DompetModul;

public class DompetActivity extends AppCompatActivity {
    private List<DompetModul> mDompet = new ArrayList<>();
    TextView credit,tunai;
    Button withdrawal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dompet);
        setTitle("Dompet");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        controlContent();
    }

    private void controlContent() {
        //initialize view
        credit = (TextView)findViewById(R.id.dm_credit);
        tunai = (TextView)findViewById(R.id.dm_tunai);
        withdrawal = (Button)findViewById(R.id.dm_btn_drawal);

        // set content control value
        credit.setText("7.000.000 IDR");
        tunai.setText("0 IDR");
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iWithdrawal = new Intent(v.getContext(), WithdrawalActivity.class);
                startActivity(iWithdrawal);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

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
}

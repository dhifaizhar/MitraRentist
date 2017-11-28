package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ComplainDetailActivity extends AppCompatActivity {
    private Intent complainDet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        complainDet = getIntent();

        TextView memberName = (TextView) findViewById(R.id.cd_member);
        TextView content = (TextView) findViewById(R.id.cd_content);

        setTitle(complainDet.getStringExtra("date"));
        memberName.setText(complainDet.getStringExtra("member"));
        content.setText(complainDet.getStringExtra("content"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }

}

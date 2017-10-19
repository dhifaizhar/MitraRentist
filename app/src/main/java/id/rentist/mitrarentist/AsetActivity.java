package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import id.rentist.mitrarentist.adapter.GridAsetAdapter;
import id.rentist.mitrarentist.tools.SessionManager;

public class AsetActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private SessionManager sm;

    CardView addAset;
    String tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aset);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Kategori Aset");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sm = new SessionManager(getApplicationContext());
        addAset = (CardView) findViewById(R.id.add_aset);

        Log.e("ASET","SumCat : " + sm.getIntPreferences("sum_cat").toString());
        if (sm.getIntPreferences("sum_cat")< 1){
            addAset.setVisibility(View.VISIBLE);
            addAset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iSetup = new Intent(AsetActivity.this, SetupCategoryActivity.class);
                    startActivityForResult(iSetup, 2);
                }
            });
        }else{
            getGridAset();
        }

    }

    public void getGridAset() {
        mRecyclerView = (RecyclerView) findViewById(R.id.ac_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        mAdapter = new GridAsetAdapter(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preference_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_preference) {
            Intent iSetup = new Intent(AsetActivity.this, SetupCategoryActivity.class);
            startActivityForResult(iSetup, 2);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (sm.getIntPreferences("sum_cat")<1){
                addAset.setVisibility(View.VISIBLE);
                getGridAset();
                addAset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iSetup = new Intent(AsetActivity.this, SetupCategoryActivity.class);
                        startActivityForResult(iSetup, 2);
                    }
                });
            }else{
                addAset.setVisibility(View.GONE);
                getGridAset();
            }
        }

    }

}

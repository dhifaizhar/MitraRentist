package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import id.rentist.mitrarentist.adapter.DetailAsetAdapter;
import id.rentist.mitrarentist.modul.ItemAsetModul;


public class DetailAsetActivity extends AppCompatActivity {
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<ItemAsetModul> mDetail = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);
        setTitle("Daihatsu Jazz | DC 123 WOW");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail_aset);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.det_aset_recyclerView);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new DetailAsetAdapter(mDetail);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_det_aset);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent iAsetEdit = new Intent(DetailAsetActivity.this, FormEditAsetActivity.class);
                startActivity(iAsetEdit);
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }

}

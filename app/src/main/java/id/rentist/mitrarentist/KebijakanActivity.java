package id.rentist.mitrarentist;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import id.rentist.mitrarentist.Tab.SlidingTabLayout;
import id.rentist.mitrarentist.Tab.KebijakanTabAdapter;

public class KebijakanActivity extends AppCompatActivity {
    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kebijakan);
        setTitle("Kebijakan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        KebijakanTabAdapter fragKebijakan = new KebijakanTabAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.kbj_tabs);
        mViewPager.setAdapter(fragKebijakan);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tab_kbj);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorWhite));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_help_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_help) {
//
//            //ubah dengan fungsi
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import id.rentist.mitrarentist.Tab.SlidingTabLayout;
import id.rentist.mitrarentist.Tab.TransactionTabAdapter;
import id.rentist.mitrarentist.fragment.TransactionAcceptFragment;
import id.rentist.mitrarentist.tools.SessionManager;

public class TransactionActivity extends AppCompatActivity {
    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;
    TabLayout mTabLayout;
    private AsyncTask mTransactionTask = null;
    private SessionManager sm;
    private SpinKitView pBar;
    String tenant;

    private static final String TAG = "TransactionActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        setTitle("Transaksi");

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView) findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TransactionTabAdapter fragTrans = new TransactionTabAdapter(getSupportFragmentManager(),this);
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(fragTrans);

        mTabLayout = (TabLayout) findViewById(R.id.stl_tab_hs);
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorWhite));
        mTabLayout.setupWithViewPager(mViewPager);

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));

        Bundle bundle = new Bundle();
        bundle.putString("tes", "data From Activity");
        // set Fragmentclass Arguments
        TransactionAcceptFragment fragobj = new TransactionAcceptFragment();
        fragobj.setArguments(bundle);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_refresh_option, menu);
//        getMenuInflater().inflate(R.menu.menu_search_option, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //ubah dengan fungsi
            return true;
        }

        if (id == R.id.action_refresh) {
            //ubah dengan fungsi
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Intent intent = new Intent(TransactionActivity.this,DashboardActivity.class);
        setResult(RESULT_OK, intent);
        this.finish();

        return true;
    }
}

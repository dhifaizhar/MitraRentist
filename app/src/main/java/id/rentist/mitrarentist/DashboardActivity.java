package id.rentist.mitrarentist;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private AsyncTask mDashboardTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private View navHeaderView;

    String tenant, img, encodedImage;
//    URL encodedImage;
    Integer sumAsset, aCar, aBike, aYacht;
    TextView totAsset, totPoin, totRating, totSaldo, rentName, rentNameDrawer, successRent, ongoRent;
    ImageView rentImgProfile;
    ImageButton btnNewTrans, btnToSaldo, btnWorkDate;

    private static final String TAG = "DashboardActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_dashboard);

        sm = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeaderView = navigationView.getHeaderView(0);

        controlContent();
        Toast.makeText(getApplicationContext(),"Selamat Datang Mitra : " + sm.getPreferences("nama"), Toast.LENGTH_LONG).show();
    }

    private void controlContent() {
        //initialize view
        rentName = (TextView) findViewById(R.id.rentName);
        rentNameDrawer = (TextView) navHeaderView.findViewById(R.id.navRentName);
        rentImgProfile = (ImageView) navHeaderView.findViewById(R.id.navImageProfile);
        btnNewTrans = (ImageButton) findViewById(R.id.btn_to_det_new_trans);
        btnToSaldo = (ImageButton) findViewById(R.id.btn_to_saldo);
        btnWorkDate = (ImageButton) findViewById(R.id.btn_work_date);
        totSaldo = (TextView) findViewById(R.id.val_saldo);
        totAsset = (TextView) findViewById(R.id.val_sum_asset);
        totPoin = (TextView) findViewById(R.id.val_poin);
        totRating = (TextView) findViewById(R.id.val_rating);
        successRent = (TextView) findViewById(R.id.val_success_rent);
        ongoRent = (TextView) findViewById(R.id.val_ongo_rent);

        // set content control value
        rentName.setText(sm.getPreferences("nama_rental"));
        rentNameDrawer.setText(sm.getPreferences("nama"));

//      rentImgProfile.setImageResource(sm.getIntPreferences("foto_profil"));
        encodedImage = sm.getPreferences("foto_profil");
//      URL encodedImage =  new URL("assets.rentist.id/images/");
//      Bitmap bmp = BitmapFactory.decodeStream(encodedImage.openConnection().getInputStream());

        if (encodedImage.equals("null")){
            rentImgProfile.setImageResource(R.drawable.user_ava_man);
        } else {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            rentImgProfile.setImageBitmap(decodedByte);
//          rentImgProfile.setImageResource(bmp);
        }
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        retrieveDashboardData(tenant);
        btnNewTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTrans = new Intent(DashboardActivity.this, TransactionActivity.class);
                iTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iTrans);
            }
        });
        btnToSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSaldo = new Intent(DashboardActivity.this, DompetActivity.class);
                iSaldo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iSaldo);
            }
        });
        btnWorkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iWork = new Intent(DashboardActivity.this, WorkDateActivity.class);
                iWork.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iWork);
            }
        });
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private void retrieveDashboardData(String tenant) {
        pDialog.setMessage("loading ...");
        showProgress(true);
        new getDataTask(tenant).execute();
    }

    private class getDataTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private String errorMsg, responseData;

        private getDataTask(String tenant) {
            mTenant = tenant;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DASHBOARD_DATA + mTenant;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseData = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Dashboard Data Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
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

            return responseData;
        }

        @Override
        protected void onPostExecute(String user) {
            mDashboardTask = null;
            showProgress(false);

            if(user != null){
                try {
                    JSONObject dataObject = new JSONObject(user);
                    JSONObject assetObject = new JSONObject(String.valueOf(dataObject.getJSONObject("asset")));
                    JSONObject saldoObject = new JSONObject(String.valueOf(dataObject.getJSONObject("saldo")));
                    JSONObject poinObject = new JSONObject(String.valueOf(dataObject.getJSONObject("poin")));
                    JSONObject ratingObject = new JSONObject(String.valueOf(dataObject.getJSONObject("rating")));
                    Log.d(TAG, String.valueOf(dataObject));

                    aCar = assetObject.getInt("mobil");
                    aBike = assetObject.getInt("motor");
                    aYacht = assetObject.getInt("yacht");
                    sumAsset = aCar + aBike + aYacht;
                    totAsset.setText(String.valueOf(sumAsset));
                    totSaldo.setText(saldoObject.getString("received").equals("null") ? "0 IDR" : saldoObject.getString("received")+" IDR");
                    totPoin.setText(poinObject.getString("received"));
                    totRating.setText(ratingObject.getString("rating").equals("null") ? "0" : ratingObject.getString("rating"));
                    successRent.setText(dataObject.getString("sukses"));
                    ongoRent.setText(dataObject.getString("berlangsung"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mDashboardTask = null;
            showProgress(false);
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        getMenuInflater().inflate(R.menu.menu_refresh_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
        }

        if (id == R.id.action_refresh) {
            retrieveDashboardData(tenant);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_aset) {
            Intent iAset = new Intent(DashboardActivity.this, AsetActivity.class);
            startActivity(iAset);
        } else if (id == R.id.nav_users) {
            Intent iPegawai = new Intent(DashboardActivity.this, UsersActivity.class);
            startActivity(iPegawai);
        } else if(id == R.id.nav_driver){
            Intent iDriver = new Intent(DashboardActivity.this, DriverActivity.class);
            startActivity(iDriver);
//        } else if(id == R.id.nav_drop){
//            Intent iDrop = new Intent(DashboardActivity.this, DropAsetActivity.class);
//            startActivity(iDrop);
//        } else if(id == R.id.nav_take){
//            Intent iTake = new Intent(DashboardActivity.this, DropAsetActivity.class);
//            startActivity(iTake);
        } else if (id == R.id.nav_dompet) {
            Intent iDompet = new Intent(DashboardActivity.this, DompetActivity.class);
            startActivity(iDompet);
        } else if (id == R.id.nav_riwayat) {
            Intent iRiwayat = new Intent(DashboardActivity.this, HistoryActivity.class);
            startActivity(iRiwayat);
        } else if (id == R.id.nav_message) {
            Intent iMessage = new Intent(DashboardActivity.this, MessageListActivity.class);
            startActivity(iMessage);
        } else if (id == R.id.nav_voucher) {
            Intent iVoucher = new Intent(DashboardActivity.this, VoucherActivity.class);
            startActivity(iVoucher);
        } else if (id == R.id.nav_pengaturan) {
            Intent iPengaturan = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(iPengaturan);
        } else if (id == R.id.nav_kebijakan) {
            Intent iKebijakan = new Intent(DashboardActivity.this, KebijakanActivity.class);
            startActivity(iKebijakan);
        } else if (id == R.id.nav_pengaduan) {
            Intent iKeluh = new Intent(DashboardActivity.this, ComplainActivity.class);
            startActivity(iKeluh);
        } else if (id == R.id.nav_critic) {
            Intent i = new Intent(DashboardActivity.this, Sample.class);
            startActivity(i);
        }else if (id == R.id.nav_panduan) {
            Intent iPanduan = new Intent(this, PanduanActivity.class);
            startActivity(iPanduan);
        } else if (id == R.id.nav_tentang) {
            Intent iTentang = new Intent(this, TentangKamiActivity.class);
            startActivity(iTentang);
        } else if (id == R.id.nav_profile){
            Intent iProfile = new Intent(this, ProfileActivity.class);
            startActivity(iProfile);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(){
        final Intent iLogin = new Intent(this, LoginActivity.class);
        AlertDialog.Builder showAlert = new AlertDialog.Builder(this);
        showAlert.setMessage("Akhiri sesi pengguna aplikasi ?");
        showAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                sm.clearPreferences();
                startActivity(iLogin);
                finish();
            }
        });
        showAlert.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });

        AlertDialog alertDialog = showAlert.create();
        alertDialog.show();
    }
}

package id.rentist.mitrarentist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.CircleTransform;
import id.rentist.mitrarentist.tools.PricingTools;
import id.rentist.mitrarentist.tools.SessionManager;
import id.rentist.mitrarentist.tools.Tools;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private AsyncTask mDashboardTask = null;
    private ProgressDialog pDialog;
    private SpinKitView pBar;
    private SessionManager sm;
    private View navHeaderView;
    private Menu navMenuView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 1;

    //    URL imageUrl;
    ImageLoader mImageLoader;

    String tenant, mUsername, mPhotoUrl, encodedImage, imageUrl, imgString, rClean, rNeat, rHonest, rCom;
    Integer sumAsset, aCar, aBike, aYacht;
    TextView newTrans, totAsset, totPoin, totRating, totSaldo, rentName, role, rentNameDrawer,
            successRent, ongoRent, toFormAccount, ratCleanliness, ratNeatness, ratHonesty, ratComunication;
    ImageView rentImgProfile, verifIco;
    LinearLayout accountDataNotif, accountDataVerifNotif, conRating, conAset, rlPoin;
//    NetworkImageView rentImgProfile;
    ImageButton btnNewTrans, btnToSaldo, btnWorkDate, btnEditProfpic;
    DrawerLayout drawer;
    RelativeLayout rlNewTrans, rlSaldo;//,rlPoin ;

    private static final String TAG = "DashboardActivity";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_dashboard);

        sm = new SessionManager(getApplicationContext());
        pBar = (SpinKitView)findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeaderView = navigationView.getHeaderView(0);
        navMenuView = navigationView.getMenu();
        setMenuAccess();

        try {
            controlContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(),"Selamat Datang Mitra : " + sm.getPreferences("nama"), Toast.LENGTH_LONG).show();
    }

    private void controlContent() throws IOException {
        //initialize view
        accountDataNotif = (LinearLayout) findViewById(R.id.account_data_notif);
        accountDataVerifNotif = (LinearLayout) findViewById(R.id.account_data_verif_notif);
        rlNewTrans = (RelativeLayout) findViewById(R.id.rl_new_trans);
        rlSaldo = (RelativeLayout) findViewById(R.id.rl_rev_saldo);
        rlPoin = (LinearLayout) findViewById(R.id.rl_rev_poin);
        rentName = (TextView) findViewById(R.id.rentName);
        rentNameDrawer = (TextView) navHeaderView.findViewById(R.id.navRentName);
        rentImgProfile = (ImageView) navHeaderView.findViewById(R.id.navImageProfile);
        role = (TextView) navHeaderView.findViewById(R.id.navRole);
        verifIco = (ImageView) findViewById(R.id.navRentVerif);
        btnNewTrans = (ImageButton) findViewById(R.id.btn_to_det_new_trans);
        btnToSaldo = (ImageButton) findViewById(R.id.btn_to_saldo);
        btnWorkDate = (ImageButton) findViewById(R.id.btn_work_date);
        btnEditProfpic = (ImageButton) navHeaderView.findViewById(R.id.btn_prof_pic);
        newTrans = (TextView) findViewById(R.id.val_new_trans);
        totSaldo = (TextView) findViewById(R.id.val_saldo);
        totAsset = (TextView) findViewById(R.id.val_sum_asset);
        totPoin = (TextView) findViewById(R.id.val_poin);
        totRating = (TextView) findViewById(R.id.val_sum_rating);
        successRent = (TextView) findViewById(R.id.val_success_rent);
        ongoRent = (TextView) findViewById(R.id.val_ongo_rent);
        toFormAccount = (TextView) findViewById(R.id.toFormProfile);

        conRating = (LinearLayout) findViewById(R.id.con_rating);
        conAset = (LinearLayout) findViewById(R.id.con_aset);

        // set content control value
        Log.e(TAG, "Tenant Data : " + sm.getPreferences("nama_pemilik") + "|" +  sm.getPreferences("nama_rental") + "|" +  sm.getPreferences("nama") + "|" +
                sm.getPreferences("alamat") + "|" +  sm.getPreferences("telepon") + "|" +  sm.getPreferences("email") + "|" +  sm.getIntPreferences("city") + "|" +
                sm.getPreferences("bank_name") + "|" +  sm.getPreferences("bank_account") + "|" +  sm.getPreferences("branch") + "|" +  sm.getPreferences("account_name"));

        if(!sm.getPreferences("nama_pemilik").isEmpty() &&
                !sm.getPreferences("nama_rental").isEmpty() &&
                !sm.getPreferences("nama").isEmpty() &&
                !sm.getPreferences("alamat").isEmpty() &&
                !sm.getPreferences("telepon").isEmpty() &&
                !sm.getPreferences("email").isEmpty() &&
                !String.valueOf(sm.getIntPreferences("city")).isEmpty() &&
                !sm.getPreferences("bank_name").isEmpty() &&
                !sm.getPreferences("bank_account").isEmpty() &&
                !sm.getPreferences("branch").isEmpty() &&
                !sm.getPreferences("account_name").isEmpty()){
            accountDataNotif.setVisibility(View.GONE);
            if (sm.getPreferences("verified").equals("false")) {
                accountDataVerifNotif.setVisibility(View.VISIBLE);
            }
        }

        //Role Show/Hide
        toFormAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sm.getPreferences("role").equals("SuperAdmin")){
                    Intent iEditRent = new Intent(DashboardActivity.this, FormEditProfilActivity.class);
                    startActivityForResult(iEditRent, 2);
                }else{
                    Toast.makeText(getApplicationContext(), "Hanya untuk Administrator", Toast.LENGTH_LONG).show();
                }
            }
        });

        rentName.setText(sm.getPreferences("nama_rental"));
        rentNameDrawer.setText(sm.getPreferences("nama"));

        role.setText(sm.getPreferences("role"));
        switch (sm.getPreferences("role")) {
            case "SuperAdmin":
                role.setBackgroundColor(getResources().getColor(R.color.roleSuperadmin));
                break;
            case "Admin":
                role.setBackgroundColor(getResources().getColor(R.color.roleAdmin));
                break;
            case "Operation":
                role.setBackgroundColor(getResources().getColor(R.color.roleOperation));
                rlSaldo.setVisibility(View.GONE);
                break;
            case "Finance":
                role.setBackgroundColor(getResources().getColor(R.color.roleFinance));
                rlNewTrans.setVisibility(View.GONE);
                break;
            case "Delivery":
                rlNewTrans.setVisibility(View.GONE);
                rlSaldo.setVisibility(View.GONE);
                break;
        }

        if (sm.getPreferences("verified").equals("false")){
            verifIco.setVisibility(View.GONE);
        }

        Log.e(TAG, "Profil Pic : " + sm.getPreferences("foto_profil") + ", CITY : " +  sm.getIntPreferences("city") );
        if (sm.getPreferences("foto_profil").equals("null")){
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + "default.png";
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(rentImgProfile);
        } else {
            String imageUrl = AppConfig.URL_IMAGE_PROFIL + sm.getPreferences("foto_profil");
            Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(rentImgProfile);

        }

        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
//        retrieveDashboardData(tenant);
        getDashboardData();

        rlNewTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTrans = new Intent(DashboardActivity.this, TransactionaNewActivity.class);
                iTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iTrans);
            }
        });
        btnNewTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTrans = new Intent(DashboardActivity.this, TransactionaNewActivity.class);
                iTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iTrans);
            }
        });

        if (sm.getPreferences("role").matches("SuperAdmin|Admin|Finance")){
            rlSaldo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iTrans = new Intent(DashboardActivity.this, DompetActivity.class);
                    iTrans.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(iTrans);
                }
            });

            rlPoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iTrans = new Intent(DashboardActivity.this, VoucherCatalogActivity.class);
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
        }

        btnWorkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iWork = new Intent(DashboardActivity.this, WorkDateActivity.class);
                iWork.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iWork);
            }
        });

        btnEditProfpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                PickSetup setup = Tools.imagePickerSetup();
                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        r.getBitmap();
                        r.getError();
                        r.getUri();
                        imgString = Tools.getStringImageView(r.getBitmap(), rentImgProfile);
                        Log.e("onPickResult: ",imgString );
                        new updatePhotoProfileTask(tenant, String.valueOf(sm.getIntPreferences("id"))).execute();
                    }
                }).show(DashboardActivity.this);
            }
        });

        conRating.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // custom dialog
                final Dialog dialog = new Dialog(DashboardActivity.this);
                dialog.setContentView(R.layout.dialog_rating_detail);
                dialog.setTitle("Rating");

                // set the custom dialog components - text, image and button
                ratCleanliness = (TextView) dialog.findViewById(R.id.cleanliness_rating);
                ratNeatness= (TextView) dialog.findViewById(R.id.neatness_rating);
                ratHonesty = (TextView) dialog.findViewById(R.id.honesty_rating);
                ratComunication = (TextView) dialog.findViewById(R.id.comunication_rating);

                ratCleanliness.setText(rClean);
                ratNeatness.setText(rNeat);
                ratHonesty.setText(rHonest);
                ratComunication.setText(rCom);

                dialog.show();
            }
        });

        conAset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAset = new Intent(DashboardActivity.this, AsetActivity.class);
                iAset.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iAset);
            }
        });
    }

    private void setMenuAccess(){
        navMenuView.findItem(R.id.nav_pengaturan).setVisible(false);
        navMenuView.findItem(R.id.nav_users).setVisible(false);
        navMenuView.findItem(R.id.nav_aset).setVisible(false);
        navMenuView.findItem(R.id.nav_feature).setVisible(false);
        navMenuView.findItem(R.id.nav_driver).setVisible(false);
        navMenuView.findItem(R.id.nav_riwayat).setVisible(false);
        navMenuView.findItem(R.id.nav_dompet).setVisible(false);
        navMenuView.findItem(R.id.nav_voucher).setVisible(false);
        navMenuView.findItem(R.id.nav_kebijakan).setVisible(false);
        navMenuView.findItem(R.id.nav_message).setVisible(false);

        if(sm.getPreferences("role").equals("SuperAdmin")){
            navMenuView.findItem(R.id.nav_users).setVisible(true);
            navMenuView.findItem(R.id.nav_aset).setVisible(true);
            navMenuView.findItem(R.id.nav_feature).setVisible(true);
            navMenuView.findItem(R.id.nav_driver).setVisible(true);
            navMenuView.findItem(R.id.nav_riwayat).setVisible(true);
            navMenuView.findItem(R.id.nav_dompet).setVisible(true);
            navMenuView.findItem(R.id.nav_voucher).setVisible(true);
            navMenuView.findItem(R.id.nav_kebijakan).setVisible(true);
            navMenuView.findItem(R.id.nav_message).setVisible(true);
        }else if(sm.getPreferences("role").equals("Admin")){
            navMenuView.findItem(R.id.nav_users).setVisible(true);
            navMenuView.findItem(R.id.nav_aset).setVisible(true);
            navMenuView.findItem(R.id.nav_feature).setVisible(true);
            navMenuView.findItem(R.id.nav_driver).setVisible(true);
            navMenuView.findItem(R.id.nav_riwayat).setVisible(true);
            navMenuView.findItem(R.id.nav_dompet).setVisible(true);
            navMenuView.findItem(R.id.nav_voucher).setVisible(true);
            navMenuView.findItem(R.id.nav_kebijakan).setVisible(true);
        }else if(sm.getPreferences("role").equals("Operation")){
            navMenuView.findItem(R.id.nav_aset).setVisible(true);
            navMenuView.findItem(R.id.nav_feature).setVisible(true);
            navMenuView.findItem(R.id.nav_driver).setVisible(true);
            navMenuView.findItem(R.id.nav_riwayat).setVisible(true);
        }else if(sm.getPreferences("role").equals("Finance")){
            navMenuView.findItem(R.id.nav_dompet).setVisible(true);
            navMenuView.findItem(R.id.nav_riwayat).setVisible(true);
        }else if(sm.getPreferences("role").equals("Delivery")){
            navMenuView.findItem(R.id.nav_riwayat).setVisible(true);
        }
    }

    public void onRestart() {
        super.onRestart();
        getDashboardData();
    }

    private void getDashboardData() {
        pBar.setVisibility(View.VISIBLE);
        String URL = AppConfig.URL_DASHBOARD_DATA + tenant;
        Log.d(TAG, "Request to : "+ URL);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseDashboardData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Dashboard Data Fetch Error : " + error.toString());
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

        queue.add(strReq);
    }

    private void parseDashboardData(String response) {
        pBar.setVisibility(View.GONE);

        if(response != null){
            if(response.equals("Could not find Tenant")){
                Intent iLogin = new Intent(this, LoginActivity.class);
                startActivity(iLogin);
                sm.clearPreferences();
                Toast.makeText(getApplicationContext(),"Akun Tidak Ditemukan", Toast.LENGTH_LONG).show();
                finish();
            }else {
                try {
                    JSONObject dataObject = new JSONObject(response);
                    JSONObject assetObject = new JSONObject(String.valueOf(dataObject.getJSONObject("asset")));
                    JSONObject poinObject = new JSONObject(String.valueOf(dataObject.getJSONObject("poin")));
                    JSONObject ratingObject = new JSONObject(String.valueOf(dataObject.getJSONObject("rating")));
                    Log.d(TAG, "Response : " + dataObject.toString());

                    sumAsset = assetObject.getInt("total");
                    newTrans.setText(dataObject.getString("paid"));
                    totAsset.setText(String.valueOf(sumAsset));
                    totPoin.setText(poinObject.getString("received"));//.equals("null") ? "0" : ratingObject.getString("received"));
                    successRent.setText(String.valueOf(dataObject.getInt("sukses")));
                    ongoRent.setText(dataObject.getString("berlangsung"));

                    sm.setPreferences("verified", dataObject.getString("verified"));
                    if (sm.getPreferences("verified").equals("false")) {
                        if (!sm.getPreferences("nama_pemilik").isEmpty() &&
                                !sm.getPreferences("nama_rental").isEmpty() &&
                                !sm.getPreferences("nama").isEmpty() &&
                                !sm.getPreferences("alamat").isEmpty() &&
                                !sm.getPreferences("telepon").isEmpty() &&
                                !sm.getPreferences("email").isEmpty() &&
                                !String.valueOf(sm.getIntPreferences("city")).isEmpty() &&
                                !sm.getPreferences("bank_name").isEmpty() &&
                                !sm.getPreferences("bank_account").isEmpty() &&
                                !sm.getPreferences("branch").isEmpty() &&
                                !sm.getPreferences("account_name").isEmpty()) {

                            accountDataVerifNotif.setVisibility(View.VISIBLE);
                            verifIco.setVisibility(View.GONE);
                        }
                    } else {
                        accountDataVerifNotif.setVisibility(View.GONE);
                        verifIco.setVisibility(View.VISIBLE);
                    }

                    if (dataObject.getInt("saldo") == 0) {
                        totSaldo.setText("0 IDR");
                    } else {
                        totSaldo.setText(PricingTools.PriceFormat(dataObject.getInt("saldo")));
                    }

                    sm.setIntPreferences("sum_car", assetObject.getInt("mobil"));
                    sm.setIntPreferences("sum_motor", assetObject.getInt("motor"));
                    sm.setIntPreferences("sum_yacht", assetObject.getInt("yacht"));
                    sm.setIntPreferences("sum_medic", assetObject.getInt("medical"));
                    sm.setIntPreferences("sum_photography", assetObject.getInt("photography"));
                    sm.setIntPreferences("sum_toys", assetObject.getInt("toys"));
                    sm.setIntPreferences("sum_adventure", assetObject.getInt("watersport"));
                    sm.setIntPreferences("sum_maternity", assetObject.getInt("maternity"));
                    sm.setIntPreferences("sum_electronic", assetObject.getInt("electronic"));
                    sm.setIntPreferences("sum_bicycle", assetObject.getInt("bicycle"));
                    sm.setIntPreferences("sum_office", assetObject.getInt("officetools"));
                    sm.setIntPreferences("sum_fashion", assetObject.getInt("fashion"));

                    JSONArray feeObject = new JSONArray(String.valueOf(dataObject.getJSONArray("percentage")));
                    JSONObject categoryCar = feeObject.getJSONObject(0);
                    JSONObject categoryMotor = feeObject.getJSONObject(1);
                    JSONObject categoryYacht = feeObject.getJSONObject(2);
                    JSONObject categoryMedic = feeObject.getJSONObject(3);
                    JSONObject categoryPhotography = feeObject.getJSONObject(4);
                    JSONObject categoryToys = feeObject.getJSONObject(5);
                    JSONObject categoryAdventure = feeObject.getJSONObject(6);
                    JSONObject categoryMaternity = feeObject.getJSONObject(7);
                    JSONObject categoryElectronic = feeObject.getJSONObject(8);
                    JSONObject categoryBicycle = feeObject.getJSONObject(9);
                    JSONObject categoryOffice = feeObject.getJSONObject(10);
                    JSONObject categoryFashion = feeObject.getJSONObject(11);

                    sm.setPreferences("fee_car", categoryCar.getString("percentage"));
                    sm.setPreferences("fee_motor", categoryMotor.getString("percentage"));
                    sm.setPreferences("fee_yacht", categoryYacht.getString("percentage"));
                    sm.setPreferences("fee_medic", categoryMedic.getString("percentage"));
                    sm.setPreferences("fee_photography", categoryPhotography.getString("percentage"));
                    sm.setPreferences("fee_toys", categoryToys.getString("percentage"));
                    sm.setPreferences("fee_adventure", categoryAdventure.getString("percentage"));
                    sm.setPreferences("fee_maternity", categoryMaternity.getString("percentage"));
                    sm.setPreferences("fee_electronic", categoryElectronic.getString("percentage"));
                    sm.setPreferences("fee_bicycle", categoryBicycle.getString("percentage"));
                    sm.setPreferences("fee_office", categoryOffice.getString("percentage"));
                    sm.setPreferences("fee_fashion", categoryFashion.getString("percentage"));

                    if (ratingObject.getInt("counts") > 0) {
                        Float clean = Float.parseFloat(ratingObject.getString("cleanliness")) / ratingObject.getInt("counts");
                        Float neat = Float.parseFloat(ratingObject.getString("neatness")) / ratingObject.getInt("counts");
                        Float honest = Float.parseFloat(ratingObject.getString("honesty")) / ratingObject.getInt("counts");
                        Float com = Float.parseFloat(ratingObject.getString("communication")) / ratingObject.getInt("counts");

                        rClean = String.format("%.1f", clean);
                        rNeat = String.format("%.1f", neat);
                        rHonest = String.format("%.1f", honest);
                        rCom = String.format("%.1f", com);

                        Float rating_sum = (clean + neat + honest + com) / 4;
                        totRating.setText(String.valueOf(rating_sum));
                    } else {
                        rClean = "0";
                        rNeat = "0";
                        rHonest = "0";
                        rCom = "0";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSON Error : " + e);
                }
            }
        }else{
            Toast.makeText(getApplicationContext(),"Gagal memuat data.", Toast.LENGTH_LONG).show();
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
            getDashboardData();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            Uri filePath = data.getData();

            try {
                //Getting the Bitmap from Gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                String imgStr = data.toString();

                String ext = imgStr.substring(imgStr.indexOf("typ")+4, imgStr.indexOf("flg")-1);
                Log.e(TAG, "ext: " + ext);

                //Setting the Bitmap to ImageView
//                rentImgProfile.setImageBitmap(bitmap);
                String isiimage = Tools.getStringImageView(bitmap, rentImgProfile);

                imgString = ext +"," + isiimage;

                new updatePhotoProfileTask(tenant, String.valueOf(sm.getIntPreferences("id"))).execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            imgString = "";
        }

        if(resultCode == RESULT_OK) {
            DashboardActivity.this.finish();
            Intent ii = new Intent(DashboardActivity.this,DashboardActivity.class);
            startActivity(ii);
        }
    }

    private class updatePhotoProfileTask extends AsyncTask<String, String, String>{
        private final String mTenant;
        private final String idUser;
        private String errorMsg, responseUser;

        private updatePhotoProfileTask(String tenant, String id) {
            mTenant = tenant;
            idUser = id;
        }

        @Override
        protected String doInBackground(String... params) {
            String URL = AppConfig.URL_DETAIL_USER + idUser;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseUser = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorMsg = error.toString();
                    Log.e(TAG, "Tenant Update Fetch Error : " + errorMsg);
                    Toast.makeText(getApplicationContext(), "Connection error, try again.",
                            Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to url
                    Map<String, String> keys = new HashMap<String, String>();
                    keys.put("id_tenant", mTenant);
                    keys.put("file", imgString);

                    return keys;
                }

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

            return responseUser;
        }

        @Override
        protected void onPostExecute(String user) {
            if(user != null){
                // set new preferences
                try {
                    JSONArray dataArray = new JSONArray(user);
                    if (dataArray.length() > 0){
                        JSONObject dataObject = dataArray.getJSONObject(0);
                        sm.setPreferences("foto_profil",dataObject.getString("profile_pic"));
                        sm.setPreferences("nama", dataObject.getString("name"));

                        DashboardActivity.this.finish();
                        Intent i = new Intent(DashboardActivity.this,DashboardActivity.class);
                        startActivity(i);
                    }
                    Log.e(TAG, "Response : " + dataArray.toString() );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Gagal merubah foto profil.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            // do some act when canceled
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_aset) {
            Intent iAset = new Intent(DashboardActivity.this, AsetActivity.class);
            startActivity(iAset);
        } else if (id == R.id.nav_feature) {
            Intent iFeature = new Intent(DashboardActivity.this, FeatureActivity.class);
            startActivity(iFeature);
        } else if (id == R.id.nav_users) {
            Intent iPegawai = new Intent(DashboardActivity.this, UsersActivity.class);
            startActivity(iPegawai);
        } else if(id == R.id.nav_driver){
            Intent iDriver = new Intent(DashboardActivity.this, DriverActivity.class);
            startActivity(iDriver);
        } else if (id == R.id.nav_dompet) {
            Intent iDompet = new Intent(DashboardActivity.this, DompetActivity.class);
            startActivity(iDompet);
        } else if (id == R.id.nav_riwayat) {
            Intent iRiwayat = new Intent(DashboardActivity.this, TransactionActivity.class);
            startActivityForResult(iRiwayat, 99);
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
            Intent i = new Intent(DashboardActivity.this, CriticSuggestionActivity.class);
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

    private void retrieveDashboardData(String tenant) {
        pBar.setVisibility(View.VISIBLE);
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
            Log.d(TAG, "Request to : "+ URL);
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
            pBar.setVisibility(View.GONE);
            Log.d(TAG, "response");

            if(user != null){
                try {
                    JSONObject dataObject = new JSONObject(user);
                    JSONObject assetObject = new JSONObject(String.valueOf(dataObject.getJSONObject("asset")));
                    JSONObject poinObject = new JSONObject(String.valueOf(dataObject.getJSONObject("poin")));
                    JSONObject ratingObject = new JSONObject(String.valueOf(dataObject.getJSONObject("rating")));
                    Log.d(TAG, "Response : " + dataObject.toString());

                    sumAsset = assetObject.getInt("total");
                    newTrans.setText(dataObject.getString("paid"));
                    totAsset.setText(String.valueOf(sumAsset));
                    totPoin.setText(poinObject.getString("received"));//.equals("null") ? "0" : ratingObject.getString("received"));
                    successRent.setText(String.valueOf(dataObject.getInt("sukses")));
                    ongoRent.setText(dataObject.getString("berlangsung"));

                    sm.setPreferences("verified", dataObject.getString("verified"));
                    if (sm.getPreferences("verified").equals("false")){
                        accountDataVerifNotif.setVisibility(View.VISIBLE);
                        verifIco.setVisibility(View.GONE);
                    } else  {
                        accountDataVerifNotif.setVisibility(View.GONE);
                        verifIco.setVisibility(View.VISIBLE);
                    }

                    totSaldo.setText(PricingTools.PriceFormat(dataObject.getInt("saldo")));

                    sm.setIntPreferences("sum_car", assetObject.getInt("mobil"));
                    sm.setIntPreferences("sum_motor", assetObject.getInt("motor"));
                    sm.setIntPreferences("sum_yacht", assetObject.getInt("yacht"));
                    sm.setIntPreferences("sum_medic", assetObject.getInt("medical"));
                    sm.setIntPreferences("sum_photography", assetObject.getInt("photography"));
                    sm.setIntPreferences("sum_toys", assetObject.getInt("toys"));
                    sm.setIntPreferences("sum_adventure", assetObject.getInt("watersport"));
                    sm.setIntPreferences("sum_maternity", assetObject.getInt("maternity"));
                    sm.setIntPreferences("sum_electronic", assetObject.getInt("electronic"));
                    sm.setIntPreferences("sum_bicycle", assetObject.getInt("bicycle"));
                    sm.setIntPreferences("sum_office", assetObject.getInt("officetools"));
                    sm.setIntPreferences("sum_fashion", assetObject.getInt("fashion"));

                    JSONArray feeObject = new JSONArray(String.valueOf(dataObject.getJSONArray("percentage")));
                    JSONObject categoryCar = feeObject.getJSONObject(0);
                    JSONObject categoryMotor = feeObject.getJSONObject(1);
                    JSONObject categoryYacht= feeObject.getJSONObject(2);
                    JSONObject categoryMedic = feeObject.getJSONObject(3);
                    JSONObject categoryPhotography = feeObject.getJSONObject(4);
                    JSONObject categoryToys = feeObject.getJSONObject(5);
                    JSONObject categoryAdventure = feeObject.getJSONObject(6);
                    JSONObject categoryMaternity = feeObject.getJSONObject(7);
                    JSONObject categoryElectronic = feeObject.getJSONObject(8);
                    JSONObject categoryBicycle = feeObject.getJSONObject(9);
                    JSONObject categoryOffice = feeObject.getJSONObject(10);
                    JSONObject categoryFashion = feeObject.getJSONObject(11);

                    sm.setPreferences("fee_car", categoryCar.getString("percentage"));
                    sm.setPreferences("fee_motor", categoryMotor.getString("percentage"));
                    sm.setPreferences("fee_yacht", categoryYacht.getString("percentage"));
                    sm.setPreferences("fee_medic", categoryMedic.getString("percentage"));
                    sm.setPreferences("fee_photography", categoryPhotography.getString("percentage"));
                    sm.setPreferences("fee_toys", categoryToys.getString("percentage"));
                    sm.setPreferences("fee_adventure", categoryAdventure.getString("percentage"));
                    sm.setPreferences("fee_maternity", categoryMaternity.getString("percentage"));
                    sm.setPreferences("fee_electronic", categoryElectronic.getString("percentage"));
                    sm.setPreferences("fee_bicycle", categoryBicycle.getString("percentage"));
                    sm.setPreferences("fee_office", categoryOffice.getString("percentage"));
                    sm.setPreferences("fee_fashion", categoryFashion.getString("percentage"));

                    if(ratingObject.getInt("counts") > 0){
                        Float clean = Float.parseFloat(ratingObject.getString("cleanliness"))/ratingObject.getInt("counts");
                        Float neat = Float.parseFloat(ratingObject.getString("neatness"))/ratingObject.getInt("counts");
                        Float honest = Float.parseFloat(ratingObject.getString("honesty"))/ratingObject.getInt("counts");
                        Float com = Float.parseFloat(ratingObject.getString("communication"))/ratingObject.getInt("counts");

                        rClean = String.format("%.1f", clean);
                        rNeat = String.format("%.1f", neat);
                        rHonest = String.format("%.1f", honest);
                        rCom = String.format("%.1f", com);

                        Float rating_sum = (clean + neat + honest + com)/4;
                        totRating.setText(String.valueOf(rating_sum));
                    }else{
                        rClean = "0";
                        rNeat = "0";
                        rHonest = "0";
                        rCom = "0";
                    }

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
            pBar.setVisibility(View.GONE);
        }
    }
}

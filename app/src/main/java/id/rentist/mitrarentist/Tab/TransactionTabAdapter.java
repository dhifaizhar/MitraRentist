package id.rentist.mitrarentist.Tab;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.fragment.TransactionAcceptFragment;
import id.rentist.mitrarentist.fragment.TransactionCanceledFragment;
import id.rentist.mitrarentist.fragment.TransactionCompletedFragment;
import id.rentist.mitrarentist.fragment.TransactionOnGoingFragment;
import id.rentist.mitrarentist.fragment.TransactionRejectFragment;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by mdhif on 07/07/2017.
 */

public class TransactionTabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles={"Diterima","Berlangsung", "Selesai", "Dibatalkan", "Ditolak"};
    private SessionManager sm;
    private ProgressDialog pDialog;
    private SpinKitView pBar;
    private AsyncTask mTransactionTask = null;
    private List<ItemTransaksiModul> mTrans;

    private String tenant, reqStatus = "";
    private String ongoData;

    private static final String TAG = "TransactionActivity";
    private static final String TOKEN = "secretissecret";

    // CHANGE STARTS HERE
    private int current_position=0;

    public void set_current_position(int i) {
        current_position = i;
    }
    // CHANGE ENDS HERE

    public TransactionTabAdapter(FragmentManager fm, Context c){
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        sm = new SessionManager(mContext);
        pDialog = new ProgressDialog(mContext);
        pDialog.setCancelable(false);
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
//
//        pDialog.setMessage("loading data...");
////        showProgress(true);
//
//        RequestQueue queue = Volley.newRequestQueue(mContext);
//        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TRANSACTION + tenant, new
//                Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        ongoData = response;
//                        reqStatus = "OK";
//
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Get Trans Fetch Error : " +  error.toString());
//                Toast.makeText(mContext, "Connection error, try again.",
//                        Toast.LENGTH_LONG).show();
//                showProgress(false);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("token", TOKEN);
//
//                return params;
//            }
//        };
//        queue.add(strReq);

            switch (position){
                case 0 :
                    frag = new TransactionAcceptFragment();
                    break;
                case 1 :
                    frag = new TransactionOnGoingFragment();
                    Bundle bundle = new Bundle();
//                    bundle.putString("data", ongoData);
                    bundle.putString("msg", "Data Accpet");
                    frag.setArguments(bundle);break;
                case 2 :
                    frag = new TransactionCompletedFragment(); break;
                case 3 :
                    frag = new TransactionCanceledFragment(); break;
                case 4 :
                    frag = new TransactionRejectFragment(); break;
            }


        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position){
        if (position == current_position) {
            return "Diterima";
        }else if(position == current_position+1){
            return "Berlangsung";
        }else if(position == current_position+2){
            return "Selesai";
        }else if(position == current_position+3){
            return "Dibatalkan";
        }else if(position == current_position+4){
            return "Ditolak";
        }

        return null;
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

    private void getTransaction() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TRANSACTION + tenant, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        transactionData(response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Trans Fetch Error : " +  error.toString());
                Toast.makeText(mContext, "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
                showProgress(false);
//                mSwipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
        queue.add(strReq);
    }

    private void transactionData(String responseJson) {
        showProgress(false);
//        mSwipeRefreshLayout.setRefreshing(false);

        String aIdTrans, aCodeTrans, aMember, aStartDate, aEndDate, aNominal, aAsetName, aThumb, aDriverName = "", errorMsg,
                aIdMember;
        Boolean aDriverIncluded;

        try {
            JSONObject jsonObject = new JSONObject(responseJson);
            JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("accepted")));
//            Toast.makeText(mContext, jsonArray.toString(),
//                    Toast.LENGTH_LONG).show();

            if(jsonArray.length() > 0){
//                noTransImage.setVisibility(View.GONE);
//                mTrans.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject transObject = jsonArray.getJSONObject(i);
                    ItemTransaksiModul itemTrans = new ItemTransaksiModul();

                    Log.e(TAG, "Accepted Data : " + String.valueOf(transObject));

                    JSONObject idTrans = transObject.getJSONObject("id_transaction");
                    JSONArray items = transObject.getJSONArray("item");
                    JSONObject memberObject = transObject.getJSONObject("id_member");
                    JSONObject item;

                    if (transObject.has("id_driver")) {
                        JSONObject driverObject = transObject.getJSONObject("id_driver");
                        aDriverName = driverObject.getString("fullname");
                    }

                    if (transObject.has("id_tenant_user")) {
                        JSONObject userObject = transObject.getJSONObject("id_tenant_user");
                        aDriverName = userObject.getString("name");
                    }

                    aIdTrans = transObject.getString("id");
                    aAsetName = "- Item Kosong -";
                    String aAsetThumb = "null";

                    if(items.length() > 0){
                        if (items.length() == 1){
                            item = items.getJSONObject(0);
                            aAsetThumb = item.getString("main_image");
                            if (item.getString("id_asset_category").equals("3")){
                                aAsetName = item.getString("type") + " " + item.getString("subtype");
                            }else {
//                                if (item.getInt("id_asset_category") == 1 ){
//                                    String driverStat = item.getString("driver_included");
//                                    aDriverIncluded = item.getInt("id_asset_category") == 1 && driverStat != "false";
//                                    itemTrans.setDriverIncluded(aDriverIncluded);
//                                }
                                aAsetName = item.getString("brand") + " " + item.getString("type");
                            }
                        }
                    }

                    JSONArray additional = transObject.getJSONArray("additional");
                    ArrayList<String> idAdditional = new ArrayList<String>();
                    if(additional.length() > 0) {
                        for (int j = 0; j < additional.length(); j++) {
                            JSONObject add = additional.getJSONObject(j);
                            JSONObject add_feature = add.getJSONObject("id_feature_item");
                            idAdditional.add(add_feature.getString("id_additional_feature"));
                        }
                    }

                    aCodeTrans = idTrans.getString("transaction_code");
                    aNominal = transObject.getString("nominal");
                    aIdMember = memberObject.getString("id");
                    aMember = memberObject.getString("firstname") + " " + memberObject.getString("lastname");
                    aThumb = memberObject.getString("profil_pic");
                    aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                    aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);

                    itemTrans.setIdTrans(aIdTrans);
                    itemTrans.setCodeTrans(aCodeTrans);
                    itemTrans.setAsetThumb(aAsetThumb);
                    itemTrans.setAsetName(aAsetName);
                    itemTrans.setIdMember(aIdMember);
                    itemTrans.setMember(aMember);
                    itemTrans.setThumbnail(aThumb);
                    itemTrans.setPrice(aNominal);
                    itemTrans.setStartDate(aStartDate);
                    itemTrans.setEndDate(aEndDate);
                    itemTrans.setDriverName(aDriverName);
                    itemTrans.setPickTime(transObject.getString("pickup_time"));
                    itemTrans.setLat(transObject.getString("latitude"));
                    itemTrans.setLong(transObject.getString("longitude"));
                    itemTrans.setAddress(transObject.getString("address"));
                    itemTrans.setNote(transObject.getString("notes"));
                    itemTrans.setIdAddtional(idAdditional.toString());

//                    mTrans.add(itemTrans);
                }


//                mRecyclerView = (RecyclerView) view.findViewById(R.id.haccept_recyclerViewFrag);
//                mLayoutManager = new LinearLayoutManager(getActivity());
//                mRecyclerView.setLayoutManager(mLayoutManager);
//                mAdapter = new TransactionAcceptAdapter(getActivity(),mTrans);
//                mRecyclerView.setAdapter(mAdapter);

            }else{
                errorMsg = "Tidak Ada Transaksi Diterima";
//                noTransImage.setVisibility(View.VISIBLE);
//                noTransText.setText(errorMsg);
            }

            reqStatus = "OK";

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

package id.rentist.mitrarentist.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.R;
import id.rentist.mitrarentist.adapter.TransactionCompleteAdapter;
import id.rentist.mitrarentist.modul.ItemTransaksiModul;
import id.rentist.mitrarentist.tools.AppConfig;
import id.rentist.mitrarentist.tools.SessionManager;

/**
 * Created by mdhif on 07/07/2017.
 */

public class TransactionCompletedFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private List<ItemTransaksiModul> mTrans;
    private AsyncTask mHistoryTask = null;
    private ProgressDialog pDialog;
    private SessionManager sm;
    private View view;
    private SpinKitView pBar;

    private LinearLayout noTransImage;
    private TextView noTransText;

    private static final String TAG = "TransactionActivity";
    private static final String TOKEN = "secretissecret";
    private String tenant;

    public TransactionCompletedFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history_comp_transaksi, container, false);
        mTrans = new ArrayList<ItemTransaksiModul>();
        sm = new SessionManager(getActivity());

        pBar = (SpinKitView) view.findViewById(R.id.progressBar);
        FadingCircle fadingCircle = new FadingCircle();
        pBar.setIndeterminateDrawable(fadingCircle);

        noTransImage = (LinearLayout) view.findViewById(R.id.no_trans);
        noTransText = (TextView) view.findViewById(R.id.no_trans_text);

        // action retrieve data history
        tenant = String.valueOf(sm.getIntPreferences("id_tenant"));
        getTransaction();

        return view;
    }

    private void getTransaction() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_TRANSACTION + tenant, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        transactionData(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Driver Fetch Error : " +  error.toString());
                Toast.makeText(getActivity(), "Connection error, try again.",
                        Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", TOKEN);

                return params;
            }
        };
//        pBar.setVisibility(View.GONE);
        queue.add(strReq);
    }

    private void transactionData(String responseJson) {
        String aIdTrans, aCodeTrans, aDriverName="" , aMember, aStartDate, aEndDate, aNominal, aAsetName, aThumb, errorMsg;
        Boolean aDriverIncluded;

        try {
            JSONObject jsonObject = new JSONObject(responseJson);
            JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.getJSONArray("completed")));
            if(jsonArray.length() > 0){
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

                    if(items.length() > 0){
                        if (items.length() == 1){
                            item = items.getJSONObject(0);
                            if (item.getString("id_asset_category").equals("3")){
                                aAsetName = item.getString("type") + " " + item.getString("subtype");
                            }else {
                                if (item.getInt("id_asset_category") == 1 ){
                                    String driverStat = item.getString("driver_included");
                                    aDriverIncluded = item.getInt("id_asset_category") == 1 && driverStat != "false";
                                    itemTrans.setDriverIncluded(aDriverIncluded);
                                }
                                aAsetName = item.getString("brand") + " " + item.getString("type");
                            }
                        } else {

                        }
                    }

                    aCodeTrans = idTrans.getString("transaction_code");
                    aNominal = transObject.getString("nominal");
                    aMember = memberObject.getString("firstname") + " " + memberObject.getString("lastname");
                    aThumb = memberObject.getString("profil_pic");
                    aStartDate = transObject.getString("start_date").replace("-","/").substring(0,10);
                    aEndDate = transObject.getString("end_date").replace("-","/").substring(0,10);

                    itemTrans.setIdTrans(aIdTrans);
                    itemTrans.setCodeTrans(aCodeTrans);
                    itemTrans.setAsetName(aAsetName);
                    itemTrans.setMember(aMember);
                    itemTrans.setThumbnail(aThumb);
                    itemTrans.setPrice(aNominal);
                    itemTrans.setStartDate(aStartDate);
                    itemTrans.setEndDate(aEndDate);
                    itemTrans.setDriverName(aDriverName);

                    mTrans.add(itemTrans);
                }

                mRecyclerView = (RecyclerView) view.findViewById(R.id.htrans_recyclerViewFrag);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new TransactionCompleteAdapter(getActivity(),mTrans);
                mRecyclerView.setAdapter(mAdapter);

            }else{
                errorMsg = "Tidak  ada Transaksi Selesai";
                noTransImage.setVisibility(View.VISIBLE);
                noTransText.setText(errorMsg);
//                      Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
            }

            Log.e(TAG, "TAB Transaction Data : " + jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
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

}

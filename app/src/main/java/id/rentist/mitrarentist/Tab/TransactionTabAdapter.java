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

import com.github.ybq.android.spinkit.SpinKitView;

import id.rentist.mitrarentist.fragment.TransactionCanceledFragment;
import id.rentist.mitrarentist.fragment.TransactionCompletedFragment;
import id.rentist.mitrarentist.fragment.TransactionOnGoingFragment;
import id.rentist.mitrarentist.fragment.TransactionAcceptFragment;
import id.rentist.mitrarentist.fragment.TransactionRejectFragment;
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

    private String tenant, acceptData;

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

        Bundle b = new Bundle();

        switch (position){
            case 0 :
                frag = new TransactionAcceptFragment();
//                b.putString("data", acceptData);
                break;
            case 1 :
                frag = new TransactionOnGoingFragment(); break;
            case 2 :
                frag = new TransactionCompletedFragment(); break;
            case 3 :
                frag = new TransactionCanceledFragment(); break;
            case 4 :
                frag = new TransactionRejectFragment(); break;
        }

        b.putInt("position", position);
        if(frag != null){
            frag.setArguments(b);
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
}

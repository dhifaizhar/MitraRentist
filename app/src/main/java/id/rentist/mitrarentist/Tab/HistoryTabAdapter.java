package id.rentist.mitrarentist.Tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import id.rentist.mitrarentist.fragment.HistoryAcceptFragment;
import id.rentist.mitrarentist.fragment.HistoryCancelFragment;
import id.rentist.mitrarentist.fragment.HistoryCompTransFragment;
import id.rentist.mitrarentist.fragment.HistoryOnTransFragment;
import id.rentist.mitrarentist.fragment.TransactionRejectFragment;

/**
 * Created by mdhif on 07/07/2017.
 */

public class HistoryTabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles={"Diterima","Berlangsung", "Selesai", "Dibatalkan", "Ditolak"};

    // CHANGE STARTS HERE
    private int current_position=0;

    public void set_current_position(int i) {
        current_position = i;
    }
    // CHANGE ENDS HERE

    public HistoryTabAdapter(FragmentManager fm, Context c){
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        switch (position){
            case 0 :
                frag = new HistoryAcceptFragment(); break;
            case 1 :
                frag = new HistoryOnTransFragment(); break;
            case 2 :
                frag = new HistoryCompTransFragment(); break;
            case 3 :
                frag = new HistoryCancelFragment(); break;
            case 4 :
                frag = new TransactionRejectFragment(); break;

        }

//        if(position == 0){
//            frag = new HistoryAcceptFragment();
//        }else if(position == 1){
//            frag = new HistoryOnTransFragment();
//        }else if(position == 2){
//            frag = new HistoryCompTransFragment();
//        }else if(position == 3){
//            frag = new HistoryCancelFragment();
//        }else if(position == 4){
//
//        }

        Bundle b = new Bundle();
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
}

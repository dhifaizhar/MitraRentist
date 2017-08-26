package id.rentist.mitrarentist.Tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import id.rentist.mitrarentist.fragment.HistoryOnTransFragment;
import id.rentist.mitrarentist.fragment.HistoryCancelFragment;
import id.rentist.mitrarentist.fragment.HistoryCompTransFragment;

/**
 * Created by mdhif on 07/07/2017.
 */

public class HistoryTabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles={"Berlangsung", "Selesai", "Dibatalkan"};

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

        if(position == 0){
            frag = new HistoryOnTransFragment();
        }else if(position == 1){
            frag = new HistoryCompTransFragment();
        }else if(position == 2){
            frag = new HistoryCancelFragment();
        }

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
            return "Berlangsung";
        }else if(position == current_position+1){
            return "Selesai";
        }else if(position == current_position+2){
            return "Dibatalkan";
        }

        return null;
    }
}

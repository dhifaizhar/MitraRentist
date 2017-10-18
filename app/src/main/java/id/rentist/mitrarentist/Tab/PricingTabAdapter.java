package id.rentist.mitrarentist.Tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import id.rentist.mitrarentist.fragment.PricingAdvanceFragment;
import id.rentist.mitrarentist.fragment.PricingBasicFragment;

public class PricingTabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles={"Basic","Advance"};

    // CHANGE STARTS HERE
    private int current_position=0;

    public void set_current_position(int i) {
        current_position = i;
    }
    // CHANGE ENDS HERE

    public PricingTabAdapter(FragmentManager fm, Context c){
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        switch (position){
            case 0 :
                frag = new PricingBasicFragment(); break;
            case 1 :
                frag = new PricingAdvanceFragment(); break;

        }

//        if(position == 0){
//            frag = new TransactionAcceptFragment();
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
            return "Basic";
        }else if(position == current_position+1) {
            return "Advance";
        }

        return null;
    }
}
